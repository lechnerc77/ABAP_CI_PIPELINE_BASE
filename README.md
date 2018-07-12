# ABAP Continous Integration Pipeline

This repository contains several templates that will enable you to set up a CI pipeline in ABAP using open source tools namely:

 - [abapGit](https://github.com/larshp/abapGit) as a Git client on your SAP ABAP System
 - a Git Server like [GitLab CE](https://gitlab.com/gitlab-org/gitlab-ce) (which is the one I used for testing)
 - [Jenkins](https://gitlab.com/gitlab-org/gitlab-ce) as automation tool running on a Linux system
 - [Postman](https://www.getpostman.com/) and [Newman](https://www.npmjs.com/package/newman)  as tools to call the SAP ABAP system endpoints
 - a SAP ABAP System that is cabaple to run the ABAP Development Tools 

We assume that all these components are properly set up.

## Scenario
The scenario in scope is:
 - a developer finishes a development task and commits i.e. pushes the work to a Git server using abapGit. The project contains a jenkins file that describes the build pipeline of Jenkins.
 - The commit triggers a build of the project in Jenkins. The concrete steps of the build defined in the Jenkinsfile call  the REST endpoints of your ABAP system to trigger unit tests, ATC checks etc.  
 - The calls to the ABAP system are defined in the *postman collections* that are also stored in your Git server in a different project as they can be re-used in different ABAP projects. The system specific data (except for user and password) are stored in the Postman environment files. 
 - Newman as CLI executes the collections and allows via JavaScript to parse the results and transfer the output to the console (for sure you can also use the Blue Ocean plugin of Jenkins to have a nice visualization of your pipeline. 

*Remark - Storage of credentials*: as common practise do **not** store you credentials in any of the files mentioned above. The temapltes in this repository assume that the passwords are stored in the Jenkins credendtials store and injected into the scripts in a secure manner.

The workflow is also depicted here:
![CI Workflow](https://github.com/lechnerc77/ABAP_CI_PIPELINE_BASE/blob/master/img/Pipeline_Schema.jpg?raw=true)

*Please be aware that this workflow will not replace the SAP transport management!*

## Structure

The repos has the following structure:

 - folder 01_ABAP_Unit contains the necessary Postman collection and environment files to execute unit tests
 - folder 02_ABAP_Coverage contains the necessary Postman collection and environment files to check the test coverage
 - folder 03_ATC_Checks contains the necessary Postman collection and environment files to execute the ATC checks for SAP HANA readiness (i. e. `FUNCTIONAL_DB` and `FUNCTIONAL_DB_ADDITION`)
 - The `pipelineScript.groovy` represents the Jenkins file with the build steps. The stages and steps execute aforementioned collections via newman. The ATC checks are executed in parallel. 

The templates assume that your build shall be triggered on project level which means that all checks are executed on ABAP package level.

## How to make it work?
The templates will not work out-of-the-box and adoptions need to be made in order to make them work in your environment. Here are the basic steps:
 - Adopt all environment files (naming convention `*.postman_environment.json`) to your system landsacpe i. e. enter the hostname of your SAP system, the port and the client where the REST APIs shall be called. 
 - Store the `*.postman_environment.json` and the  `*.postman_collection.json`files on you Git server in a dedicated project. The idea is to have them separated from the ABAP projects as they should be re-usable. Which of the files should be used is steered via the Jenkins file in your ABAP project
 - Adopt the Jenkins file i. e. put the right location of the Postman collection and environment files from the previous steps. Then define the package that is in focus of the build as global variable in your Newman calls. Finally add the ID of your credentials in Jenkins that are needed to connect to your SAP ABAP system. 

There should be no need to adopt the collections as they are parameterized via the environment files and global variables injected via the call of newman.