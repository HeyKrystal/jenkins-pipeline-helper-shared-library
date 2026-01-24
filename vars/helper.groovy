/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return a JenkinsHelper instance explicitly for clarity.
 */
import helpers.JenkinsHelper

// Singleton instance
@Field private JenkinsHelper _instance

// Returns a singleton JenkinsHelper instance.
def getInstance() {
    if (_instance == null) {
        _instance = new JenkinsHelper(this)
    }
    return _instance
}

// Expose helper methods for easier access.
def repoName() { getInstance().repoName() }
def isMainBranch() { getInstance().isMainBranch() }
def shortCommit() { getInstance().shortCommit() }