/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 0.4.8
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.model.internal.classconverter;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.qcadoo.model.Utils;
import com.qcadoo.model.internal.utils.ClassNameUtils;

public class ModelXmlToClassConverterTest {

    private final static ModelXmlToClassConverterImpl modelXmlToClassConverter = new ModelXmlToClassConverterImpl();

    private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

    private static Map<String, PropertyDescriptor> propertyDescriptors = new HashMap<String, PropertyDescriptor>();

    @BeforeClass
    public static void init() throws Exception {
        modelXmlToClassConverter.setBeanClassLoader(ClassLoader.getSystemClassLoader());

        for (Class<?> clazz : modelXmlToClassConverter.convert(Utils.FULL_FIRST_ENTITY_XML_RESOURCE,
                Utils.FULL_SECOND_ENTITY_XML_RESOURCE, Utils.FULL_THIRD_ENTITY_XML_RESOURCE,
                Utils.OTHER_FIRST_ENTITY_XML_RESOURCE, Utils.OTHER_SECOND_ENTITY_XML_RESOURCE)) {
            classes.put(clazz.getCanonicalName(), clazz);
        }

        for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(classes.get(ClassNameUtils
                .getFullyQualifiedClassName("full", "firstEntity")))) {
            propertyDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
        }
    }

    @Test
    public void shouldDefineClasses() throws Exception {
        assertEquals(5, classes.size());
    }

    @Test
    public void shouldHaveProperName() throws Exception {
        assertNotNull(classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")));
        assertNotNull(classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "secondEntity")));
        assertNotNull(classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "thirdEntity")));
        assertNotNull(classes.get(ClassNameUtils.getFullyQualifiedClassName("other", "firstEntity")));
        assertNotNull(classes.get(ClassNameUtils.getFullyQualifiedClassName("other", "secondEntity")));
    }

    @Test
    public void shouldDefineIdentifier() {
        verifyField(propertyDescriptors.get("id"), Long.class);
    }

    @Test
    public void shouldDefineSimpleFields() throws Exception {
        verifyField(propertyDescriptors.get("fieldInteger"), Integer.class);
        verifyField(propertyDescriptors.get("fieldString"), String.class);
        verifyField(propertyDescriptors.get("fieldText"), String.class);
        verifyField(propertyDescriptors.get("fieldDecimal"), BigDecimal.class);
        verifyField(propertyDescriptors.get("fieldDatetime"), Date.class);
        verifyField(propertyDescriptors.get("fieldDate"), Date.class);
        verifyField(propertyDescriptors.get("fieldBoolean"), Boolean.class);
        verifyField(propertyDescriptors.get("fieldDictionary"), String.class);
        verifyField(propertyDescriptors.get("fieldOtherDictionary"), String.class);
        verifyField(propertyDescriptors.get("fieldEnum"), String.class);
        verifyField(propertyDescriptors.get("fieldPassword"), String.class);
        verifyField(propertyDescriptors.get("fieldPriority"), Integer.class);
    }

    @Test
    public void shouldDefineActivableField() throws Exception {
        verifyField(propertyDescriptors.get("active"), Boolean.class);
    }

    @Test
    public void shouldDefineBelongsToFields() throws Exception {
        verifyField(propertyDescriptors.get("fieldSecondEntity"),
                classes.get(ClassNameUtils.getFullyQualifiedClassName("other", "secondEntity")));
        verifyField(propertyDescriptors.get("fieldSecondEntity2"),
                classes.get(ClassNameUtils.getFullyQualifiedClassName("other", "secondEntity")));
    }

    @Test
    public void shouldDefineHasManyFields() throws Exception {
        verifyField(propertyDescriptors.get("fieldTree"), Set.class);
        verifyField(propertyDescriptors.get("fieldHasMany"), Set.class);
    }

    @Test
    public void shouldHaveToStringMethod() throws Exception {
        // given
        Object entity = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")).newInstance();

        BeanUtils.setProperty(entity, "fieldInteger", 13);
        BeanUtils.setProperty(entity, "fieldString", "Xxx");

        // when
        String string = entity.toString();

        // then
        assertTrue(string.contains("fieldInteger=13"));
        assertTrue(string.contains("fieldString=Xxx"));
        assertTrue(string.contains(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")));
    }

    @Test
    public void shouldHaveHashCodeMethod() throws Exception {
        Object entity1 = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")).newInstance();
        Object entity2 = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")).newInstance();

        BeanUtils.setProperty(entity1, "fieldInteger", 13);
        BeanUtils.setProperty(entity2, "fieldInteger", 13);
        BeanUtils.setProperty(entity1, "fieldString", "Xxx");
        BeanUtils.setProperty(entity2, "fieldString", "Xxx");

        assertTrue(entity1.hashCode() == entity1.hashCode());
        assertTrue(entity1.hashCode() == entity2.hashCode());

        BeanUtils.setProperty(entity2, "fieldString", "Xxz");

        assertFalse(entity1.hashCode() == entity2.hashCode());
    }

    @Test
    public void shouldHaveEqualsMethod() throws Exception {
        Object entity1 = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")).newInstance();
        Object entity2 = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "firstEntity")).newInstance();
        Object entity3 = classes.get(ClassNameUtils.getFullyQualifiedClassName("full", "secondEntity")).newInstance();

        BeanUtils.setProperty(entity1, "fieldInteger", 13);
        BeanUtils.setProperty(entity2, "fieldInteger", 13);
        BeanUtils.setProperty(entity1, "fieldString", "Xxx");
        BeanUtils.setProperty(entity2, "fieldString", "Xxx");

        assertTrue(entity1.equals(entity1));
        assertTrue(entity1.equals(entity2));
        assertTrue(entity2.equals(entity1));

        BeanUtils.setProperty(entity2, "fieldString", "Xxz");

        assertFalse(entity1.equals(entity2));
        assertFalse(entity2.equals(entity1));
        assertFalse(entity1.equals(null));
        assertFalse(entity1.equals(entity3));
    }

    private void verifyField(final PropertyDescriptor propertyDescriptor, final Class<?> type) {
        verifyField(propertyDescriptor, type, true, true);
    }

    private void verifyField(final PropertyDescriptor propertyDescriptor, final Class<?> type, final boolean readable,
            final boolean writable) {
        assertEquals(type, propertyDescriptor.getPropertyType());
        if (writable) {
            assertNotNull(propertyDescriptor.getWriteMethod());
        } else {
            assertNull(propertyDescriptor.getWriteMethod());
        }
        if (readable) {
            assertNotNull(propertyDescriptor.getReadMethod());
        } else {
            assertNull(propertyDescriptor.getReadMethod());
        }
    }
}
