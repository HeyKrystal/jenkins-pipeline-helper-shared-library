/**
 * Info-focused Git helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Side-effect free (unless explicitly documented)
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */

package helpers.git

class GitInfo implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    /**
     * Constructor
     */
    GitInfo(def script) {
        this.script = script
    }

    /**
     * Optional: first line only (subject).
     */
    String lastCommitMessage() {
        def msg = triggerCommitMessage()
        if (!msg) return null
        return msg.readLines().find { it?.trim() }?.trim()
    }

    /**
     * Best-effort commit message for the commit that triggered this build.
     *
     * Strategy:
     *  1) Use Jenkins changelog (no shell) if available.
     *  2) Fall back to `git log -1` in the workspace (requires checkout + git present).
     *
     * Returns null if it cannot be determined.
     */
    String triggerCommitMessage() {
        // 1) Try Jenkins changelog first (no shell)
        try {
            def cs = script.currentBuild?.changeSets
            if (cs) {
                for (def set : cs) {
                    for (def item : set.items) {
                        // In multibranch pipelines this is often populated
                        def msg = item?.msg
                        if (msg) return msg.toString().trim()
                    }
                }
            }
        } catch (Throwable ignored) {
            // If Jenkins doesn't provide changeSets in this context, ignore.
        }

        // 2) Fallback: `git log -1` (requires workspace + .git)
        try {
            // If there is no workspace / node context, this will throw (and we return null)
            def out = script.sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
            return out ? out : null
        } catch (Throwable ignored) {
            return null
        }
    }

    /**
     * Returns true if the current branch is 'main'.
     */
    boolean isMainBranch() {
        return (script.env.BRANCH_NAME == 'main')
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
        String gitUrl = script.env.GIT_URL

        // Return null if none set.
        if (!gitUrl) return null

        // Strip .git if present, then tokenize.
        gitUrl = gitUrl.replaceAll(/\.git$/, '')
        def parts = gitUrl.tokenize('/:')

        // Return the last part as the repo name.
        return parts ? parts.last() : null
    }

    /**
     * Returns a short (7-char) Git commit hash if available.
     */
    String shortCommit() {
        def commit = script.env.GIT_COMMIT
        return commit ? commit.take(7) : null
    }
}
