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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ericsson.oss.containerisation.modeling.benchmark.BenchmarkProperties;
import com.ericsson.oss.containerisation.modeling.benchmark.Result;
import com.ericsson.oss.containerisation.modeling.benchmark.ThroughputFinder;
import com.ericsson.oss.containerisation.modeling.benchmark.exception.ModelingBenchmarkException;

@ExtendWith(MockitoExtension.class)
class WriteJobTest {

    private static final long MOCK_RESULT = 10L;
    private static final Integer TEST_NUMBER_FILES = 10;
    private static final Integer TEST_FILE_SIZE = 20;
    private static final String TEST_JOB_TYPE = "WRITE";

    @Mock
    private ThroughputFinder throughputFinder;

    @TempDir
    private File testTempDirectory;
    private String testTempDirLocation;

    @BeforeEach
    void setup() {
        testTempDirLocation = testTempDirectory.getAbsolutePath();
        BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.setValue(testTempDirLocation);
    }

    @AfterEach
    void teardown() {
        System.clearProperty(BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.getName());
    }

    @Test
    void GIVEN_validConfig_WHEN_executeWriteJob_THEN_copyFromDirectoryPopulatedAndPositiveResultReturned()
            throws NoSuchFieldException, IllegalAccessException {
        final Properties testProperties = setupProperties(TEST_NUMBER_FILES.toString(), TEST_FILE_SIZE.toString(), testTempDirLocation, TEST_JOB_TYPE);
        final WriteJob writeJob = new WriteJob(testProperties);

        when(throughputFinder.getWriteThroughput(testTempDirLocation, testTempDirLocation)).thenReturn(MOCK_RESULT);
        overrideThroughputFinderField(writeJob);

        final Result result = writeJob.execute();

        assertEquals("writeThroughputKiBps", result.getName());
        assertEquals(MOCK_RESULT, Integer.parseInt(result.getAchievedResult()));
    }

    @Test
    void GIVEN_incorrectDataTypeForNrFiles_WHEN_executeWriteJob_THEN_exceptionThrown() {
        final Properties testProperties = setupProperties("string content", TEST_FILE_SIZE.toString(), testTempDirLocation, TEST_JOB_TYPE);
        final BenchmarkJob writeJob = new WriteJob(testProperties);

        assertThrows(IllegalArgumentException.class, writeJob::execute);
    }

    @Test
    void GIVEN_incorrectDataTypeForFileSize_WHEN_executeWriteJob_THEN_exceptionThrown() {
        final Properties testProperties = setupProperties(TEST_NUMBER_FILES.toString(), "string content", testTempDirLocation, TEST_JOB_TYPE);
        final BenchmarkJob writeJob = new WriteJob(testProperties);

        assertThrows(IllegalArgumentException.class, writeJob::execute);
    }

    @Test
    void GIVEN_invalidValueForNrFiles_WHEN_executeWriteJob_THEN_exceptionThrown() {
        final Properties testProperties = setupProperties("0", TEST_FILE_SIZE.toString(), testTempDirLocation, TEST_JOB_TYPE);
        final BenchmarkJob writeJob = new WriteJob(testProperties);

        assertThrows(ModelingBenchmarkException.class, writeJob::execute);
    }

    @Test
    void GIVEN_invalidValueForFileSize_WHEN_executeWriteJob_THEN_exceptionThrown() {
        final Properties testProperties = setupProperties(TEST_NUMBER_FILES.toString(), "0", testTempDirLocation, TEST_JOB_TYPE);
        final BenchmarkJob writeJob = new WriteJob(testProperties);

        assertThrows(ModelingBenchmarkException.class, writeJob::execute);
    }

    private Properties setupProperties(final String nrFiles, final String fileSize, final String directory, final String type) {
        final Properties testProperties = new Properties();
        testProperties.setProperty("nrFiles", nrFiles);
        testProperties.setProperty("fileSize", fileSize);
        testProperties.setProperty("directory", directory);
        testProperties.setProperty("type", type);
        return testProperties;
    }

    private void overrideThroughputFinderField(final WriteJob writeJob) throws NoSuchFieldException, IllegalAccessException {
        final Field throughputFinderField = WriteJob.class.getDeclaredField("throughputFinder");
        throughputFinderField.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(throughputFinderField, throughputFinderField.getModifiers() & ~Modifier.FINAL);
        throughputFinderField.set(writeJob, throughputFinder);
    }
}
