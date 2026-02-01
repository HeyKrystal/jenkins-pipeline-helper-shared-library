/**
 * General-purpose deployment target helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 * 
 * Targets must be defined via the DEPLOYMENT_TARGETS_JSON environment variable.
 * For an example JSON structure, refer to resources/examples/deploy-targets-example.json
 */
package helpers.core

import groovy.json.JsonSlurper

class Targets implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    // Variables for caching loaded targets
    private Map _cfgCache
    private Map<String, Target> _targetCache

    /**
     * Constructor
     */
    Targets(def script) {
        this.script = script
        this._targetCache = [:]
    }

    /**
     * Get properties of a target by name.
     */
    String getHost (String name) {
        return getTarget(name).host
    }

    String getUser (String name) {
        return getTarget(name).user
    }

    String getDeployRoot(String name) {
        return getTarget(name).deployRoot
    }

    String getOS(String name) {
        return getTarget(name).os
    }

    /**
     * Get a single Target by name.
     */
    private Target getTarget(String name) {
        return _targetCache[name] ?: (_targetCache[name] = loadTarget(name))
    }

    /**
     * Load a single Target by name and convert Map -> Target.
     */
    private Target loadTarget(String name) {
        Map cfg = loadTargetsConfig()
        Map targets = (Map) (cfg.targets ?: [:])
        Map target = (Map) targets.get(name)

        if (target == null) {
            throw new IllegalArgumentException("Deploy target not defined for: ${name}")
        }

        validateTarget(name, target)

        return new Target(
            name,
            (String) target.host,
            (String) target.user,
            (String) target.deployRoot,
            (String) target.os
        )
    }

    /**
     * Parse the JSON once and cache it.
     */
    private Map loadTargetsConfig() {
        if (_cfgCache != null) return _cfgCache

        def raw = script.env.DEPLOYMENT_TARGETS_JSON ?: '{}'
        _cfgCache = (Map) new JsonSlurper().parseText(raw)

        return _cfgCache
    }

    /**
     * Validate required fields are present.
     */
    private void validateTarget(String name, Map target) {
        ['host', 'user', 'deployRoot', 'os'].each { String key ->
            def value = target.get(key)
            if (value == null || value.toString().trim().isEmpty()) {
                throw new IllegalStateException("Deploy target ${name} missing required field: ${key}")
            }
        }
    }
    
    /**
     * Private helper class
     */
    private static class Target implements Serializable {
        final String name
        final String host
        final String user
        final String deployRoot
        final String os

       /**
        * Constructor
        */
        Target(String name, String host, String user, String deployRoot, String os) {
            this.name = name
            this.host = host
            this.user = user
            this.deployRoot = deployRoot
            this.os = os
        }
    }
}
