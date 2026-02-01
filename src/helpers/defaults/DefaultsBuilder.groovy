/**
 * Builder for setting default configuration values.
 *
 * Intentionally:
 * - Stateless
 * - Built for chaining
 * - Optional to use during init stage
 *
 * Designed for readability and predictability.
 */
package helpers.defaults

class DefaultsBuilder implements Serializable {

    // Reference to Defaults used for this builder.
    private final Defaults defaults

    /**
     * Constructor
     */
    DefaultsBuilder(def script) {
        this.defaults = Defaults.of(script)
    }

    /*
     * Chaining methods for setting defaults
     */
    // ===== General defaults =====
    DefaultsBuilder withKeepReleases(int n) {
        defaults.setKeepReleases(n)
        return this
    }

    // ===== Python defaults =====
    DefaultsBuilder withPythonValidationDockerImage(String image) {
        defaults.setPythonValidationDockerImage(image)
        return this
    }

    // ===== Java defaults =====
    DefaultsBuilder withJDKTool(String image) {
        defaults.setJDKTool(image)
        return this
    }
}