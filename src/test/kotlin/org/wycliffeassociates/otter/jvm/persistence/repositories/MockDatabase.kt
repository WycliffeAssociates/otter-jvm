package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.jooq.DSLContext
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.database.daos.ChunkDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.MarkerDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.TakeDao
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.TakeEntity

class MockDatabase {
    companion object {
        private fun chunkDao(): ChunkDao = mock {
            val dao = InMemoryDao<ChunkEntity>()
            on { insert(any(), anyOrNull()) }.then { input ->
                dao.insert(input.getArgument(0))
            }
            on { fetchById(any(), anyOrNull()) }.then { call ->
                dao.fetchById(call.getArgument(0))
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
        }
        fun database(): AppDatabase {
            val takeDao = takeDao()
            val markerDao = markerDao()
            val chunkDao = chunkDao()
            return mock {
                on { getTakeDao() } doReturn takeDao
                on { getMarkerDao() } doReturn markerDao
                on { getChunkDao() } doReturn chunkDao
                on { transaction(any()) }.then { input ->
                    input.getArgument<(DSLContext) -> Unit>(0)(mock())
                }
            }
        }
    }
}