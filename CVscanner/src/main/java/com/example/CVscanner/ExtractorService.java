package com.example.CVscanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
public class ExtractorService {
    public static String extractText(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return extractPdfText(file);
        } else if (fileName.endsWith(".docx")) {
            return extractDocxText(file);
        } else if (fileName.endsWith(".txt")) {
            return extractTxtText(file);
        } else {
            throw new IOException("Unsupported file type: " + fileName);
        }
    }

    private static String extractPdfText(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractDocxText(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder sb = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                sb.append(para.getText()).append("\n");
            }
            return sb.toString();
        }
    }

    private static String extractTxtText(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
