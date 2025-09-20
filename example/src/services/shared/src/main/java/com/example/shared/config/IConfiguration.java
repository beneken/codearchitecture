package com.example.shared.config;

import java.net.URI;
import java.nio.file.Path;

public interface IConfiguration {
    Path getPath(String envKey) throws ConfigurationException;
    Integer getPort(String envKey) throws ConfigurationException;
    URI getUri(String envKey) throws ConfigurationException;
    String getString(String envKey) throws ConfigurationException;
    Integer getInteger(String envKey) throws ConfigurationException;
}
