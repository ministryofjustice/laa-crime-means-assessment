## laa-crime-means-assessment

This is a Java based Spring Boot Application which will be hosted on AWS Environment. The application is being deployed on to the AWS ECS container service. This is a Facade application to the existing LAA legacy Applications MAAT/MLRA. 

[High level design] (https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/3673751570/Means+Assessment+-+High+level+Design+Approach)


## Developer setup

TBD

### Pre-requisites

1. Docker
2. SSH 
3. An editor/IDE of some sort - preferably Intellij/Ecilipse 
4. Grade
5. aws cli 
6. kubectl

We're using [Gradle](https://gradle.org/) to build the application. This also includes plugins for generating IntelliJ configuration.

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

Once you have done this, a team member who already has access can add your key by running `git-crypt add-gpg-user USER_ID`* and creating a pull request to this repo.

Once this has been merged you can decrypt your local copy of the repository by running `git-crypt unlock`. 

*`USER_ID` can be your key ID, a full fingerprint, an email address, or anything else that uniquely identifies a public key to GPG (see "HOW TO SPECIFY A USER ID" in the gpg man page).

### DB Configuration

TBD


### Application Set up

Clone Repository

```sh
git clone git@github.com:ministryofjustice/laa-crime-means-assessment.git

cd crime-means-assessment
```
Makesure tests all testes are passed by running following ‘gradle’ Command  

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

laa-crime-means-assessment application will be running on http://localhost:8090 

### Cloud Platform Set Up 

It is advisable to have the cloud platform set up locally. 

Follow this link to on board yourself with the LAA cloud platform environment. - https://user-guide.cloud-platform.service.justice.gov.uk/documentation/getting-started/kubectl-config.html#how-to-use-kubectl-to-connect-to-the-cluster


Configure AWS details using aws cli (command - ```aws configure```) Set up AWS Access Key ID & AWS Secret Access Key. All other values can be default. 

More detail can be found on https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/edit-v2/1756201359.

The terraform scripts for the SQS can be found on https://github.com/ministryofjustice/cloud-platform-environments/tree/master/namespaces/live-1.cloud-platform.service.justice.gov.uk/laa-court-data-adaptor-dev


### Deployment 

TBD

### Open API
We have implemented the Open API standard (with Swagger 3). The web link provides a details Rest API with a schema definition. The link can only from local or from dev environment. The swagger link can be found from [here](http://localhost:8090/open-api/docs.html)  
  
  
### Debugging Application

Speak to one of the team member and get the docker-compose-debug.yml which will have  relevant credentials  to run the application on remote Debug Mode.

Run the following command
  
```sh
 docker-compose -f docker-compose-debug.yml up
```

Make sure Remote Debug Option is set up on your preferred Editor.


### Application Monitoring and Logs 

The LAA Crime Means Assessment API has been configured to send the application logs to both AWS Cloud Watch and Sentry. 

####Cloud Watch Logs: 
To see the Cloud watch logs, you need to have the right user groups and permission. More details about this available here. (link) The application logs are configured with the followings log groups (names). 
The application deployed as a Docker container, and the logs can also be found from the AWS ECS logs. 

####Sentry 
Sentry is a 3rd party application logging and monitoring platform. The platform provides easier searching based on meta-data as well as application monitoring. You can learn more about ['how we have integrated Sentry to improve application logging and monitoring'](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/2139914261/Integrate+Sentry+to+improve+application+logging+and+monitoring)
There are several alert rules configured on Sentry that will push notification to both email and Slack channel. We have created a dedicated slack channel (named 'laa-crime-apps-logs-alert'). Sentry will push the alert to this channel for a specific type of exceptions. The configuration for Slack alert can be change from a [Sentry dashboard](https://sentry.io/settings/ministryofjustice/projects/laa-crime-means-assessment/alerts/).  


### Further reading
 
  
* [Diagrams for LAA and the common platform](https://dsdmoj.atlassian.net/wiki/spaces/LAACP/pages/1513128006/Diagrams)

* [New Starter Guild](https://dsdmoj.atlassian.net/wiki/spaces/LAA/pages/1391460702/New+Hire+Check+List)




