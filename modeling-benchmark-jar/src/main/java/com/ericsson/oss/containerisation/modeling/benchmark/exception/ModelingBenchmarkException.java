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
package com.ericsson.oss.containerisation.modeling.benchmark.exception;

import java.util.Arrays;

/**
 * Generic exception for modeling benchmark.
 */
public class ModelingBenchmarkException extends RuntimeException {

    public ModelingBenchmarkException(final String message) {
        super("Exception thrown by modeling benchmark: " + message);
    }

    public ModelingBenchmarkException(final String message, final Throwable throwable) {
        super("Exception thrown by modeling benchmark: " + message
                + "Stack trace: " + Arrays.toString(throwable.getStackTrace()));
    }
}
