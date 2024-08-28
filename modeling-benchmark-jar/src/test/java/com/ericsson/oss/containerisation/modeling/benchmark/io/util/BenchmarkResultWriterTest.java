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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.ericsson.oss.containerisation.modeling.benchmark.Result;

class BenchmarkResultWriterTest {

    private static final Result result = new Result("Metric", "1000");

    @TempDir
    private File testDir;
    private File outputDirectory;
    private File outputFile;

    @BeforeEach
    void setup() {
        outputDirectory = new File(testDir.getAbsolutePath() + "/" + "output");
        outputFile = new File(outputDirectory.getAbsolutePath() + "/" + "result.yaml");
    }

    @Test
    void GIVEN_outputDirectoryExists_WHEN_writingOutputFile_THEN_resultFileWrittenCorrectly() {
        assertTrue(outputDirectory.mkdir());
        assertFalse(outputFile.exists());

        BenchmarkResultWriter.write(result, outputFile);
        assertTrue(outputFile.exists());
    }

    @Test
    void GIVEN_outputDirectoryDoesNotExists_WHEN_writingOutputFile_THEN_resultFileCannotBeWrittenExceptionThrown() {
        assertFalse(outputDirectory.exists());

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> BenchmarkResultWriter.write(result, outputFile));
        assertTrue(exception.getMessage().contains("The provided output files parent directory does not exist"));
    }
}
