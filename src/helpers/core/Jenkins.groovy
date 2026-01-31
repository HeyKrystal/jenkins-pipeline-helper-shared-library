/**
 * General-purpose Jenkins helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Side-effect free (unless explicitly documented)
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.core

class Jenkins implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Jenkins(def script) {
        this.script = script
    }

    int getBuildTimeInSeconds() {
        long startTime = script.currentBuild.startTimeInMillis ?: System.currentTimeMillis()
        long duration = script.currentBuild.duration ?: 0L
        long endTime = startTime + duration
        return (endTime - startTime) / 1000L
    }
}