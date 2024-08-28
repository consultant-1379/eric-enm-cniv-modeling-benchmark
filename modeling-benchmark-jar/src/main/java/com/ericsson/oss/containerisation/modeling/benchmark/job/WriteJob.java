/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.containerisation.modeling.benchmark.job;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.containerisation.modeling.benchmark.BenchmarkProperties;
import com.ericsson.oss.containerisation.modeling.benchmark.Result;
import com.ericsson.oss.containerisation.modeling.benchmark.ThroughputFinder;
import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;
import com.ericsson.oss.containerisation.modeling.benchmark.io.util.RandomFileGenerator;

/**
 * Carries out the write benchmark for modeling use case.
 */
public class WriteJob implements BenchmarkJob {

    private static final String NR_FILES_PROPERTY = "nrFiles";
    private static final String FILE_SIZE_PROPERTY = "fileSize";
    private static final String DIRECTORY_PROPERTY = "directory";
    private static final String METRIC_TITLE = "writeThroughputKiBps";
    private static final Logger logger = LoggerFactory.getLogger(WriteJob.class);

    private final ThroughputFinder throughputFinder = new ThroughputFinder();
    private final Properties properties;

    public WriteJob(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public Result execute() {
        logger.info("Executing write benchmark test");

        final int nrFiles = getIntProperty(NR_FILES_PROPERTY);
        final int fileSize = getIntProperty(FILE_SIZE_PROPERTY);
        final String copyToDirectoryUri = getStringProperty(DIRECTORY_PROPERTY);
        final String tempDirectory = createTempDirectory();

        RandomFileGenerator.fileLayout(nrFiles, tempDirectory, fileSize);

        final long throughputRate = throughputFinder.getWriteThroughput(tempDirectory, copyToDirectoryUri);
        return new Result(METRIC_TITLE, Long.toString(throughputRate));
    }

    private String createTempDirectory() {
        final String temporaryFilesDirectoryPropertyValue = BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.getValue();
        final String tempDirRoot;
        try {
            if (temporaryFilesDirectoryPropertyValue != null) {
                final File temporaryFilesDirectory = new File(temporaryFilesDirectoryPropertyValue);
                final Path path = Files.createDirectories(Paths.get(temporaryFilesDirectory.getCanonicalPath()));
                tempDirRoot = path.toString();
            } else {
                final Path path = Files.createTempDirectory("tempFiles");
                tempDirRoot = path.toString();
            }

            logger.debug("Created temporary directory at '{}' for file layout", tempDirRoot);
            return tempDirRoot;
        } catch (final IOException ioException) {
            final String errorMessage = String.format("Error while creating temporary directory for write job: %s", ioException.getMessage());
            logger.error(errorMessage);
            throw new ModelingBenchmarkException(errorMessage, ioException);
        }
    }

    protected String getStringProperty(final String propName) {
        final String property = properties.getProperty(propName);
        if (property == null) {
            final String message = String.format("Required property: '%s' is not present in the supplied properties.", propName);
            logger.error(message);
            throw new ModelingBenchmarkException(message);
        }

        logger.debug("Value found '{}' for property '{}'", property, propName);
        return property;
    }

    protected int getIntProperty(final String propName) {
        final String property = getStringProperty(propName);
        try {
            final int intProperty = Integer.parseInt(property);
            if (intProperty <= 0) {
                final String message = String.format("Required property: '%s' must be a positive integer. Supplied %d", propName, intProperty);
                logger.error(message);
                throw new ModelingBenchmarkException(message);
            }
            return intProperty;
        } catch (final NumberFormatException numberFormatException) {
            final String message = String.format("Required property: '%s' must be an integer. Supplied %s", propName, property);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
