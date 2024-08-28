# Modeling-Benchmark-Chart

*eric-enm-cniv-modeling-benchmark* is a custom cloud native infrastructure
verification benchmark, which verifies the underlying filesystem is sufficient
to handle the write demands of the modeling usecases.
This project achieves this by stressing the infrastructure with write
operations and measuring the throughput of the operations.

## Benchmark throughput calculations

The write benchmark must determine that the underlying storage is sufficient to
deploy models during MDT. To determine this we must have or understand the
following values:

* The number of **unique** models files copied in an MDT execution
* The average file size of a model
* The duration of a bad/just acceptable MDT execution
* The percentage of time allocated to IO operations in the MDT execution.

With these values the following calculation can be populated:

```illustration
<allowable copy time in seconds> = <MDT execution duration mins> x <percentage of time allocated to IO> x 60
<Throughput in KBs> = <number of files> x <average file size in bytes> / <allowable copy time in seconds> / 1000
```

For example:

```example
allowable copy time in seconds = 140(mins) x 0.30 = 42 x 60 = 2520
Throughput in KBs = 300000 x 20000 / 2520 = 2,380,952 / 1000 = 2,380
```

## Benchmark Artifacts

This benchmark creates 2 artifacts, a docker image and a helm chart with the
name *eric-enm-cniv-modeling-benchmark*. The version for these artifacts follow
the format of major.minor.patch-buildnumber. i.e. 1.0.0-5
The paths for these artifacts change for different development stages.

### Image

* locally: armdocker.rnd.ericsson.se/proj-lt-test/eric-enm-cniv-modeling-benchmark:local
* PCR: armdocker.rnd.ericsson.se/proj-eric-oss-cniv/proj-eric-oss-cniv-ci-internal/eric-enm-cniv-modeling-benchmark:{version}
* Release: armdocker.rnd.ericsson.se/proj-eric-oss-cniv/proj-eric-oss-cniv-drop/eric-enm-cniv-modeling-benchmark:{version}

### Chart

* PCR: <https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-cniv-ci-internal-helm/eric-enm-cniv-modeling-benchmark/eric-enm-cniv-modeling-benchmark-{version}.tgz>
* Release: <https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-cniv-drop-helm/eric-enm-cniv-modeling-benchmark/eric-enm-cniv-modeling-benchmark-{version}.tgz>

## Running the Benchmark

### Local Setup

Laptop should have the following installed to run the benchmark chart:

* Docker (Docker-Desktop)
* Kubernetes (Docker-Desktop)
* Helm
* Bob [Installing Bob](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/HEAD/USER_GUIDE_2.0.md#Supported-options-for-deploying-Bob)

### Building the project

A local bob ruleset file is provided to assist in building and testing the project.

* To build the image, run ```bob build-local-image```
* To clean the project, run ```bob clean```
* To see the full list of commands available for the project ```bob --list```

### Create the Kube Secret

*Note: The project is defaulted to use this secret, so trying to get it work
before creating this secret will result in failure to pull the required image.*

The chart has dependencies and needs a secret to be generated and added to the
image credentials.

To create a secret based on a pre-determined auth token that has the required
access, the following steps shall be followed.

* Generate an *auth token* by running

```bash
echo "<signum>:<armdocker token>" | base64 -w 0
```

* Add the below entry to the "auths" object in the .docker/config.json file.

```json
"auths": {
    "armdocker.rnd.ericsson.se": {
      "auth": "<auth token>"
    }
  }
```

Once added, source this file and create a new kube secret, named
cniv-modeling-bm-secret, like below

```bash
kubectl create secret generic cniv-modeling-bm-secret -n <your-namespace-here> \
                        --from-file=".dockerconfigjson=${HOME}/.docker/config.json" \
                        --type=kubernetes.io/dockerconfigjson
```

This shall now be used as the pullsecret in the helm chart. Update the
devValues.yaml to represent this secret.
The name used in the above example is the default secret used in this chart.

### Add helm repo if not already present

```bash
helm repo list
```

Check if the url
<https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm-local>
is present in the list. If not present, add it to the list by doing

```bash
helm repo add cniv-drop-helm https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm-local --username <your_signum> --password <your_password>
```

### Installing the chart

* Update the dependencies first

```bash
helm dep up chart/eric-enm-cniv-modeling-benchmark/
```

* Install the chart after making sure the above secret is created. Supply the
values file present in the folder for local deployment.

```bash
helm install modeling-benchmark chart/eric-enm-cniv-modeling-benchmark/ --values devValues.yaml
```

* To see the results, look at the logs of the created pods.

```bash
kubectl logs <pod_name>
```

* To cleanup after the testing, uninstall the release

```bash
helm uninstall modeling-benchmark
```

### Testing on any other kubernetes cluster

When testing on a kubernetes cluster, following things needs to be taken care of.

* The secret needs to be present in the namespace intended for the benchmark chart
* The storageclass available in the cluster needs to be configured as the
storageclass used by the chart in the devValues.yaml file
