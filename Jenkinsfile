pipeline {
    agent any

    tools {
        jdk "jdk21"
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test || true'
                junit '**/target/*.xml'
            }
        }
        stage('Deploy-m2') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                sh 'mvn install -DskipTests'
            }
        }
        stage('Deploy') {
            steps {
                sh 'cp /home/ubuntu/.jenkins/workspace/finance-manager-backend/target/finance-manager-0.1.jar /home/ubuntu'
                sh './scripts/start.sh'
            }
        }
    }
}