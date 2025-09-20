package com.example.scanner.config;

import com.example.shared.config.ConfigurationException;
import com.example.shared.config.IConfiguration;

import java.nio.file.Path;

public class ScannerConfig {
    private final Path pathToWatchedFolder;
    private final String nameOfDocumentQueue;
    private final Integer maximumFileSizeInMB;

    public ScannerConfig(IConfiguration config) throws ConfigurationException {
        this.pathToWatchedFolder = config.getPath("PATH_INVOICES");
        this.nameOfDocumentQueue = config.getString("INPUT_QUEUE");
        this.maximumFileSizeInMB = config.getInteger("MAX_FILE_SIZE");
    }

    public Path getPathToWatchedFolder() {
        return pathToWatchedFolder;
    }

    public String getNameOfDocumentQueue() {
        return nameOfDocumentQueue;
    }

    public Integer getMaximumFileSizeInMB() {
        return maximumFileSizeInMB;
    }
}
