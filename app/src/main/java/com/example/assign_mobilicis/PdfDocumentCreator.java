package com.example.assign_mobilicis;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;

public class PdfDocumentCreator {
    private PdfDocument pdfDocument;
    private Document document;

    public PdfDocumentCreator(String filePath) {
        try {
            pdfDocument = new PdfDocument(new PdfWriter(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        document = new Document(pdfDocument);
    }

    public void addToPdfReport(String testName, String result) {
        if (document != null) {
            document.add(new Paragraph(testName + ": " + result));
        }
    }

    public void generatePdfReport() {
        if (document != null) {
            document.close();
        }
    }
}
