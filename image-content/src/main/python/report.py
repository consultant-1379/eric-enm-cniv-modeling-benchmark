#!/usr/bin/env python3.11
"""
COPYRIGHT Ericsson 2023
The copyright to the computer program(s) herein is the property of
Ericsson Inc. The programs may be used and/or copied only with written
permission from Ericsson Inc. or in accordance with the terms and
conditions stipulated in the agreement/contract under which the
program(s) have been supplied.
"""
import sys
from pathlib import Path

from distutils.util import strtobool
import requests  # pylint: disable=E0401
from requests.exceptions import RequestException  # pylint: disable=E0401
from requests.adapters import HTTPAdapter, Retry  # pylint: disable=E0401
from urllib3.exceptions import NewConnectionError  # pylint: disable=E0401

from utils import print_rows, read_yaml_content, get_logger

REQUIREMENTS_PATH = '/config/requirements.yaml'
RESULT_FILE = '/output/result.yaml'
CNIV_AGENT_URL = 'http://{}/result/{}/{}'
SEND_ATTEMPTS = 3


def build_report():
    """ Creates a report for a benchmark result

    Returns:
        Dictionary containing report info
    """
    logger.info('Evaluating achieved throughput against requirement')
    logger.debug('Benchmark requirements file: %s', REQUIREMENTS_PATH)
    logger.debug('Benchmark result file: %s', RESULT_FILE)
    report = []
    results = read_yaml_content(RESULT_FILE)
    output = {}
    for key, val in results.items():
        output[key] = val
    requirements = read_yaml_content(Path(REQUIREMENTS_PATH))
    reqs = requirements.get('requirements', {})
    exp_result = reqs.get('expectedResult')
    logger.debug('Expected throughput: %s', exp_result)
    achieved_result = output.get('achievedResult')
    passed = exp_result is not None and float(achieved_result) >= exp_result
    output['expectedResult'] = str(exp_result)
    output['status'] = 'PASS' if passed else 'FAIL'
    output['subRowDesc'] = reqs.get('description', '')
    logger.debug('Benchmark status: %s', output['status'])
    report.append(output)
    return {'report': report,
            'description': DESCRIPTION,
            'nodename': NODE_NAME}


def print_report(results: dict) -> None:
    """ Print formatted reports

    Args:
        results: Dictionary containing results/error information
    """
    if results:
        print('\n\n   -- REPORT --\n')
        headers = ['name', 'expectedResult', 'achievedResult', 'status']
        for name, report in results.items():
            print_rows(report['report'], headers,
                       title=f"{name}    NODE: {report.get('nodename')}",
                       sort_by='name')
    else:
        print('\n\n   -- No REPORT to display --\n')


def send_report(report, agent_address, bench_group, bench_name, attempts):
    """Sends a report to CNIV agent.
    Args:
        report: A dict containing report data.
        agent_address: Agent address.
        bench_group: Benchmark group name.
        bench_name: Benchmark name.
        attempts: An integer containing number of http attempts to send
            a report to the agent
    Raises:
        RequestException: Fails to successfully send a report.
        NewConnectionException: Fails to establish a connection to agent.
    """
    url = CNIV_AGENT_URL.format(agent_address, bench_group, bench_name)

    session = requests.Session()
    retries = Retry(total=attempts, backoff_factor=5)
    session.mount('http://', HTTPAdapter(max_retries=retries))

    logger.info('Sending report to: %s', url)
    try:
        session.post(url, json=report, timeout=10)
        logger.info("Successfully sent report to CNIV agent")
    except (RequestException, NewConnectionError) as exception:
        logger.error("Failed to send report to CNIV agent.")
        raise exception


def main():
    """Main method generates a report based on the benchmark job results,
       prints the report to the console, and sends the report to the cniv agent if enabled
    """
    report = build_report()  # pylint: disable=E1121
    print_report({BENCHMARK_NAME: report})
    if AGENT_ENABLED:
        send_report(report, AGENT_ADDRESS, BENCHMARK_GROUP_LABEL, BENCHMARK_NAME, SEND_ATTEMPTS)


if __name__ == '__main__':
    if len(sys.argv) < 7:
        print('Usage: script.py <DESCRIPTION> <BENCHMARK_GROUP_LABEL> <BENCHMARK_NAME> ' +
              '<NODE_NAME> <AGENT_ENABLED> <AGENT_ADDRESS>')
        sys.exit(1)

    DESCRIPTION = sys.argv[1]
    BENCHMARK_GROUP_LABEL = sys.argv[2]
    BENCHMARK_NAME = sys.argv[3]
    NODE_NAME = sys.argv[4]
    AGENT_ENABLED = bool(strtobool(sys.argv[5]))
    AGENT_ADDRESS = sys.argv[6]

    logger = get_logger(BENCHMARK_NAME)
    logger.debug('Arguments received:')
    logger.debug('Benchmark Group: %s', BENCHMARK_NAME)
    logger.debug('Benchmark Name: %s', BENCHMARK_NAME)
    logger.debug('Node Name: %s', NODE_NAME)
    logger.debug('Agent Enabled: %s', AGENT_ENABLED)
    logger.debug('Agent Address: %s', AGENT_ADDRESS)

    main()
