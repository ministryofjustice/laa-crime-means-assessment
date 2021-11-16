{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for api and worker containers
*/}}
{{- define "laa-crime-means-assessment.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry_dsn }}
  - name: SENTRY_CURRENT_ENV
    value: {{ .Values.java.host_env }}
{{- end -}}
