{{/*
Expand the name of the chart.
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create group label
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.benchmarkGroup.label" -}}
    {{- $label := .Chart.Name -}}
    {{- if (include "eric-enm-cniv-modeling-benchmark.cnivagent" .) -}}
        {{- if .Values.global -}}
            {{- if .Values.global.sequence -}}
                {{- range $groupmap := .Values.global.sequence -}}
                    {{- range $group, $benchlist := $groupmap -}}
                        {{- range $bench := $benchlist -}}
                            {{- if eq $.Chart.Name $bench -}}
                                {{- $label = $group -}}
                            {{- end }}
                        {{- end }}
                    {{- end }}
                {{- end }}
            {{- end }}
         {{- end }}
    {{- end }}
    {{- $label | lower | trunc 54 | trimSuffix "-" -}}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.selectorLabels" -}}
app.kubernetes.io/name: {{ include "eric-enm-cniv-modeling-benchmark.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
benchmarkname: {{ include "eric-enm-cniv-modeling-benchmark.name" . }}
benchmarkgroup: {{ include "eric-enm-cniv-modeling-benchmark.benchmarkGroup.label" . }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.standard-labels" -}}
helm.sh/chart: {{ include "eric-enm-cniv-modeling-benchmark.chart" . }}
{{ include "eric-enm-cniv-modeling-benchmark.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
sidecar.istio.io/inject: "false"
{{- end }}

{{/*
User defined labels, global level and service level
*/}}
{{ define "eric-enm-cniv-modeling-benchmark.global-labels" }}
  {{- $global := (.Values.global).labels -}}
  {{- $service := .Values.labels -}}
  {{- include "eric-enm-cniv-modeling-benchmark.mergeLabels" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Merge labels for default, which includes standard and global labels
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.labels" -}}
  {{- $standard := include "eric-enm-cniv-modeling-benchmark.standard-labels" . | fromYaml -}}
  {{- $global := include "eric-enm-cniv-modeling-benchmark.global-labels" . | fromYaml -}}
  {{- include "eric-enm-cniv-modeling-benchmark.mergeLabels" (dict "location" .Template.Name "sources" (list $standard $global)) | trim }}
{{- end -}}

{{/*
User defined annotations, global level and service level
*/}}
{{ define "eric-enm-cniv-modeling-benchmark.global-annotations" }}
  {{- $global := (.Values.global).annotations -}}
  {{- $service := .Values.annotations -}}
  {{- include "eric-enm-cniv-modeling-benchmark.mergeAnnotations" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Merged annotations for Default, which includes productInfo and global
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.annotations" -}}
  {{- $productInfo := include "eric-enm-cniv-modeling-benchmark.product-info" . | fromYaml -}}
  {{- $global := include "eric-enm-cniv-modeling-benchmark.global-annotations" . | fromYaml -}}
  {{- include "eric-enm-cniv-modeling-benchmark.mergeAnnotations" (dict "location" .Template.Name "sources" (list $productInfo $global)) | trim }}
{{- end -}}

{{/*
CNIV Agent
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.cnivagent" -}}
{{- $cnivagent := .Values.cnivAgent.enabled -}}
{{- if .Values.global -}}
    {{- if .Values.global.cnivAgent -}}
        {{- if .Values.global.cnivAgent.enabled -}}
            {{- $cnivagent = .Values.global.cnivAgent.enabled -}}
        {{- end }}
    {{- end }}
{{- end }}
{{- $cnivagent -}}
{{- end }}

{{/*
Agent Name
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.agentName" -}}
{{- $agentName := .Values.cnivAgent.name -}}
{{- if .Values.global -}}
    {{- if .Values.global.cnivAgent -}}
        {{- if .Values.global.cnivAgent.name -}}
            {{- $agentName = .Values.global.cnivAgent.name -}}
        {{- end }}
    {{- end }}
{{- end }}
{{- $agentName -}}
{{- end }}

{{/*
Agent Port
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.agentPort" -}}
{{- $agentPort := .Values.cnivAgent.port -}}
{{- if .Values.global -}}
    {{- if .Values.global.cnivAgent -}}
        {{- if .Values.global.cnivAgent.port -}}
            {{- $agentPort = .Values.global.cnivAgent.port -}}
        {{- end }}
    {{- end }}
{{- end }}
{{- $agentPort -}}
{{- end }}

{{/*
Agent address
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.agentAddress" -}}
{{ include "eric-enm-cniv-modeling-benchmark.agentName" . }}:{{ include "eric-enm-cniv-modeling-benchmark.agentPort" . }}
{{- end }}

{{/*
Paths to images
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.initbenchImagePath" -}}
{{ include "eric-enm-cniv-modeling-benchmark.registry" . }}/{{ .Values.imageCredentials.initBench.repoPath }}/{{ .Values.images.initBench.name }}:{{ .Values.images.initBench.tag }}
{{- end }}

{{- define "eric-enm-cniv-modeling-benchmark.providerImagePath" -}}
{{ include "eric-enm-cniv-modeling-benchmark.registry" . }}/{{ .Values.imageCredentials.providerBench.repoPath }}/{{ .Values.images.providerBench.name }}:{{ .Values.images.providerBench.tag }}
{{- end }}

{{/*
Registry URL
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.registry" -}}
{{- $registry := .Values.registry.url -}}
{{- if .Values.global -}}
    {{- if .Values.global.registry -}}
        {{- if .Values.global.registry.url -}}
            {{- $registry = .Values.global.registry.url -}}
        {{- end }}
    {{- end }}
{{- end }}
{{- $registry -}}
{{- end }}

{{/*
Define PullSecret
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.pullSecret" -}}
    {{- $pullSecret := .Values.imageCredentials.registry.pullSecret -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.pullSecret -}}
                {{- $pullSecret = .Values.global.registry.pullSecret }}
            {{- end }}
        {{- end }}
    {{- end }}
{{- $pullSecret -}}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "eric-enm-cniv-modeling-benchmark.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Resolve imagePullPolicy
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.imagePullPolicy" -}}
    {{- $imagePullPolicy := .Values.imageCredentials.registry.imagePullPolicy -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- $imagePullPolicy = .Values.global.registry.imagePullPolicy -}}
        {{- end }}
    {{- end }}
    {{- coalesce $imagePullPolicy "IfNotPresent" -}}
{{- end }}

{{/*
Resolve Storage class
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.storageClass" -}}
    {{- $storageClass := .Values.persistentVolumeClaim.storageClassName }}
    {{- if .Values.global -}}
        {{- if .Values.global.persistentVolumeClaim -}}
            {{- $storageClass = .Values.global.persistentVolumeClaim.storageClass.file -}}
        {{- end }}
    {{- end }}
    {{- $storageClass -}}
{{- end }}

{{/*
Create the fsGroup value according to DR-D1123-136.
*/}}
{{- define "eric-enm-cniv-modeling-benchmark.fsGroup.coordinated" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.fsGroup -}}
      {{- if not (kindIs "invalid" .Values.global.fsGroup.manual) -}}
        {{- if .Values.global.fsGroup.manual | int64 | toString | trimAll " " | mustRegexMatch "^[0-9]+$" }}
          {{- .Values.global.fsGroup.manual | int64 | toString | trimAll " " }}
        {{- else }}
          {{- fail "global.fsGroup.manual shall be a positive integer if given" }}
        {{- end }}
      {{- else -}}
        {{- if eq (.Values.global.fsGroup.namespace | toString) "true" -}}
          # The 'default' defined in the Security Policy will be used.
        {{- else -}}
          10000
        {{- end -}}
      {{- end -}}
    {{- else -}}
      10000
    {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
{{- end -}}

{{- define "eric-enm-cniv-modeling-benchmark.product-info" }}
{{-   if .Files.Glob "eric-product-info.yaml" }}
ericsson.com/product-name: {{ (fromYaml ( .Files.Get "eric-product-info.yaml")).productName | quote }}
ericsson.com/product-number: {{ (fromYaml ( .Files.Get "eric-product-info.yaml")).productNumber | quote }}
ericsson.com/product-revision: {{ regexReplaceAll "(.*)[+|-].*" .Chart.Version "${1}" | quote }}
{{-     $productDescription := (fromYaml ( .Files.Get "eric-product-info.yaml")).productDescription -}}
{{-     if $productDescription }}
ericsson.com/description: {{ $productDescription | quote }}
{{-     end -}}
{{-   end -}}
{{- end -}}