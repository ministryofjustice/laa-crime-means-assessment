{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for api and worker containers
*/}}
{{- define "laa-crime-means-assessment.env-vars" }}
env:
  - name: AWS_ACCESS_KEY_ID
    valueFrom:
      secretKeyRef:
        name: cda-messaging-queues-output
        key: access_key_id
  - name: AWS_SECRET_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: cda-messaging-queues-output
        key: secret_access_key
  {{- end }}
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry_dsn }}
  - name: SENTRY_CURRENT_ENV
    value: {{ .Values.java.host_env }}
  - name: METRICS_SERVICE_HOST
    value: {{ include "laa-crime-means-assessment.fullname" . }}
{{- end -}}
