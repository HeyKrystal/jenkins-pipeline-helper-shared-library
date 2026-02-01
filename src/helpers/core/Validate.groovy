/**
 * Validation helper for multilingual projects.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.core

class Validate implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Validate(def script) {
        this.script = script
    }

    /**
     * Perform a checkout of the current SCM and record the commit hash.
     */
    public void validate(List languages) {

        languages.each { lang ->
            switch (lang.toLowerCase()) {
                case 'python':
                    script.echo "Validating Python"
                    PythonValidate pythonValidate = new PythonValidate(script).validate()
                    break
                case 'shell':
                    script.echo "Validating Shell"
                    ShellValidate shellValidate = new ShellValidate(script).validate()
                    break
                default:
                    throw new IllegalStateException("Validation requested for unsupported language: ${lang}")
            }
        }
    }
}