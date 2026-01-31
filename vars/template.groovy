/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.template.Template

// Singleton instances
@Field private Template _templateInstance

// Returns a singleton Template instance.
def getInstance() {
    if (_templateInstance == null) {
        _templateInstance = new Template(this)
    }
    return _templateInstance
}

// Expose helper methods for easier access.
def String groovyWorld() { getInstance().groovyWorld() }