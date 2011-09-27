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
package com.qcadoo.plugins.qcadooExportToPdf.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.report.api.pdf.PdfUtil;

public final class ExportToPdfPageNumbering extends PdfPageEventHelper {

    /** The PdfTemplate that contains the total number of pages. */
    private PdfTemplate total;

    private final String page;

    private final String in;

    private final String generatedBy;

    private final String generationDate;

    public ExportToPdfPageNumbering(final String page, final String in, final String generatedBy, final String username) {
        super();
        this.page = page;
        this.in = in;

        StringBuilder footerData = new StringBuilder();
        footerData = footerData.append(generatedBy);
        footerData = footerData.append(" ");
        footerData = footerData.append(username);
        this.generatedBy = footerData.toString();

        this.generationDate = new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT).format(new Date());
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onOpenDocument(final PdfWriter writer, final Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
        try {
            PdfUtil.prepareFontsAndColors();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onStartPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onStartPage(final PdfWriter writer, final Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        String text = page + " " + writer.getPageNumber() + " " + in + " ";
        float textBase = document.top() + 22;
        float textSize = PdfUtil.getArial().getWidthPoint(text, 7);
        cb.setColorFill(PdfUtil.getLightColor());
        cb.setColorStroke(PdfUtil.getLightColor());
        cb.beginText();
        cb.setFontAndSize(PdfUtil.getArial(), 7);
        float adjust = PdfUtil.getArial().getWidthPoint("0", 7);
        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(total, document.right() - adjust, textBase);
        cb.setLineWidth(1);
        cb.setLineDash(2, 2, 1);
        cb.moveTo(document.left(), document.top() + 12);
        cb.lineTo(document.right(), document.top() + 12);
        cb.stroke();
        cb.restoreState();
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onEndPage(final PdfWriter writer, final Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        String text = page + " " + writer.getPageNumber() + " " + in + " ";
        float textBase = document.bottom() - 25;
        float textSize = PdfUtil.getArial().getWidthPoint(text, 7);
        cb.setColorFill(PdfUtil.getLightColor());
        cb.setColorStroke(PdfUtil.getLightColor());
        cb.setLineWidth(1);
        cb.setLineDash(2, 2, 1);
        cb.moveTo(document.left(), document.bottom() - 10);
        cb.lineTo(document.right(), document.bottom() - 10);
        cb.stroke();
        cb.beginText();
        cb.setFontAndSize(PdfUtil.getArial(), 7);
        float adjust = PdfUtil.getArial().getWidthPoint("0", 7);
        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);

        textSize = PdfUtil.getArial().getWidthPoint(generatedBy, 7);
        cb.setTextMatrix(document.right() - textSize, textBase - 10);
        cb.showText(generatedBy);

        textSize = PdfUtil.getArial().getWidthPoint(generationDate, 7);
        cb.setTextMatrix(document.right() - textSize, textBase - 20);
        cb.showText(generationDate);

        cb.endText();
        cb.addTemplate(total, document.right() - adjust, textBase);
        cb.restoreState();
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onCloseDocument(final PdfWriter writer, final Document document) {
        total.beginText();
        total.setFontAndSize(PdfUtil.getArial(), 7);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber() - 1));
        total.endText();
    }

}
