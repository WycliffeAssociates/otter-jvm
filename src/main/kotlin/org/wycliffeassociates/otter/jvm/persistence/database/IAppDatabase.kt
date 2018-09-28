package org.wycliffeassociates.otter.jvm.persistence.database

import org.wycliffeassociates.otter.jvm.persistence.database.daos.*

// interface for a particular app database implementation
interface IAppDatabase {
    fun getLanguageDao(): ILanguageDao
    fun getResourceMetadataDao(): IResourceMetadataDao
    fun getCollectionDao(): ICollectionDao
    fun getChunkDao(): IChunkDao
    fun getResourceLinkDao(): IResourceLinkDao
    fun getTakeDao(): ITakeDao
    fun getMarkerDao(): IMarkerDao
}