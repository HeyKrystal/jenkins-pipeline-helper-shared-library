/**
 * General-purpose Discord helper utility.
 *
 * Intentionally:
 * - Stateless
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
    Discord(def script) {
        this.script = script
        this.gitInfo = new GitInfo(script)
        this.jenkins = new Jenkins(script)
    }

    /**
     * Determine author text based on build result.
     */
    private Tuple2<String, int> authorAndColorFromResult(String result) {
        
        switch(result?.toUpperCase()) {
            case "SUCCESS":
                return new Tuple2(script.env.DEPLOYED == "true" ? "✅ DEPLOY COMPLETE" : "✅ BUILD COMPLETE", 0x2ECC71) // Green
            case "UNSTABLE":
                return new Tuple2("⚠️ BUILD UNSTABLE", 0xF1C40F) // Yellow
            case "FAILURE":
                return new Tuple2("❌ BUILD FAILED", 0xE74C3C) // Red
            case "ABORTED":
                return new Tuple2("🛑 BUILD ABORTED", 0xE74C3C) // Red
            case "NOT_BUILT":
                return new Tuple2("🤔 BUILD NOT BUILT", 0xF1C40F) // Yellow
            default:
                return new Tuple2("🌀 UNKNOWN BUILD RESULT", 0x3498DB) // Blue for unknown
        }
    }

    /**
     * Build the JSON payload for Discord embeded message.
     */
    private String buildEmbedJSONPayload() {

        // Reference to environment
        def env = script.env

        // Precalculate some json members.
        int durationSeconds = jenkins.getBuildTimeInSeconds()
        String durationText = (durationSeconds > 60 ? String.format("%dm %ds", durationSeconds / 60, durationSeconds % 60) : "${durationSeconds}s")
        String buildResult = currentBuild.currentResult ?: "UNKNOWN"
        def (authorTextFromResult, severityColorFromResult) = authorAndColorFromResult(buildResult)

        // Generate json payload.
        String discordPayload = JsonOutput.toJson([
          username: "${env.NODE_NAME_ALT ?: env.NODE_NAME ?: 'Jenkins'}",
          avatar_url: "${env.NODE_ICON_URL}",
          embeds: [
            [
              author: [ name: "${authorTextFromResult}" ],
              color: severityColorFromResult,
              title: "${env.JOB_NAME}",
              url: "${env.BUILD_URL}",
              description: "${gitInfo.lastCommitMessage()}",
              fields: [
                [ name: "Agent", value: "${env.NODE_NAME ?: 'Jenkins Node'}", inline: true ],
                [ name: "Duration", value: "${durationText}", inline: true ],
                [ name: "Build #", value: env.BUILD_NUMBER, inline: true ],
                [ name: "Git SHA", value: "${gitInfo.longCommitSha()}", inline: false ],
              ],
              footer: [ text: "IronKerberos • Jenkins" ]
            ]
          ]
        ])

        return discordPayload
    }

    /**
     * Send a Discord embed message via webhook.
     */
    public sendBuildResultNotification(String webhookCredentialId) {

        script.withCredentials([script.string(credentialsId: webhookCredentialId, variable: 'DISCORD_WEBHOOK_URL')]) {
            def webhookUrl = script.env.DISCORD_WEBHOOK_URL
            script.sh """
                set -eu
                printf '%s' '${buildEmbedJSONPayload()}' | \
                    curl -sS -H 'Content-Type: application/json' \
                        -d @- \
                        "$DISCORD_WEBHOOK_URL" > /dev/null
            """
        }
    }
}