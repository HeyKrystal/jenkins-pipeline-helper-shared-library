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

class Jenkins implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Jenkins(def script) {
        this.script = script
    }

    /**
     * Perform a checkout of the current SCM and record the commit hash.
     */
    public void checkout() {
        script.checkout(script.scm)
        script.sh 'git rev-parse HEAD > GIT_COMMIT.txt'
    }

    /**
     * Returns the total build time in seconds.
     */
    int getBuildTimeInSeconds() {
        long startTime = script.currentBuild.startTimeInMillis ?: System.currentTimeMillis()
        long duration = script.currentBuild.duration ?: 0L
        long endTime = startTime + duration
        return (int) ((endTime - startTime) / 1000L)
    }
}