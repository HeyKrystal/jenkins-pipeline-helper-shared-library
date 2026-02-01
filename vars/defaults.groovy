/**
 * Jenkins automatically instantiates vars/*.groovy files.
 */
import helpers.defaults.DefaultsBuilder

// Expose helper methods for easier access.
def DefaultsBuilder override() { return new DefaultsBuilder(this) }