package com.example.scanner.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class PDFLoader {
    private static final Logger logger = LoggerFactory.getLogger(PDFLoader.class);

    public Document loadDocument(Path path) {
        try (PDDocument doc = Loader.loadPDF(path.toFile())) {
            validateDocument(doc, path);

            var text = extractText(doc);
            var version = doc.getVersion();
            var fileName = path.getFileName().toString();
            var metadata = extractMetadata(doc, fileName);

            return new Document(version, text, fileName, metadata);
        } catch (IOException e) {
            logger.error("Error while parsing the PDF file: {}", path, e);
            return null;
        }
    }

    private void validateDocument(PDDocument doc, Path path) throws IOException {
        if (doc.isEncrypted()) {
            logger.warn("PDF is encrypted: {}", path);
            throw new IOException("Encrypted PDF files are not supported.");
        }
    }

    private String extractText(PDDocument doc) throws IOException {
        return new PDFTextStripper().getText(doc);
    }

    private HashMap<String, String> extractMetadata(PDDocument doc, String fileName) {
        var metaData = doc.getDocumentInformation();
        var map = new HashMap<String, String>();

        if (metaData != null) {
            addMetadataField(map, "title", metaData.getTitle(), fileName);
            addMetadataField(map, "author", metaData.getAuthor(), fileName);
            addMetadataField(map, "keywords", metaData.getKeywords(), fileName);
            addMetadataField(map, "creationDate", metaData.getCreationDate() != null ? metaData.getCreationDate().toString() : null, fileName);
            addMetadataField(map, "modificationDate", metaData.getModificationDate() != null ? metaData.getModificationDate().toString() : null, fileName);
        } else {
            logger.warn("No metadata found for file: {}", fileName);
        }

        return map;
    }

    private void addMetadataField(HashMap<String, String> map, String key, String value, String fileName) {
        if (value != null) {
            map.put(key, value);
        } else {
            logger.warn("No metadata for {} found in file: {}", key, fileName);
        }
    }

}