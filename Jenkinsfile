pipeline {
    agent any

    tools {
        jdk "jdk21"
    }

    triggers {
        upstream 'authentication, '
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test || true'
                junit '**/target/surefire-reports/*.xml'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                sh 'cp /home/ubuntu/.jenkins/workspace/finance-manager-backend/target/finance-manager-0.1.jar /home/ubuntu/applications'
                sh 'JENKINS_NODE_COOKIE=dontKillMe /home/ubuntu/scripts/start.sh &'
            }
        }
    }
}