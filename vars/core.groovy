/**
 * Jenkins automatically instantiates vars/*.groovy files.
 */
import groovy.transform.Field
import helpers.core.Jenkins

// Singleton instances
@Field private Jenkins _jenkinsInstance
@Field private Jenkins _validateInstance

// Returns a singleton Jenkins instance.
def getJenkinsInstance() {
    if (_jenkinsInstance == null) {
        _jenkinsInstance = new Jenkins(this)
    }
    return _jenkinsInstance
}
// Returns a singleton Validate instance.
def getValidateInstance() {
    if (_validateInstance == null) {
        _validateInstance = new Jenkins(this)
    }
    return _validateInstance
}

// Expose helper methods for easier access.
def void checkout() { getJenkinsInstance().checkout() }
// def void validate(List languages) { getValidateInstance().validate(languages) }