/**
 * General-purpose Discord helper utility.
 *
 * Intentionally:
 * - Stateless
 * - Side-effect free (unless explicitly documented)
 * - Return values instead of chaining
 *
 * Designed for readability and predictability.
 */
package helpers.discord

import groovy.json.JsonOutput
import helpers.core.Jenkins
import helpers.git.GitInfo

class Discord implements Serializable {
    
    // Reference to the Jenkins script context
    private final def script

    // Reference to helper singletons.
    private final GitInfo gitInfo
    private final Jenkins jenkins

    /**
     * Constructor
     */
    Discord(def script, GitInfo gitInfo) {
        this.script = script
        this.gitInfo = new GitInfo(script)
        this.jenkins = new Jenkins(script)
    }

    private String authorTextFromResult(String result) {
        
        switch(result?.toUpperCase()) {
            case "SUCCESS":
                return (script.env.DEPLOYED == "true" ? "✅ DEPLOY COMPLETE" : "✅ BUILD COMPLETE") // Green
            case "UNSTABLE":
                return "⚠️ BUILD UNSTABLE" // Yellow
            case "FAILURE":
                return "❌ BUILD FAILED" // Red
            case "ABORTED":
                return "🛑 BUILD ABORTED" // Red
            case "NOT_BUILT":
                return "🤔 BUILD NOT BUILT" // Yellow
            default:
                return "🌀 UNKNOWN BUILD RESULT" // Blue for unknown
        }
    }

    private severityColorFromResult(String result) {

        switch(result?.toUpperCase()) {
            case "SUCCESS":
                return 0x2ECC71 // Green
            case "UNSTABLE":
                return 0xF1C40F // Yellow
            case "FAILURE":
                return 0xE74C3C // Red
            case "ABORTED":
                return 0xE74C3C // Red
            case "NOT_BUILT":
                return 0xF1C40F // Yellow
            default:
                return 0x3498DB // Blue for unknown
        }
    }

    private String buildEmbedJSONPayload() {

        def env = script.env
        String discordPayload = JsonOutput.toJson([
          username: "${env.NODE_NAME_ALT ?: env.NODE_NAME ?: 'Jenkins'}",
          avatar_url: "${env.NODE_ICON_URL}",
          embeds: [
            [
              author: [ name: "${authorTextFromResult(env.BUILD_STATUS)}" ],
              color: severityColorFromResult(env.BUILD_STATUS),
              title: "${env.JOB_NAME}",
              url: "${env.BUILD_URL}",
              description: "Jenkins built commit `${sha}` on branch `${env.BRANCH_NAME}`.\n" +
                           (deployed == "true" ? "Deployed to *${env.DEPLOY_TARGET}*." : "No deployment performed."),
              fields: [
                [ name: "Agent", value: "${env.NODE_NAME ?: 'n/a'}", inline: true ],
                [ name: "Duration", value: "${jenkins.getBuildTimeInSeconds()} seconds", inline: true ],
                [ name: "Build #", value: env.BUILD_NUMBER, inline: true ],
                [ name: "Git SHA", value: sha, inline: true ],
              ],
              footer: [ text: "IronKerberos • Jenkins" ]
            ]
          ]
        ])

        return discordPayload
    }

    public sendDiscordEmbed(String webhookUrl) {

        sh """
            set -eu
            printf '%s' '${buildEmbedJSONPayload()}' | \
                curl -sS -H 'Content-Type: application/json' \
                    -d @- \
                    "${webhookUrl}" > /dev/null
        """
    }
}