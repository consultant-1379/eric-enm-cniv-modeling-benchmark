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

/**
 * This class defines the system properties that can be used when invoking the modeling benchmark and their default values.
 */
public enum BenchmarkProperties {

    /**
     * The location of the input properties file.
     * The file at this location is expected to exist on benchmark invocation.
     */
    INPUT_PROPERTIES_FILE_LOCATION("modeling.benchmark.input.file", "/config/benchmark.properties"),

    /**
     * The location to write the result file from the benchmark.
     * The parent directory of this location is expected to exist on benchmark invocation
     */
    RESULT_FILE_DESTINATION("modeling.benchmark.result.file", "/output/result.yaml"),

    /**
     * The location to place temporary files for write benchmark.
     */
    TEMPORARY_FILES_DIRECTORY("modeling.benchmark.temporary.files.directory", null);

    private final String propertyName;
    private final String defaultValue;

    BenchmarkProperties(final String propertyName, final String defaultValue) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
    }

    /**
     * Get the name of this property.
     *
     * @return the name of this property
     */
    public String getName() {
        return propertyName;
    }

    /**
     * Get the value for this property. If no value exits for the property, then the default value is returned. If no value or default value exists
     * then <code>null</code> is returned.
     *
     * @return the value for this property.
     */
    public String getValue() {
        return System.getProperty(propertyName, defaultValue);
    }

    /**
     * Set the value of the property to the given value.
     *
     * @param value
     *         the value to give to the property.
     */
    @SuppressWarnings("squid:S3066")
    public void setValue(final String value) {
        System.setProperty(propertyName, value);
    }
}
