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

import com.ericsson.oss.containerisation.modeling.benchmark.Result;

/**
 * This interface represents the modeling benchmark jobs.
 */
public interface BenchmarkJob {

    /**
     * Executes the benchmark.
     *
     * @return {@link Result} instance holding the result of the execution.
     */
    Result execute();
}
