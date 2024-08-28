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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * End to end integration tests for the modeling benchmark.
 */
class ModelingBenchmarkTest {

    private static final String testDirectory = "target/int-tests";

    private static final String EXPECTED_WRITE_METRIC_NAME = "writeThroughputKiBps";
    private static final String writeOutputLocation = testDirectory + "/write_output.yaml";

    @BeforeEach
    void setup() {
        final File testDir = new File(testDirectory);
        assertTrue(testDir.mkdirs());
    }

    @AfterEach
    void teardown() throws IOException {
        final File testDir = new File(testDirectory);
        FileUtils.deleteDirectory(testDir);

        System.clearProperty(BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.getName());
        System.clearProperty(BenchmarkProperties.RESULT_FILE_DESTINATION.getName());
    }

    @Test
    void GIVEN_validWriteJobFile_WHEN_benchmarkRan_THEN_correctOutputFileCreated() throws IOException {
        final String jobFilePath = createTestJobFile("write", testDirectory, "150", "20", "test_jobfile.properties");
        BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.setValue(jobFilePath);
        BenchmarkProperties.RESULT_FILE_DESTINATION.setValue(writeOutputLocation);

        BenchmarkRunner.main(new String[] {});

        final Map<String, Object> outputProperties = getPropertiesFromYaml(writeOutputLocation);
        assertEquals(EXPECTED_WRITE_METRIC_NAME, outputProperties.get("name"));
        assertTrue(Long.parseLong((String) outputProperties.get("achievedResult")) > 0L);
    }

    private String createTestJobFile(final String type, final String directory, final String nrFiles,
                                     final String fileSize, final String jobFileName) throws IOException {
        final File jobfile = new File(directory + "/" + jobFileName);
        assertTrue(jobfile.createNewFile());

        final BufferedWriter bw = new BufferedWriter(new FileWriter(jobfile));
        bw.write("type=" + type);
        bw.newLine();
        bw.write("directory=" + directory);
        bw.newLine();
        bw.write("nrFiles=" + nrFiles);
        bw.newLine();
        bw.write("fileSize=" + fileSize);

        bw.flush();
        bw.close();

        return jobfile.getPath().replaceAll("\\\\", "/");
    }

    private Map<String, Object> getPropertiesFromYaml(final String uri) throws IOException  {
        try (final InputStream input = Files.newInputStream(Paths.get(uri))) {
            Yaml yaml = new Yaml();
            return yaml.load(input);
        }
    }
}
