package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.common.persistence.repositories.IResourceRepository

class ResourcesDao: IResourceRepository {
    override fun getByChunk(chunk: Chunk): Single<List<Resource>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByCollection(collection: Collection): Single<List<Resource>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateChunkLink(resource: Resource, chunk: Chunk): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCollectionLink(resource: Resource, collection: Collection): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(obj: Resource): Single<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): Single<List<Resource>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(obj: Resource): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(obj: Resource): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}