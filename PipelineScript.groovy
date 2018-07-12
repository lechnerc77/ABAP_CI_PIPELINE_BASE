pipeline {
   agent any
   stages{
         stage('ABAP Unit Test'){
               steps{
                     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ID OF YOUR CREDENTIALS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                     sh '''
                     newman run https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/01_ABAP_Unit/abap_unit.postman_collection.json -k --bail --environment https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/01_ABAP_Unit/abap_unit.postman_environment.json -k --timeout-request 120000 --global-var "username=$USERNAME" --global-var "password=$PASSWORD" --global-var "package=NAME OF YOUR PROJECT/PACKAGE" 
                     '''
                     }
                }
         }
        stage('ABAP Test Coverage'){
               steps{
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ID OF YOUR CREDENTIALS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh ''' 
                    newman run https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/02_ABAP_Coverage/abap_coverage_analysis.postman_collection.json -k --bail --environment https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/02_ABAP_Coverage/abap_coverage.postman_environment.json --timeout-request 120000 --global-var "username=$USERNAME" --global-var "password=$PASSWORD" --global-var "package=NAME OF YOUR PROJECT/PACKAGE" 
                    '''
                    }
               }
         }
        stage('ATC Checks'){
              parallel{
                    stage('ATC Checks HANA Readiness - FUNCTIONAL_DB'){
                        steps{
                            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ID OF YOUR CREDENTIALS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                            sh ''' 
                           newman run https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/03_ATC_Checks/abap_atc.postman_collection.json -k --bail --environment https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/03_ATC_Checks/abap_atc_funcdb.postman_environment.json --timeout-request 120000 --global-var "username=$USERNAME" --global-var "password=$PASSWORD" --global-var "package=NAME OF YOUR PROJECT/PACKAGE"
                           '''
                           }
                        }
                     }
                     stage('ATC Checks HANA Readiness - FUNCTIONAL_DB_ADDITION'){
                           steps{
                           withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ID OF YOUR CREDENTIALS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                            sh ''' 
                           newman run https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/03_ATC_Checks/abap_atc.postman_collection.json -k --bail --environment https://raw.githubusercontent.com/lechnerc77/ABAP_CI_PIPELINE_BASE/master/03_ATC_Checks/abap_atc_funcdbadd.postman_environment.json --timeout-request 120000 --global-var "username=$USERNAME" --global-var "password=$PASSWORD" --global-var "package=NAME OF YOUR PROJECT/PACKAGE"
                           '''
                           }
                        }
                     }
               }
         }      
   }
}