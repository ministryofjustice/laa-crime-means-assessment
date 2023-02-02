## Laa Crime Means Assessment

This microservice implements the create/update assessment and get old assessment business rules migrated from the legacy PL/SQL Assessments package.
This is a Java based Spring Boot application hosted on [MOJ Cloud Platform](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/concepts/about-the-cloud-platform.html).

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/ministryofjustice/laa-crime-means-assessment/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/ministryofjustice/laa-crime-means-assessment/tree/main)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://laa-crime-means-assessment-dev.apps.live.cloud-platform.service.justice.gov.uk/open-api/swagger-ui/index.html)

## Contents

- [Getting started](#getting-started)
  - [Developer setup](#developer-setup)
  - [Decrypting docker-compose.override.yml](#decrypting-docker-composeoverrideyml)
- [Running locally](#running-locally)
- [Database](#database)
- [CI/CD](#cicd)
- [Documentation](#documentation)
  - [CMA](#cma)
  - [High level design](#high-level-design)
  - [Open API](#open-api)
- [Application Monitoring and Logs](#application-monitoring-and-logs)
  - [Error Reporting](#error-reporting)
- [Mutation PI testing](#mutation-pi-testing)
- [JSON Schema to POJO](#json-schema-to-pojo)
- [Further reading](#further-reading)

## Getting started

### Developer setup

1. Go through with this [Java Developer On-boarding Check List](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3738468667/Java+Developer+Onboarding+Check+List/) and complete all tasks.
2. Request a team member to be added to the repository.
3. Create a GPG (more detail further down on the page) key and create a PR. Someone from the team will approve the PR.
4. This is a document to outline the general guideline [Developer Guidelines](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3896049821/Developer+Guidelines).
5. This project have its own dedicated Jira Scrum board, and you can access [from here](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881) and [project backlog](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881/backlog)

### Decrypting docker-compose.override.yml

The `docker-compose.override.yml` is encrypted using [git-crypt](https://github.com/AGWA/git-crypt).

To run the app locally you need to be able to decrypt this file.

You will first need to create a GPG key. See [Create a GPG Key](https://docs.publishing.service.gov.uk/manual/create-a-gpg-key.html) for details on how to do this with `GPGTools` (GUI) or `gpg` (command line).
You can install either from a terminal or just download the UI version.

```
brew update
brew install gpg
brew install git-crypt
```

Once you have done this, a team member who already has access can add your key by running `git-crypt add-gpg-user USER_ID`\* and creating a pull request to this repo.

Once this has been merged you can decrypt your local copy of the repository by running `git-crypt unlock`.

\*`USER_ID` can be your key ID, a full fingerprint, an email address, or anything else that uniquely identifies a public key to GPG (see "HOW TO SPECIFY A USER ID" in the gpg man page).

## Running locally

Clone Repository

```sh
git clone git@github.com:ministryofjustice/laa-crime-means-assessment.git

cd crime-means-assessment
```

The project is build using [Gradle](https://gradle.org/). This also includes plugins for generating IntelliJ configuration.

Make sure tests all testes are passed by running following ‘gradle’ Command

```sh
./gradlew clean test
```

You will need to build the artifacts for the source code, using `gradle`.

```sh
./gradlew clean build
```

The apps should then startup cleanly if you run

```sh
docker-compose build
docker-compose up
```

laa-crime-means-assessment application will be running on http://localhost:8080

## Database

This application is run with PostgresSQL using docker compose. PostgresSQL is used solely for static data.
For database changes, we are using [liquibase]() and all the sql scripts stored in the directory (resources/db/changelog/).

All CRUD operations in the MAATDB are run via the [MAAT-API](https://github.com/ministryofjustice/laa-maat-court-data-api)

## CI/CD

We have configured a CircleCI code pipelines. You can [log in](https://app.circleci.com/pipelines/github/ministryofjustice/laa-crime-means-assessment) from here to access the pipeline.

To make any changes,create a branch and submit the PR. Once the PR is submitted the branch deployment is kicked off under the new branch name.
On successful build, the image is pushed to AWS ECR and requires approval to deploy to dev.

Once the PR is merged with main, the build is automatically deployed to DEV. Deployment to higher environments requires approval.

## Debugging Application

Please refer to the manual [here](https://dsdmoj.atlassian.net/wiki/spaces/~360899610/pages/3846439496/Debugging+crime-means-assessment)

Speak to one of the team member and get the docker-compose-debug.yml which will have relevant credentials to run the application on remote Debug Mode.

Run the following command

```sh
 docker-compose -f docker-compose-debug.yml up
```

Make sure Remote Debug Option is set up on your preferred Editor.

##Documentation

###[CMA Documentation](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3917447206/Crime+Means+Assessment+Service+CMA)

###[High level design](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/3673751570/Means+Assessment+-+High+level+Design+Approach)

### Open API

We have implemented the Open API standard (with Swagger 3). The web link provides a details Rest API with a schema definition. The link can only from local or from dev environment.
The swagger link can be found from [here](http://localhost:8080/open-api/docs.html)

## Application Monitoring and Logs

[Prometheus](https://prometheus.cloud-platform.service.justice.gov.uk)
[Thanos](https://thanos.live.cloud-platform.service.justice.gov.uk)
[AlertManager](https://alertmanager.cloud-platform.service.justice.gov.uk)
[Grafana](https://grafana.cloud-platform.service.justice.gov.uk)
[Kibana](https://kibana.cloud-platform.service.justice.gov.uk)

###Error Reporting
Sentry sentry-java/sentry-spring-boot-starter at main · getsentry/sentry-java

https://sentry.io/organizations/ministryofjustice/projects/laa-crime-means-assessment/?project=6212907

## Mutation PI testing

Mutation testing providing test coverage for Java applications.
Faults (or mutations) are automatically seeded into the code, then your tests are run. If your tests fail then the mutation is killed, if your tests pass then the mutation lived.
Here are some of the key benefits for this type of testing.

- High coverage of testing
- New kinds of test scenarios
- Validate unit test scripts

Once we build the project then run the following command. This will generate a test report under build/reports/pitest/
We want to make sure that the Mutation Coverage for new classes are covered properly

```sh
./gradlew pitest
```

## JSON Schema to POJO

Gradle plugin that converts json schema files into POJOs (Plain Old Java Objects). See [Extended jsonschema2pojo Gradle plugin](https://github.com/jsonschema2dataclass/js2d-gradle).

The generated POJO files can be found in crime-means-assessment/build/generated/sources/js2d, after each build, or by running the following command:

```shell
./gradlew clean generateJsonSchema2DataClass
```

### Configuration

In the jsonSchema2Pojo section of crime-means-assessment/build.gradle file, there are a number of settings to that have
been set and are documented inside that section.:

- source.setFrom: The location of the json schema files.
- targetPackage: what package the POJOs should belong to
- includeJsr303Annotations: JSR-303/349 annotations (for schema rules like minimum, maximum, etc)
- dateTimeType: What type to use instead of string

### Further reading

- [Diagrams for LAA and the common platform](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/1513128006/Diagrams)
- [New Starter Guild](https://dsdmoj.atlassian.net/wiki/spaces/LAA/pages/1391460702/New+Hire+Check+List)
- [Cloud Platform user guide](https://user-guide.cloud-platform.service.justice.gov.uk/#application-logging)
- [Modernisation Platform Team Information](https://user-guide.modernisation-platform.service.justice.gov.uk/#modernisation-platform-team-information)
