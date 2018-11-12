package org.wycliffeassociates.otter.jvm.persistence.mapping

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.jvm.persistence.entities.AudioPluginEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.AudioPluginDataMapper
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
            1
    )

    private val pluginData = AudioPluginData(
            0,
            "ocenaudio",
            "1.0.0",
            true,
            true,
            "/path/to/bin/ocenaudio",
            listOf("none"),
            File("")
    )

    @Test
    fun shouldMapEntityToPluginData() {
        entity.edit = 1
        entity.record = 1
        pluginData.canEdit = true
        pluginData.canRecord = true
        val result = pluginMapper.mapFromEntity(entity)
        Assert.assertEquals(pluginData, result)
    }

    @Test
    fun shouldMapEntityToPluginDataWhenBooleansFalse() {
        entity.edit = 0
        entity.record = 0
        entity.args = "-o"
        pluginData.canEdit = false
        pluginData.canRecord = false
        pluginData.args = listOf("-o")
        val result = pluginMapper.mapFromEntity(entity)
        Assert.assertEquals(pluginData, result)
    }

    @Test
    fun shouldMapPluginDataToEntity() {
        pluginData.canEdit = true
        pluginData.canRecord = true
        pluginData.args = listOf("-o")
        entity.edit = 1
        entity.record = 1
        entity.args = "-o"
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