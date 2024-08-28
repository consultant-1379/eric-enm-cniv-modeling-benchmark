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
 * Represents the result of a job execution.
 */
public class Result {

    private final String name;
    private final String achievedResult;

    /**
     * Constructs an instance of {@link Result}.
     *
     * @param name
     *         Name of the benchmark metric being tested.
     * @param achievedResult
     *         Result of the benchmark execution.
     */
    public Result(final String name, final String achievedResult) {
        this.name = name;
        this.achievedResult = achievedResult;
    }

    public String getName() {
        return name;
    }

    public String getAchievedResult() {
        return achievedResult;
    }
}
