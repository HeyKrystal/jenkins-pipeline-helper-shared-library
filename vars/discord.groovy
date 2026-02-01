/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.discord.Discord

// Singleton instance
@Field private Discord _instance

// Returns a singleton Discord instance.
def getInstance() {
    if (_instance == null) {
        _instance = new Discord(this)
    }
    return _instance
}

// Expose helper methods for easier access.
def void sendDiscordEmbed(String webhookCredentialId) { getInstance().sendDiscordEmbed(webhookCredentialId) }