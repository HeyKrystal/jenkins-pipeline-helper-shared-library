/**
 * Deploy-focused Python helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.python

import helpers.defaults.Defaults
import helpers.core.Targets

class PythonDeploy implements Serializable { 

    // Reference to the Jenkins script context
    private final def script

    // Reference to Defaults helper
    private final Defaults defaults

    /**
     * Constructor
     */
    PythonDeploy(def script) {
        this.script = script
        this.defaults = Defaults.of(script)
    }

    /**
     * Starts the Python deploy process.
     */
    public void deploy(String targetName, String sshCredentialsId) {

        // Pre calculate helper valuess
        def env = script.env
        Targets targets = new Targets(script)

        // Set environment variables for target
        env.TARGET_HOST = targets.getHost(targetName)
        env.TARGET_USER = targets.getUser(targetName)
        env.TARGET_DEPLOY_ROOT = targets.getDeployRoot(targetName)
        env.TARGET_OS = targets.getOS(targetName)

        // Set environment variables for deploy
        env.TARGET_APP_DIRECTORY = "${env.TARGET_DEPLOY_ROOT}/${defaults.repoName()}"
        env.TARGET_RELEASES_DIR = "${env.TARGET_APP_DIRECTORY}/releases"
        env.TARGET_RELEASE_DIR = "${env.TARGET_RELEASES_DIR}/${env.RELEASE_TAG}"
        env.TARGET_CURRENT_LINK = "${env.TARGET_APP_DIRECTORY}/current"

        switch (targets.getOS(targetName).toLowerCase()) {
            case { it.contains('linux') }:
                deployUnix(targetName, sshCredentialsId)
                break
            case { it.contains('macos') }:
                deployUnix(targetName, sshCredentialsId)
                break
            case { it.contains('windows') }:
            default:
                throw new IllegalStateException("Unsupported OS: ${target.os}")
        }

        env.DID_DEPLOY = "true"
    }


    /**
     * Private OS specific deploy steps for Linux/MacOS
     */
    private void deployUnix(String targetName, String sshCredentialsId) {
        
        // Set credentials
        script.withCredentials([sshUserPrivateKey(credentialsId: sshCredentialsId,
                                           keyFileVariable: 'SSH_KEY',
                                           usernameVariable: 'SSH_USER')]) {
            
            // Run deploy steps
            def cmd = '''
              set -eux

              # Make sure target dirs exist
              ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SSH_USER@$TARGET_HOST" "
              set -eux
              mkdir -p '$T_RELEASES_DIR'
              "

              # Copy the artifact to target temp
              scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$ARTIFACT_PATH" \
              "$SSH_USER@$TARGET_HOST:/tmp/$ARTIFACT_NAME"

              # Extract to new release dir, flip current symlink, keep last N releases
              ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SSH_USER@$TARGET_HOST" "
              set -eux

              # Optional runtime preflight (doesn't reinstall anything)
              command -v ffmpeg >/dev/null 2>&1 || echo 'WARN: ffmpeg not found in PATH on FrostedStoat'
              command -v yt-dlp >/dev/null 2>&1 || echo 'WARN: yt-dlp not found in PATH on FrostedStoat'
              command -v curl  >/dev/null 2>&1 || echo 'WARN: curl not found in PATH on FrostedStoat'

              mkdir -p '$T_RELEASE_DIR'
              tar -xzf '/tmp/$ARTIFACT_NAME' -C '$T_RELEASE_DIR'
              rm -f '/tmp/$ARTIFACT_NAME'

              # Atomic-ish cutover: update the symlink in one operation
              ln -sfn '$T_RELEASE_DIR' '$T_CURRENT_LINK'

              # Cleanup: keep the newest $KEEP_RELEASES release directories
              # (based on modification time)
              cd '$T_RELEASES_DIR'
              if [ -d . ]; then
                  ls -1dt ./* 2>/dev/null | tail -n +$((KEEP_RELEASES+1)) | xargs -I{} rm -rf \"{}\"
              fi
              "
            '''

            script.sh(cmd)
        }
    }

    /**
     * Private OS specific deploy steps for Windows
     */
    private void deployWindows(String targetName, String sshCredentialsId) {
        
        // Not supported currently.
    }
}