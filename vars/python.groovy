/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.python.PythonValidate
import helpers.python.PythonBuild
import helpers.python.PythonDeploy

// Singleton instance
@Field private PythonValidate _pythonValidateInstance
@Field private PythonBuild _pythonBuildInstance
@Field private PythonDeploy _pythonDeployInstance

// Returns a singleton PythonValidate instance.
def getPythonValidateInstance() {
    if (_pythonValidateInstance == null) {
        _pythonValidateInstance = new PythonValidate(this)
    }
    return _pythonValidateInstance
}

// Returns a singleton PythonBuild instance.
def getPythonBuildInstance() {
    if (_pythonBuildInstance == null) {
        _pythonBuildInstance = new PythonBuild(this)
    }
    return _pythonBuildInstance
}

// Returns a singleton PythonDeploy instance.
def getPythonDeployInstance() {
    if (_pythonDeployInstance == null) {
        _pythonDeployInstance = new PythonDeploy(this)
    }
    return _pythonDeployInstance
}

// Expose helper methods for easier access.
def void validate() { getPythonValidateInstance().validate() }
def void build() { getPythonBuildInstance().build() }
def void deploy(String targetName, String sshCredentialsId) { getPythonDeployInstance().deploy(targetName, sshCredentialsId) }