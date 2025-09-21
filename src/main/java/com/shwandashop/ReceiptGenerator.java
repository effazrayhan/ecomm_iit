package com.shwandashop;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ReceiptGenerator {
    public static String generateReceipt(Receipt receipt) {
        StringBuilder sb = new StringBuilder();
        sb.append("----- RECEIPT -----\n");
        sb.append("Order ID: ").append(receipt.getOrderId()).append("\n");
        sb.append("Customer: ").append(receipt.getCustomerName()).append("\n");
        sb.append("Date: ").append(receipt.getDate()).append("\n");
        sb.append("Items: ").append(receipt.getItems()).append("\n");
        sb.append("Total: $").append(String.format("%.2f", receipt.getTotalAmount())).append("\n");
        sb.append("-------------------\n");
        return sb.toString();
    }

    public static void generateReceiptPdf(Receipt receipt, String filePath) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        PDFont font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        contentStream.setFont(font, 12);
        contentStream.beginText();
        contentStream.setLeading(18f);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("----- RECEIPT -----");
        contentStream.newLine();
        contentStream.showText("Order ID: " + receipt.getOrderId());
        contentStream.newLine();
        contentStream.showText("Customer: " + receipt.getCustomerName());
        contentStream.newLine();
        contentStream.showText("Date: " + receipt.getDate());
        contentStream.newLine();
        contentStream.showText("Items: " + receipt.getItems());
        contentStream.newLine();
        contentStream.showText("Total: $" + String.format("%.2f", receipt.getTotalAmount()));
        contentStream.newLine();
        contentStream.showText("-------------------");
        contentStream.endText();
        contentStream.close();
        document.save(filePath);
        document.close();
    }
}
