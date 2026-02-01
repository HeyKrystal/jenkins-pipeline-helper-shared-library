/**
 * General-purpose Template helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.Template

class Template implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Template(def script) {
        this.script = script
    }

    /**
     * Sample method
     */
    String groovyWorld() {
        return "Groovy World!"
    }
}