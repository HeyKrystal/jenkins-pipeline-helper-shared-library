/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.core.Jenkins

// Singleton instances
@Field private Jenkins _jenkinsInstance

// Returns a singleton Jenkins instance.
def getInstance() {
    if (_jenkinsInstance == null) {
        _jenkinsInstance = new Jenkins(this)
    }
    return _jenkinsInstance
}

// Expose helper methods for easier access.
def void checkout() { getInstance().checkout() }