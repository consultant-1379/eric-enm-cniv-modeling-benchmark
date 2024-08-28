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
package com.ericsson.oss.containerisation.modeling.benchmark.io.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.containerisation.modeling.benchmark.Result;
import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Output writer implementation to write output files in YAML format.
 */
public final class BenchmarkResultWriter {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkResultWriter.class);

    private BenchmarkResultWriter() {}

    /**
     * Write a result to a provided file.
     *
     * @param result
     *         The {@link Result} object to be written.
     * @param file
     *         The file to write the result to.
     */
    public static void write(final Result result, final File file) {
        validateOutputFile(file);
        logger.debug("Writing output to file '{}'", file);

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(file, result);
        } catch (final IOException ioException) {
            final String message = "Failed to write to the output file: " + ioException.getMessage();
            logger.error(message);
            throw new ModelingBenchmarkException(message, ioException);
        }
    }

    private static void validateOutputFile(final File file) {
        final Path parentPath = file.toPath().getParent();
        logger.debug("Validating folder '{}' exists", parentPath);

        if (!Files.exists(parentPath)) {
            final String message = "The provided output files parent directory does not exist: " + parentPath;
            throw new IllegalArgumentException(message);
        }
    }
}
