pipeline {
  agent { label 'oakbeaver' }

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Test') {
      steps {
        sh '''
        set -eu

        docker run --rm \
            -u "$(id -u):$(id -g)" \
            -v "$PWD:/work" -w /work \
            gradle:8-jdk17 \
            gradle test
        '''
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
    }
  }
}
