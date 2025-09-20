package com.example.scanner.services;

import com.example.scanner.pdf.Document;
import com.example.scanner.pdf.PDFLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;

public class DirectoryScanner {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryScanner.class);
    private static volatile boolean running = true;
    private final Path pathToWatchedFolder;
    private final Integer maxFileSizeInMB;

    private final BlockingQueue<String> newDocuments;

    private WatchService watcher;

    public DirectoryScanner(Path pathToWatchedFolder, Integer maxFileSize,
                            BlockingQueue<String> newDocuments) {
        this.pathToWatchedFolder = pathToWatchedFolder;
        this.maxFileSizeInMB = maxFileSize;
        this.newDocuments = newDocuments;
    }

    public void processNewFile(Path filePath) {
        try {
            if (filePath.toString().endsWith(".pdf")) {
                logger.info("New PDF file detected: {}", filePath.getFileName());
                String json = parseFile(filePath);
                newDocuments.put(json);
            } else {
                logger.info("New file ignored: {}", filePath.getFileName());
            }
        }
        catch(Exception e) {
            logger.error("Error processing new file: {}", filePath.getFileName(), e);
        }
    }

    private boolean checkFileSize(Path filePath) {
        try {
            long fileSizeInBytes = Files.size(filePath);
            if (fileSizeInBytes > maxFileSizeInMB * 1024 * 1024) {
                logger.warn("File {} exceeds the maximum size: {} bytes", filePath.getFileName(), maxFileSizeInMB);
                return false;
            }
        } catch (IOException e) {
            logger.error("Error checking the file size", e);
            return false;
        }
        return true;
    }

    public String parseFile(Path filePath) {
        checkFileSize(filePath);
        PDFLoader parser = new PDFLoader();
        Document result = parser.loadDocument(filePath);

        if (result != null) {
           String json = result.toJsonString();
           logger.info("File parsed and JSON added to queue: {}", filePath.getFileName());
           return json;
        } else {
            logger.warn("Conversion to JSON resulted in null for file: {}", filePath.getFileName());
            return null;
        }
    }

    public void processWatchKeyEvents(WatchKey key, Path path) {
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            Path filePath = path.resolve((Path) event.context());

            if (kind == StandardWatchEventKinds.ENTRY_CREATE && filePath.toString().endsWith(".pdf")) {
                processNewFile(filePath);
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE && filePath.toString().endsWith(".pdf")) {
                logger.info("PDF file removed: {}", filePath.getFileName());
            }
        }
    }

    public WatchService startMonitoring() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
            pathToWatchedFolder.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            logger.info("Monitoring started for directory: {}", pathToWatchedFolder);
            return watcher;
        }
        catch (IOException e) {
            logger.error("Error while monitoring the directory", e);
        }
        return null;
    }

    public void pollWatchService() {
        WatchKey key = watcher.poll();
        if (key != null) {
            processWatchKeyEvents(key, pathToWatchedFolder);
            key.reset();
        }
    }
}