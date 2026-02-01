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

    /**
     * Constructor
     */
    PythonValidate(def script) {
        this.script = script
        this.defaults = Defaults.of(script)
    }

    /**
     * Starts the Shell script validation process with dockerized linting and testing.
     */
    public void validate() {

        // Run dockerized validation
        def cmd = '''
            set -eu

            # Find shell scripts in the checked-out repo (exclude common generated/vendor dirs)
            files="$(find . -type f -name '*.sh' \
                -not -path './.git/*' \
                -not -path './.venv/*' \
                -not -path './.gradle/*' \
                -not -path './build/*' \
                -not -path './dist/*' \
                -not -path './node_modules/*' \
                -not -path './.ruff_cache/*' \
                -print)"

            if [ -z "$files" ]; then
                echo "No .sh files found; skipping ShellCheck."
                exit 0
            fi

            # Run ShellCheck in a container against the workspace
            docker run --rm \
                -u "$(id -u):$(id -g)" \
                -v "$PWD:/work" -w /work \
                koalaman/shellcheck:stable \
                sh -lc 'shellcheck -x '"$files"
            '''
        script.sh(cmd)
    }
}