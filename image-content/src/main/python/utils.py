#!/usr/bin/env python3.11
"""
COPYRIGHT Ericsson 2023
The copyright to the computer program(s) herein is the property of
Ericsson Inc. The programs may be used and/or copied only with written
permission from Ericsson Inc. or in accordance with the terms and
conditions stipulated in the agreement/contract under which the
program(s) have been supplied.
"""
import logging
import os
import yaml


def read_yaml_content(fname):
    """Gets Yaml file content.

    Args:
        fname: File name of the YAML file to read.
    Returns:
        A dict containing data.
    """
    with open(fname, 'r', encoding='utf-8') as yaml_content:
        return yaml.safe_load(yaml_content)


def print_rows(rows: list, headers: list, title: str = '', sort_by: str = '',
               column_spacing: int = 2) -> None:
    """Print passed in rows in a table format with optional title.

    Args:
        rows: Data format is a list of dictionaries representing rows
        headers: Table headers in a list, should match keys in rows. Omitted
                 headers will not be printed.
        title:   Title to print above table
        sort_by: Optional column header used to sort table
        column_spacing: Optional column spacing
    """
    # Find maxlen for each column
    column_widths = {column: max(len(column), max(len(node.get(column, '')) for node in rows))
                     for column in headers}
    header_row = ''.join(column.ljust(column_widths[column] + column_spacing)
                         for column in headers)
    table_width = len(header_row) - column_spacing
    if title:
        print(f"{title}\n")
    print(header_row)
    print('-' * table_width)
    if sort_by:
        rows = sorted(rows, key=lambda k: k.get(sort_by, ''))
    for row in rows:
        table_row = ''.join(row.get(column, '').ljust(column_widths[column] + column_spacing)
                            for column in headers)
        print(table_row)
    print('-' * table_width, end='\n\n')


def get_logger(name):
    """Configures a basic logger based on log level set at system level variable.

        Args:
             name: Logger name
        Returns:
            A logger
        """
    log_level = os.environ.get('LOG_LEVEL', 'INFO').upper()
    msg_format = '%(asctime)s %(name)s %(levelname)s: %(message)s'
    logging.basicConfig(level=log_level, format=msg_format)
    return logging.getLogger(name=name)
