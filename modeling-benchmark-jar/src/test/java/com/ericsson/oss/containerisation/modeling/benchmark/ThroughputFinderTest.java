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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;
import com.ericsson.oss.containerisation.modeling.benchmark.io.util.RandomFileGenerator;

@ExtendWith(MockitoExtension.class)
class ThroughputFinderTest {

    private static final int NUMBER_OF_FILES = 150;
    private static final int FILE_SIZE = 20;
    private static final int ZERO = 0;

    @TempDir
    private File testDirectory;
    private String testDirLocation;
    @TempDir
    private File testCopyToDirectory;
    private String testCopyToDirLocation;
    private ThroughputFinder throughputFinder;

    @BeforeEach
    void setup() {
        throughputFinder = new ThroughputFinder();
        testDirLocation = testDirectory.getAbsolutePath();
        testCopyToDirLocation = testCopyToDirectory.getAbsolutePath();
    }

    @Test
    void GIVEN_directoryOfOneHundredFiftyFiles_WHEN_getWriteThroughput_THEN_validResultReturned() {
        RandomFileGenerator.fileLayout(NUMBER_OF_FILES, testDirLocation, FILE_SIZE);

        final long achievedResult = throughputFinder.getWriteThroughput(testDirLocation, testCopyToDirLocation);
        assertTrue(achievedResult > 0L);
    }

    @Test
    void GIVEN_directoryOfZeroFiles_WHEN_getWriteThroughput_THEN_noFilesCopiedExceptionThrown() {
        RandomFileGenerator.fileLayout(ZERO, testDirLocation, ZERO);

        final Exception exception = assertThrows(ModelingBenchmarkException.class, () -> throughputFinder.getWriteThroughput(testDirLocation, testCopyToDirLocation));
        assertTrue(exception.getMessage().contains("Invalid result."));
    }
}
