package org.wycliffeassociates.otter.jvm.device.audioplugin.parser

import audioplugin.yamlparser.ParsedAudioPluginData
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import java.io.File

class ParsedAudioPluginDataMapper {
    // Map from Jackson parser class to an AudioPlugin
    // No need to map the other way
    fun mapToAudioPluginData(yamlAudioPlugin: ParsedAudioPluginData, yamlSourceFile: File): AudioPluginData {
        val osName = System.getProperty("os.name").toUpperCase()

        // Get the executable for the system we are running on
        val executable = if (osName.contains("WIN"))
            yamlAudioPlugin.executable.windows
        else if (osName.contains("MAC"))
            yamlAudioPlugin.executable.macos
        else yamlAudioPlugin.executable.linux

        // Return the audio plugin or through an UnsupportedPlatformException
        // if no executable was given for this platform
        return AudioPluginData(
                0,
                yamlAudioPlugin.name,
                yamlAudioPlugin.version,
                yamlAudioPlugin.canEdit,
                yamlAudioPlugin.canRecord,
                executable ?: throw UnsupportedPlatformException(),
                yamlAudioPlugin.args,
                yamlSourceFile
        )
    }
}