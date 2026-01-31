/**
 * Jenkins automatically instantiates vars/*.groovy files.
 * We return class instances explicitly for clarity.
 */
import groovy.transform.Field
import helpers.git.GitInfo

// Singleton instance
@Field private GitInfo _gitInfoInstance

// Returns a singleton GitInfo instance.
def getInstance() {
    if (_gitInfoInstance == null) {
        _gitInfoInstance = new GitInfo(this)
    }
    return _gitInfoInstance
}

// Expose helper methods for easier access.
def String lastCommitMessage() { getInstance().lastCommitMessage() }
def boolean isMainBranch() { getInstance().isMainBranch() } 
def String repoName() { getInstance().repoName() }
def String shortCommit() { getInstance().shortCommit() }