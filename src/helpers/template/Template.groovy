/**
 * General-purpose Template helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Side-effect free (unless explicitly documented)
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

    String groovyWorld() {
        return "Groovy World!"
    }
}