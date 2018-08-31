package audioplugin.yamlparser

data class ParsedExecutable(
        // nullable since executable might not exist for a platform
        var macos: String?,
        var windows: String?,
        var linux: String?
)