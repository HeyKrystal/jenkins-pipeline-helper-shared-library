/**
 * Constants class that contains default values for library settings.
 *
 * Intentionally:
 * - Constant values only
 *
 * Designed for readability and predictability.
 */
package helpers.defaults

class DefaultsConstants {

    // ===== General defaults =====
    static final int DEFAULT_KEEP_RELEASES = 5

    // ===== Python defaults =====
    static final String DEFAULT_PYTHON_DOCKER_IMAGE = 'python:3.12-slim'

    // ===== Java defaults =====
    static final String DEFAULT_JDK_TOOL = 'temurin-21'  // example if you ever use tools
}