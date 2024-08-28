#!/bin/sh
# *******************************************************************************
# * COPYRIGHT Ericsson 2023
# *
# * The copyright to the computer program(s) herein is the property of
# * Ericsson Inc. The programs may be used and/or copied only with written
# * permission from Ericsson Inc. or in accordance with the terms and
# * conditions stipulated in the agreement/contract under which the
# * program(s) have been supplied.
# *******************************************************************************
#
# Description: Entrypoint script for eric-enm-cniv-modeling-benchmark.
# Arguments:
#   $1: BENCHMARK_NAME: I.E eric-enm-cniv-modeling-benchmark-write
#   $2: NODE_NAME: I.E worker-node-1
### Global Variables ####
_JAVA="java"
_BASENAME=/bin/basename
_CAT=/bin/cat

SCRIPT_NAME=$(${_BASENAME} "${0}")
CLASSPATH="/benchmark/dependency-jars/*:/benchmark/modeling-benchmark.jar"
MAINCLASS="com.ericsson.oss.containerisation.modeling.benchmark.BenchmarkRunner"
PYTHON_REPORT_SCRIPT="report.py"

### Methods ###
# Display usage information about this script
usage() {
    ${_CAT} << EOF
    Usage: ${SCRIPT_NAME} <DESCRIPTION> <BENCHMARK_GROUP_LABEL> <BENCHMARK_NAME> <NODE_NAME> <AGENT_ENABLED> <AGENT_ADDRESS>
    Example: ${SCRIPT_NAME} eric-enm-modeling-benchmark-write worker-node-1
EOF
    exit 1
}


### Main ####
if [[ ${#} -ne 6 ]]; then
  usage
fi
DESCRIPTION=${1}
BENCHMARK_GROUP_LABEL=${2}
BENCHMARK_NAME=${3}
NODE_NAME=${4}
AGENT_ENABLED=${5}
AGENT_ADDRESS=${6}

${_JAVA} -cp "${CLASSPATH}" "${MAINCLASS}" ;
${PYTHON_REPORT_SCRIPT} "${DESCRIPTION}" "${BENCHMARK_GROUP_LABEL}" "${BENCHMARK_NAME}" "${NODE_NAME}" "${AGENT_ENABLED}" "${AGENT_ADDRESS}" ;