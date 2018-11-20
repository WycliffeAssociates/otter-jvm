package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.jvm.persistence.entities.AudioPluginEntity
import java.io.File

class AudioPluginDataMapperTest {
    // unit under test
    private val pluginMapper = AudioPluginDataMapper()

    private val entity = AudioPluginEntity(
            0,
            "ocenaudio",
            "1.0.0",
            "/path/to/bin/ocenaudio",
            "none",
            1,
            1,
            null
    )

    private val pluginData = AudioPluginData(
            0,
            "ocenaudio",
            "1.0.0",
            true,
            true,
            "/path/to/bin/ocenaudio",
            listOf("none"),
            null
    )

    @Test
    fun shouldMapEntityToPluginDataNullPath() {
        entity.edit = 1
        entity.record = 1
        entity.path = null
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.pluginFile = null
        val result = pluginMapper.mapFromEntity(entity)
        Assert.assertEquals(pluginData, result)
    }

    @Test
    fun shouldMapEntityToPluginDataNotNullPath() {
        entity.edit = 1
        entity.record = 1
        entity.path = File("./plugin.yaml").absolutePath
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.pluginFile = File("./plugin.yaml").absoluteFile
        val result = pluginMapper.mapFromEntity(entity)
        Assert.assertEquals(pluginData, result)
    }

    @Test
    fun shouldMapEntityToPluginDataWhenBooleansFalse() {
        entity.edit = 0
        entity.record = 0
        entity.args = "-o"
        entity.path = null
        pluginData.canRecord = false
        pluginData.canEdit = false
        pluginData.args = listOf("-o")
        val result = pluginMapper.mapFromEntity(entity)
        Assert.assertEquals(pluginData, result)
    }

    @Test
    fun shouldMapPluginDataToEntityNullFile() {
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.args = listOf("-o")
        pluginData.pluginFile = null
        entity.edit = 1
        entity.record = 1
        entity.args = "-o"
        entity.path = null
        val result = pluginMapper.mapToEntity(pluginData)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapPluginDataToEntityNotNullFile() {
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.args = listOf("-o")
        pluginData.pluginFile = File("./plugin.yaml").absoluteFile
        entity.edit = 1
        entity.record = 1
        entity.args = "-o"
        entity.path = File("./plugin.yaml").absolutePath
        val result = pluginMapper.mapToEntity(pluginData)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapPluginDataToEntityWhenBooleansFalse() {
        pluginData.canEdit = false
        pluginData.canRecord = false
        pluginData.args = listOf("-o")
        entity.edit = 0
        entity.record = 0
        entity.args = "-o"
        val result = pluginMapper.mapToEntity(pluginData)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapPluginDataToEntityNoArgs() {
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.args = listOf()
        entity.edit = 1
        entity.record = 1
        entity.args = ""
        val result = pluginMapper.mapToEntity(pluginData)
        Assert.assertEquals(entity, result)
    }
}