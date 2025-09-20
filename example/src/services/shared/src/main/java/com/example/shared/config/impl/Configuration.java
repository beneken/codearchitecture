package com.example.shared.config.impl;

import com.example.shared.config.ConfigurationException;
import com.example.shared.config.IConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration implements IConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final String ENV_VAR_EMPTY_ERROR = "Environment variable %s not set or empty.";
    private static final String ENV_VAR_PATH_ERROR = "Environment variable %s is not a directory.";
    private static final String INVALID_PORT_ERROR = "Port %s not valid: %s";

    @Override
    public Path getPath(String envKey) throws ConfigurationException {
        String value = getMandatoryEnv(envKey);

        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            logger.error("Path does not exist {}", value);
            throw new ConfigurationException(String.format(ENV_VAR_PATH_ERROR, envKey));
        }
        if (!Files.isDirectory(path)) {
            logger.error("Path is not a directory {}", value);
            throw new ConfigurationException(String.format(ENV_VAR_PATH_ERROR, envKey));
        }

        return path;
    }

    @Override
    public Integer getPort(String envKey) throws ConfigurationException {
        Integer port = getInteger(envKey);

        if (port <= 0 || port > 65535) {
            logger.error("port number {} is not between 0 and 65535 ", port);
            throw new ConfigurationException(String.format(INVALID_PORT_ERROR, envKey, port));
        }
        return port;
    }

    @Override
    public URI getUri(String envKey) throws ConfigurationException {
        String value = getMandatoryEnv(envKey);
        try {
            return new URI(value);
        } catch (Exception e) {
            logger.error("environment variable {} has value {} not a valid URI", envKey, value);
            throw new ConfigurationException(String.format("Environment variable %s is not a valid URI.", envKey));
        }
    }

    @Override
    public String getString(String envKey) throws ConfigurationException {
        return getMandatoryEnv(envKey);
    }

    @Override
    public Integer getInteger(String envKey) throws ConfigurationException {
        String value = getMandatoryEnv(envKey);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("environment variable {} has value {} not an integer", envKey, value);
            throw new ConfigurationException(String.format(INVALID_PORT_ERROR, envKey, value));
        }
    }

    protected String getEnv(String key) {
        return System.getenv(key);
    }

    protected String getMandatoryEnv(String key) throws ConfigurationException {
        String value = getEnv(key);
        if (value == null || value.isEmpty()) {
            throw new ConfigurationException(String.format(ENV_VAR_EMPTY_ERROR, key));
        }
        return value;
    }
}