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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;

/**
 * Utility to layout randomly generated files of specified size.
 */
public final class RandomFileGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RandomFileGenerator.class);
    private static final Random RANDOM_BYTES = new Random();

    private RandomFileGenerator() {}

    /**
     * Generates a given number of random files.
     *
     * @param numFiles
     *         number of files to create.
     * @param uri
     *         path of directory in which to create the files.
     * @param fileSize
     *         size of each file in KiB.
     * @return File object of the directory containing the generated files
     */
    public static File fileLayout(final int numFiles, final String uri, final int fileSize) {
        final File directory = new File(uri);
        validateDirectory(directory);

        final String prefix = RandomStringUtils.randomAlphanumeric(8);
        final Path targetDir = Paths.get(uri + File.separator + prefix);
        try {
            Files.createDirectory(targetDir);
            for (int i = 0; i < numFiles; i++) {
                generateFile(fileSize, targetDir.toFile(), i);
            }
            logger.info("File layout complete. Layout directory: '{}'", targetDir);
            return targetDir.toFile();
        } catch (final AccessDeniedException accessDeniedException) {
            final String message = "Failed to layout files - Access denied: " + accessDeniedException.getMessage();
            logger.error(message);
            throw new ModelingBenchmarkException(message, accessDeniedException);
        } catch (final IOException ioException) {
            final String message = "Failed to layout files: " + ioException.getMessage();
            logger.error(message);
            throw new ModelingBenchmarkException(message, ioException);
        }
    }

    /**
     * Generates a file of the given size in KiB with random content. File name will follow format of model-&lt;fileNumber&gt;.xml
     *
     * @param fileSize
     *         size of file in KiB to be created.
     * @param parentDirectory
     *         directory that the file will be created in.
     * @param fileNumber
     *         file number to be used in generating file name
     */
    private static void generateFile(final int fileSize, final File parentDirectory, final int fileNumber) {
        validateDirectory(parentDirectory);

        final String name = String.format("model-%d.xml", fileNumber);
        final File file = new File(parentDirectory, name);

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            final byte[] bytes = new byte[fileSize * 1024];
            RANDOM_BYTES.nextBytes(bytes);
            fos.write(bytes);
        } catch (final IOException ioException) {
            final String message = "Error while writing to file: " + ioException.getMessage();
            logger.error(message);
            throw new ModelingBenchmarkException(message, ioException);
        }
    }

    private static void validateDirectory(final File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            final String message = "Invalid directory provided: " + directory.getPath();
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
