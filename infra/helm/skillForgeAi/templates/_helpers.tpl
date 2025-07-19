{{- define "skillForgeAi.name" -}}
{{- default .Values.nameOverride .Chart.Name | trunc 63 | lower | trimSuffix "-" -}}
{{- end }}

{{- define "skillForgeAi.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | lower | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Values.nameOverride .Chart.Name | lower -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end }}

{{- define "skillForgeAi.chart" -}}
{{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{- define "skillForgeAi.labels" -}}
helm.sh/chart: {{ include "skillForgeAi.chart" . }}
app.kubernetes.io/name: {{ include "skillForgeAi.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "skillForgeAi.selectorLabels" -}}
app.kubernetes.io/name: {{ include "skillForgeAi.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
