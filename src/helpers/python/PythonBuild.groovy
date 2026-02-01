/**
 * Build-focused Python helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.python

import helpers.defaults.Defaults
import helpers.git.GitInfo

class PythonBuild implements Serializable { 

    // Reference to the Jenkins script context
    private final def script

    // Reference to helpers helper
    private final Defaults defaults
    private final GitInfo gitInfo

    /**
     * Constructor
     */
    PythonBuild(def script) {
        this.script = script
        this.defaults = Defaults.of(script)
        this.gitInfo = new GitInfo(script)
    }

    /**
     * Starts the Python build process.
     */
    public void build() {

        // Pre calculate helper valuess
        def env = script.env
        def shortSha = defaults.shortCommitSha()

        // Set environment variables for build
        env.RELEASE_TAG = "${defaults.repoName()}-${env.BRANCH_NAME}-${env.BUILD_NUMBER}-${shortSha}"
        env.ARTIFACT_LOCATION = "dist/"
        env.ARTIFACT_NAME = "${env.RELEASE_TAG}.tar.gz"
        env.ARTIFACT_PATH = "${env.ARTIFACT_LOCATION}${env.ARTIFACT_NAME}"

        // Run build steps
        script.sh '''
          # Create a deployable tarball from the checked-out workspace
          set -eux
          mkdir -p $ARTIFACT_LOCATION
          git ls-files -z | tar --null -T - -czf "$ARTIFACT_PATH"
        '''
        script.archiveArtifacts artifacts: '$ARTIFACT_PATH,GIT_COMMIT.txt', fingerprint: true
    }
}