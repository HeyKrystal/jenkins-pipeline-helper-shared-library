/**
 * Validation-focused Python helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.python

import helpers.defaults.Defaults

class PythonValidate implements Serializable { 

    // Reference to the Jenkins script context
    private final def script

    // Reference to Defaults helper
    private final Defaults defaults
    private final Scripts scripts

    /**
     * Constructor
     */
    PythonValidate(def script) {
        this.script = script
        this.defaults = Defaults.of(script)
        this.scripts = new Scripts(script)
    }

    /**
     * Starts the Python validation process with dockerized linting and testing.
     */
    public void validate() {

        // Prepare docker initiation scripts
        def runner = scripts.writeExecutable(
            '.jenkins/scripts/python/validate-runner.sh',
            scripts.load('sh/python/validation.runner.sh')
                .replace('__DOCKER_IMAGE__', defaults.getPythonDockerImage())
        )

        // Prepare inner validation script
        def inner = scripts.writeExecutable(
            '.jenkins/scripts/python/validate-inner.sh',
            scripts.load('sh/python/validation.inner.sh')
        )

        // Run validation
        script.sh(runner)
    }
}