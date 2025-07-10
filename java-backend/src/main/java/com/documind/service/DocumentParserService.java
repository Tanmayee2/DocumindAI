package com.documind.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DocumentParserService {
    
    public String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }
        
        if (fileName.endsWith(".pdf")) {
            return extractFromPdf(file.getInputStream());
        } else if (fileName.endsWith(".docx")) {
            return extractFromDocx(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file type. Only PDF and DOCX are supported.");
        }
    }
    
    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }
}