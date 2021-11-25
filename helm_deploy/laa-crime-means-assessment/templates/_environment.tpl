{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-crime-means-assessment.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry_dsn }}
  - name: SENTRY_CURRENT_ENV
    value: {{ .Values.java.host_env }}
  - name: DATASOURCE_HOST_PORT
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: rds_instance_endpoint
  - name: DATASOURCE_DBNAME
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_name
  - name: DATASOURCE_USERNAME
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_username
  - name: DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_password
{{- end -}}
