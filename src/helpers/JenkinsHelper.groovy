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
package helpers

class JenkinsHelper implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    Helpers(def script) {
        this.script = script
    }

    /**
     * Returns the repository name inferred from the Git URL.
     *
     * Examples:
     *   https://github.com/org/repo-name.git -> repo-name
     *   git@github.com:org/repo-name.git    -> repo-name
     *
     * Returns null if the repo cannot be determined.
     */
    String repoName() {
        def gitUrl = script.env.GIT_URL

        // Return null if none set.
        if (!gitUrl) return null

        // Strip .git if present, then tokenize.
        gitUrl = gitUrl.replaceAll(/\.git$/, '')
        def parts = gitUrl.tokenize('/:')

        // Return the last part as the repo name.
        return parts ? parts.last() : null
    }

    /**
     * Returns true if the current branch is 'main'.
     */
    boolean isMainBranch() {
        script.env.BRANCH_NAME == 'main'
    }

    /**
     * Returns a short (7-char) Git commit hash if available.
     */
    String shortCommit() {
        def commit = script.env.GIT_COMMIT
        return commit ? commit.take(7) : null
    }
}