/**
 * General-purpose Jenkins helper utility class.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.core

class Scripts implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Scripts(def script) {
        this.script = script
    }
    
    /**
     * Load a library resource as text
     */
    private String load(String resourcePath) {
        return script.libraryResource(resourcePath)
    }

    /**
     * Write a script to the workspace and mark it executable.
     * Returns the absolute workspace path.
     */
    private String writeExecutable(String relativePath, String contents) {
        def fullPath = "${script.pwd()}/${relativePath}"

        script.writeFile file: fullPath, text: contents
        script.sh "chmod +x '${fullPath}'"

        return fullPath
    }

}