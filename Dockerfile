ARG BUILDER_IMAGE_REPO=armdocker.rnd.ericsson.se/dockerhub-ericsson-remote
ARG BUILDER_IMAGE_NAME=maven
ARG BUILDER_IMAGE_TAG=3.6.3-jdk-8

ARG CBO_IMAGE_NAME=sles
ARG CBO_IMAGE_REPO=armdocker.rnd.ericsson.se/proj-ldc/common_base_os_release
ARG CBO_VERSION=6.16.0-13

FROM ${BUILDER_IMAGE_REPO}/${BUILDER_IMAGE_NAME}:${BUILDER_IMAGE_TAG} AS MAVEN_BUILD

WORKDIR /
COPY image-content/builder-config/settings.xml /usr/share/maven/ref/
COPY checkstyle-suppressions.xml ./
COPY pom.xml ./
COPY ./modeling-benchmark-jar ./modeling-benchmark-jar
COPY ./testsuite ./testsuite

RUN mvn clean install -s /usr/share/maven/ref/settings.xml


FROM ${CBO_IMAGE_REPO}/${CBO_IMAGE_NAME}:${CBO_VERSION} AS PACKAGE

ARG BUILD_DATE
ARG COMMIT
ARG APP_VERSION

LABEL author="ENM/LooneyTunes"
LABEL com.ericsson.product-number="CXU 101 0001"
LABEL org.opencontainers.image.title="CNIV Modeling Benchmark" \
      org.opencontainers.image.created=${BUILD_DATE} \
      org.opencontainers.image.revision=${COMMIT} \
      org.opencontainers.image.vendor="Ericsson" \
      org.opencontainers.image.version=${APP_VERSION}

ARG USER_ID=10001

ARG CBO_VERSION
ARG CBOS_SLES_REPO=arm.rnd.ki.sw.ericsson.se/artifactory/proj-ldc-repo-rpm-local/common_base_os/sles/
ARG SLES_BASE_OS_REPO=sles_base_os_repo
ARG CNIV_PYPI_HOST=arm.seli.gic.ericsson.se
ARG CNIV_PYPI_REPO=https://arm.seli.gic.ericsson.se/artifactory/proj-cn-infra-verification-tool-pypi-local/

WORKDIR /
COPY ./image-content/requirements.txt /tmp/

RUN zypper addrepo -C -G -f https://${CBOS_SLES_REPO}${CBO_VERSION}?ssl_verify=no $SLES_BASE_OS_REPO && \
    zypper install -y java-1_8_0-openjdk && \
    zypper --non-interactive in python311 python311-pip && \
    zypper clean --all && \
    zypper removerepo --all && \
    pip3 install -I --no-cache-dir --trusted-host $CNIV_PYPI_HOST \
        --index-url $CNIV_PYPI_REPO \
        -r /tmp/requirements.txt && \
    zypper remove -y python311-pip && \
    echo "$USER_ID:x:$USER_ID:$USER_ID::/nonexistent:/bin/false" >>/etc/passwd && \
    echo "$USER_ID:!:0::::::" >>/etc/shadow

WORKDIR /benchmark

ARG PYTHON_DIR=/benchmark/modeling-py/
ARG SCRIPT_DIR=/benchmark/scripts/
ARG LIB_PATH=/benchmark/dependency-jars/
ARG MAIN_JAR=/benchmark/modeling-benchmark.jar

COPY --chown=$USER_ID ./image-content/src/main/python/* ${PYTHON_DIR}
COPY --chown=$USER_ID ./image-content/src/main/scripts/* ${SCRIPT_DIR}

RUN chmod 700 ${SCRIPT_DIR}/benchmark.sh && chmod 700 ${PYTHON_DIR}/*.py

COPY --chown=$USER_ID --from=MAVEN_BUILD modeling-benchmark-jar/target/dependency-jars/ ${LIB_PATH}
COPY --chown=$USER_ID --from=MAVEN_BUILD modeling-benchmark-jar/target/modeling-benchmark.jar ${MAIN_JAR}

ENV JAVA_HOME=/usr/java/latest
ENV PATH=$PATH:$JAVA_HOME/bin:${PYTHON_DIR}:${SCRIPT_DIR}

USER $USER_ID

ENTRYPOINT [ "benchmark.sh" ]
# Args should be provided by user, BENCHMARK_NAME & NODE_NAME
