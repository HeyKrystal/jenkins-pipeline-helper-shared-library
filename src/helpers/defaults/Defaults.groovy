/**
 * General-purpose helper utility to acquiring and overriding default configuration values.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.defaults

class Defaults implements Serializable {

    // Reference to the Jenkins script context
    private final def script

    // Key used to store config map on script
    private static final String KEY = '__krstldv_defaults'

    /**
     * Constructor
     */
    private Defaults(def script) {
        this.script = script
    }

    /**
     * Factory method to get Defaults instance.
     */
    static Defaults of(def script) {
        return new Defaults(script)
    }

    /**
     * Returns current config map stored on script, or an empty map.
     */
    private Map cfg() {
        try {
            def map = script.getProperty(KEY)
            return (map instanceof Map) ? (Map) map : [:]
        } catch (MissingPropertyException ignored) {
            return [:]
        }
    }

    /**
     * Store updated config map on script.
     */
    private void put(String key, Object value) {
        Map map = new LinkedHashMap(cfg())
        map[key] = value
        script.setProperty(KEY, map)
    }

    /*
     * Getters with defaults
     */
    // ===== General defaults =====
    int getKeepReleases() {
        def v = cfg().get('keepReleases')
        return (v instanceof Number) ? ((Number) v).intValue() : DefaultsConstants.DEFAULT_KEEP_RELEASES
    }

    // ===== Python defaults =====
    String getPythonDockerImage() {
        def v = cfg().get('pythonDockerImage')
        return (v != null && v.toString().trim()) ? v.toString() : DefaultsConstants.DEFAULT_PYTHON_DOCKER_IMAGE
    }

    // ===== Java defaults =====
    String getJDKTool() {
        def v = cfg().get('jdkTool')
        return (v != null && v.toString().trim()) ? v.toString() : DefaultsConstants.DEFAULT_JDK_TOOL
    }

    /*
     * Mutators used by the builder
     */
    // ===== General defaults =====
    void setKeepReleases(int n) { put('keepReleases', n) }

    // ===== Python defaults =====
    void setPythonDockerImage(String s) { put('pythonDockerImage', s) }

    // ===== Java defaults =====
    void setJDKTool(String s) { put('jdkTool', s) }
}
