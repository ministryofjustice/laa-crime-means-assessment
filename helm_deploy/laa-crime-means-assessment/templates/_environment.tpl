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
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: MAAT_API_OAUTH_CLIENT_ID
    value: {{ .Values.maatApi.clientId }}
  - name: MAAT_API_OAUTH_CLIENT_SECRET
    value: {{ .Values.maatApi.clientSecret }}          
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
  - name: AWS_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: cma-messaging-queues-output
        key: access_key_id
  - name: AWS_SECRET_KEY
    valueFrom:
      secretKeyRef:
        name: cma-messaging-queues-output
        key: secret_access_key
  - name: POST_PROCESSING_QUEUE
    valueFrom:
      secretKeyRef:
        name: cma-messaging-queues-output
        key: sqs_name_means_assessment_post_processing
{{- end -}}
