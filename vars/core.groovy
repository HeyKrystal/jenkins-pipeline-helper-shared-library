/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.template.Template

// Singleton instances
@Field private Template _jenkinsInstance

// Returns a singleton Template instance.
def getInstance() {
    if (_jenkinsInstance == null) {
        _jenkinsInstance = new Template(this)
    }
    return _jenkinsInstance
}

// Expose helper methods for easier access.