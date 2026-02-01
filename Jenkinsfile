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
          ./gradlew test
        '''
      }
    }
  }

  post {
    always {
      junit 'build/test-results/test/*.xml'
    }
  }
}
