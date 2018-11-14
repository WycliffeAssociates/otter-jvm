package org.wycliffeassociates.otter.jvm.persistence.repositories.test

import com.nhaarman.mockitokotlin2.*
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.database.daos.ChunkDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.LanguageDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.MarkerDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.TakeDao
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.TakeEntity

class MockDatabase {
    companion object {
        private fun languageDao(): LanguageDao = mock {
            val dao = InMemoryDao<LanguageEntity>()
            on { insert(any(), anyOrNull()) }.then { input ->
                dao.insert(input.getArgument(0))
            }
            on { insertAll(any(), anyOrNull()) }.then { input ->
                input.getArgument<List<LanguageEntity>>(0).map { entity -> dao.insert(entity) }
            }
            on { update(any(), anyOrNull()) }.then { input ->
                dao.update(input.getArgument(0), input.getArgument<LanguageEntity>(0).id)
            }
            on { delete(any(), anyOrNull()) }.then { input ->
                dao.delete(input.getArgument<LanguageEntity>(0).id)
            }
            on { fetchAll(anyOrNull()) }.then { call ->
                dao.fetchAll()
            }
            on { fetchBySlug(any(), anyOrNull()) }.then { call ->
                dao.fetchByProperty("slug", call.getArgument(0)).first()
            }
            on { fetchGateway(anyOrNull()) }.then { call ->
                dao.fetchByProperty("gateway", 1)
            }
            on { fetchTargets(anyOrNull()) }.then { call ->
                dao.fetchByProperty("gateway", 0)
            }
        }
        private fun chunkDao(): ChunkDao = mock {
            val dao = InMemoryDao<ChunkEntity>()
            val linkDao = InMemoryDao<Pair<Int, Int>>()
            on { insert(any(), anyOrNull()) }.then { input ->
                dao.insert(input.getArgument(0))
            }
            on { fetchAll(anyOrNull()) }.then {
                dao.fetchAll()
            }
            on { fetchById(any(), anyOrNull()) }.then { call ->
                dao.fetchById(call.getArgument(0))
            }
            on { fetchByCollectionId(any(), anyOrNull()) }.then { call ->
                dao.fetchByProperty("collectionFk", call.getArgument(0))
            }
            on { fetchSources(any(), anyOrNull()) }.then { call ->
                linkDao
                        .fetchByProperty("first", call.getArgument<ChunkEntity>(0).id)
                        .map {
                            dao.fetchById(it.second)
                        }
            }
            on { updateSources(any(), any(), anyOrNull()) }.then { call ->
                linkDao.fetchAll().filter {
                    it.first == call.getArgument<ChunkEntity>(0).id
                }.forEach { linkDao.delete(it) }
                call.getArgument<List<ChunkEntity>>(1).forEach {
                    linkDao.insert(Pair(call.getArgument<ChunkEntity>(0).id, it.id))
                }
            }
            on { update(any(), anyOrNull()) }.then { input ->
                dao.update(input.getArgument(0), input.getArgument<ChunkEntity>(0).id)
            }
            on { delete(any(), anyOrNull()) }.then { input ->
                dao.delete(input.getArgument<ChunkEntity>(0).id)
            }
        }
        private fun takeDao(): TakeDao = mock {
            val dao = InMemoryDao<TakeEntity>()
            on { insert(any(), anyOrNull()) }.then { input ->
                dao.insert(input.getArgument(0))
            }
            on { update(any(), anyOrNull()) }.then { input ->
                dao.update(input.getArgument(0), input.getArgument<TakeEntity>(0).id)
            }
            on { delete(any(), anyOrNull()) }.then { input ->
                dao.delete(input.getArgument<TakeEntity>(0).id)
            }
            on { fetchAll(anyOrNull()) }.then { call ->
                dao.fetchAll()
            }
            on { fetchByChunkId(any(), anyOrNull()) }.then { call ->
                dao.fetchByProperty("contentFk", call.getArgument(0))
            }
            on { fetchById(any(), anyOrNull()) }.then { call ->
                dao.fetchById(call.getArgument(0))
            }
        }
        private fun markerDao(): MarkerDao = mock {
            val dao = InMemoryDao<MarkerEntity>()
            on { insert(any(), anyOrNull()) }.then { input ->
                dao.insert(input.getArgument(0))
            }
            on { delete(any(), anyOrNull()) }.then { input ->
                dao.delete(input.getArgument<MarkerEntity>(0).id)
            }
            on { fetchByTakeId(any(), anyOrNull()) }.then { input ->
                dao.fetchByProperty<Int>("takeFk", input.getArgument(0))
            }
            on { fetchAll(anyOrNull()) }.then { input ->
                dao.fetchAll()
            }
        }
        fun database(): AppDatabase {
            val takeDao = takeDao()
            val markerDao = markerDao()
            val chunkDao = chunkDao()
            val languageDao = languageDao()
            return mock {
                on { getTakeDao() } doReturn takeDao
                on { getMarkerDao() } doReturn markerDao
                on { getChunkDao() } doReturn chunkDao
                on { getLanguageDao() } doReturn languageDao
                on { transaction(any()) }.then { input ->
                    input.getArgument<(DSLContext) -> Unit>(0)(mock())
                }
            }
        }
    }
}