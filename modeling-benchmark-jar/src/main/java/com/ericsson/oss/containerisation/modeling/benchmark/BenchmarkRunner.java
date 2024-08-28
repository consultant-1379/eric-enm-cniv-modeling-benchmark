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
package com.ericsson.oss.containerisation.modeling.benchmark;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;
import com.ericsson.oss.containerisation.modeling.benchmark.io.util.BenchmarkResultWriter;
import com.ericsson.oss.containerisation.modeling.benchmark.job.WriteJob;

/**
 * Main class that runs the modeling benchmarks.
 */
public final class BenchmarkRunner {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunner.class);

    private BenchmarkRunner() {}

    /**
     * Main method to execute benchmark.
     *
     * @param args
     *         input args should be the location of the properties file followed by the output file destination.
     */
    public static void main(final String[] args) {
        logger.info("Starting benchmark process");

        final String propertiesFileLocation = BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.getValue();
        final String outputFileDestination = BenchmarkProperties.RESULT_FILE_DESTINATION.getValue();
        validateInputFile(propertiesFileLocation);

        final Properties properties = loadPropertiesFromFile(propertiesFileLocation);
        final Result result =  new WriteJob(properties).execute();

        BenchmarkResultWriter.write(result, new File(outputFileDestination));
        logger.info("Benchmark process complete");
    }

    private static Properties loadPropertiesFromFile(final String propertiesUri) {
        logger.debug("Loading input properties from '{}'", propertiesUri);

        final Properties properties = new Properties();
        try (final InputStream input = Files.newInputStream(Paths.get(propertiesUri))) {
            properties.load(input);
        } catch (final IOException ioException) {
            final String message = "Error loading properties from file: " + ioException.getMessage();
            logger.error(message);
            throw new ModelingBenchmarkException(message, ioException);
        }
        return properties;
    }

    private static void validateInputFile(final String inputFilePath) {
        logger.debug("Validating input properties file exists at: '{}'", inputFilePath);

        try {
            final Path propertiesFilePath = Paths.get(inputFilePath);
            if (!Files.exists(propertiesFilePath)) {
                final String message = "Provided properties file '" + propertiesFilePath + "' does not exist.";
                throw new IllegalArgumentException(message);
            }
        } catch (final InvalidPathException invalidPathException) {
            final String message = "Provided properties file path '" + inputFilePath + "' is not a valid path.";
            throw new IllegalArgumentException(message);
        }
    }
}