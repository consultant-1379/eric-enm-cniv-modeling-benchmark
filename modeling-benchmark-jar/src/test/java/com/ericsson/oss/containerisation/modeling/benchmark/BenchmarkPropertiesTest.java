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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BenchmarkPropertiesTest {

    @AfterEach
    void teardown() {
        System.clearProperty(BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.getName());
        System.clearProperty(BenchmarkProperties.RESULT_FILE_DESTINATION.getName());
        System.clearProperty(BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.getName());
    }

    @Test
    void GIVEN_propertiesNotOverridden_WHEN_getValue_THEN_defaultValuesReturned() {
        assertEquals("/config/benchmark.properties", BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.getValue());
        assertEquals("/output/result.yaml", BenchmarkProperties.RESULT_FILE_DESTINATION.getValue());
        assertNull(BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.getValue());
    }

    @Test
    void GIVEN_propertiesOverridden_WHEN_getValue_THEN_configuredValuesReturned() {
        BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.setValue("myBenchmark.properties");
        BenchmarkProperties.RESULT_FILE_DESTINATION.setValue("test_result.yaml");
        BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.setValue("test/tempFiles");

        assertEquals("myBenchmark.properties", BenchmarkProperties.INPUT_PROPERTIES_FILE_LOCATION.getValue());
        assertEquals("test_result.yaml", BenchmarkProperties.RESULT_FILE_DESTINATION.getValue());
        assertEquals("test/tempFiles", BenchmarkProperties.TEMPORARY_FILES_DIRECTORY.getValue());
    }
}
