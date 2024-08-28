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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;

/**
 * Calculates the throughput.
 */
public class ThroughputFinder {

    private static final Logger logger = LoggerFactory.getLogger(ThroughputFinder.class);
    private final StopWatch stopWatch = new StopWatch();

    /**
     * Calculates and returns the write throughput in KiB/s of the filesystem based on the provided configuration.
     *
     * @param copyFromDirectoryUri
     *          Uri of the directory in which the files to be copied are located.
     * @param copyToDirectoryUri
     *          Uri of the directory in which the files will be copied to.
     * @return The calculated write throughput in KiB/s.
     */
    public long getWriteThroughput(final String copyFromDirectoryUri, final String copyToDirectoryUri) {
        logger.debug("Finding write throughput by copying from directory '{}' to '{}'", copyFromDirectoryUri, copyToDirectoryUri);

        try {
            final File dirToCopyFrom = new File(copyFromDirectoryUri);
            final File dirToCopyTo = new File(copyToDirectoryUri);

            stopWatch.start();
            final long bytesCopied = copyFilesToDir(dirToCopyFrom, dirToCopyTo);
            stopWatch.stop();
            final long timeTakenInMilliseconds = stopWatch.getTime(TimeUnit.MILLISECONDS);

            final long sizeCopiedInKiB = bytesCopied / 1024L;
            logger.debug("Copied {}KiB in {} ms", sizeCopiedInKiB, timeTakenInMilliseconds);

            validateThroughputOutcome(sizeCopiedInKiB, timeTakenInMilliseconds);
            final long throughput = (sizeCopiedInKiB * 1000L) / timeTakenInMilliseconds;

            logger.info("Computed write throughput: {}KiB/s", throughput);
            return throughput;
        } finally {
            stopWatch.reset();
        }
    }

    /**
     * Copies all the files from one directory to another.
     *
     * @param directoryToCopyFrom
     *         The directory containing all the files to be copied.
     * @param directoryToCopyTo
     *         The directory to copy all the files to.
     *
     * @return The number of bytes copied.
     * @throws IllegalArgumentException
     *         if one or both of the supplied files is not a directory
     */
    private long copyFilesToDir(final File directoryToCopyFrom, final File directoryToCopyTo) {
        if (!directoryToCopyFrom.isDirectory() || !directoryToCopyTo.isDirectory()) {
            final String message = "Invalid directory/directories provided: '" + directoryToCopyFrom + "', '" + directoryToCopyTo + "'";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        long totalBytesCopied = 0L;
        final File[] files = directoryToCopyFrom.listFiles();

        if (files != null) {
            for (final File file : files) {
                try {
                    if (file.isDirectory()) {
                        totalBytesCopied += copyFilesToDir(file, directoryToCopyTo);
                    } else {
                        final Path fileToCopy = Paths.get(file.getAbsolutePath());
                        final Path targetFile = Paths.get(directoryToCopyTo.getAbsolutePath() + File.separator + file.getName());
                        Files.copy(fileToCopy, targetFile);
                        totalBytesCopied += Files.size(fileToCopy);
                    }
                } catch (final IOException ioException) {
                    final String message = "Error while copying file: " + ioException.getMessage();
                    logger.error(message);
                    throw new ModelingBenchmarkException(message, ioException);
                }
            }
        }
        return totalBytesCopied;
    }

    private void validateThroughputOutcome(final long kbProcessed, final long timeTakenInMilliseconds) {
        if (kbProcessed <= 0L || timeTakenInMilliseconds <= 0L) {
            final String errorMessage = String.format("Invalid result. KB-Processed: %d. TimeTakenInMilliseconds %d. Check Configuration",
                    kbProcessed, timeTakenInMilliseconds);
            logger.error(errorMessage);
            throw new ModelingBenchmarkException(errorMessage);
        }
    }
}