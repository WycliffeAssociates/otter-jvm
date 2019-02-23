package org.wycliffeassociates.otter.jvm.persistence.database.daos

import jooq.Tables.RESOURCE_LINK
import org.jooq.*
import org.jooq.impl.DSL.max
import org.wycliffeassociates.otter.jvm.persistence.database.InsertionException
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceLinkEntity

class ResourceLinkDao(
        private val instanceDsl: DSLContext
) {
    fun fetchByContentId(id: Int, dsl: DSLContext = instanceDsl): List<ResourceLinkEntity> {
        return dsl
                .select()
                .from(RESOURCE_LINK)
                .where(RESOURCE_LINK.CONTENT_FK.eq(id))
                .fetch {
                    RecordMappers.mapToResourceLinkEntity(it)
                }
    }

    fun fetchByCollectionId(id: Int, dsl: DSLContext = instanceDsl): List<ResourceLinkEntity> {
        return dsl
                .select()
                .from(RESOURCE_LINK)
                .where(RESOURCE_LINK.COLLECTION_FK.eq(id))
                .fetch {
                    RecordMappers.mapToResourceLinkEntity(it)
                }
    }

    private fun insertImpl(entity: ResourceLinkEntity, dsl: DSLContext) {
        if (entity.id != 0) throw InsertionException("Entity ID is not 0")

        // Insert the resource link entity
        dsl
                .insertInto(
                        RESOURCE_LINK,
                        RESOURCE_LINK.RESOURCE_CONTENT_FK,
                        RESOURCE_LINK.CONTENT_FK,
                        RESOURCE_LINK.COLLECTION_FK,
                        RESOURCE_LINK.DUBLIN_CORE_FK
                )
                .values(
                        entity.resourceContentFk,
                        entity.contentFk,
                        entity.collectionFk,
                        entity.dublinCoreFk
                )
                .execute()
    }

    @Synchronized
    fun insert(entity: ResourceLinkEntity, dsl: DSLContext = instanceDsl): Int {
        insertImpl(entity, dsl)

        // Fetch and return the resulting ID
        return dsl
                .select(max(RESOURCE_LINK.ID))
                .from(RESOURCE_LINK)
                .fetchOne {
                    it.getValue(max(RESOURCE_LINK.ID))
                }
    }

    @Synchronized
    fun insertNoReturn(entity: ResourceLinkEntity, dsl: DSLContext = instanceDsl): Unit = insertImpl(entity, dsl)

    /** @param select a triple record containing values for main content ID, resource content ID, dublinCore ID */
    @Synchronized
    fun insertContentResourceNoReturn(select: Select<Record3<Int, Int, Int>>, dsl: DSLContext = instanceDsl): Unit {
        dsl
                .insertInto(
                        RESOURCE_LINK,
                        RESOURCE_LINK.CONTENT_FK,
                        RESOURCE_LINK.RESOURCE_CONTENT_FK,
                        RESOURCE_LINK.DUBLIN_CORE_FK
                )
                .select(select)
                .execute()
    }

    fun fetchById(id: Int, dsl: DSLContext = instanceDsl): ResourceLinkEntity {
        return dsl
                .select()
                .from(RESOURCE_LINK)
                .where(RESOURCE_LINK.ID.eq(id))
                .fetchOne {
                    RecordMappers.mapToResourceLinkEntity(it)
                }
    }

    fun fetchAll(dsl: DSLContext = instanceDsl): List<ResourceLinkEntity> {
        return dsl
                .select()
                .from(RESOURCE_LINK)
                .fetch {
                    RecordMappers.mapToResourceLinkEntity(it)
                }
    }

    fun update(entity: ResourceLinkEntity, dsl: DSLContext = instanceDsl) {
        dsl
                .update(RESOURCE_LINK)
                .set(RESOURCE_LINK.RESOURCE_CONTENT_FK, entity.resourceContentFk)
                .set(RESOURCE_LINK.CONTENT_FK, entity.contentFk)
                .set(RESOURCE_LINK.COLLECTION_FK, entity.collectionFk)
                .set(RESOURCE_LINK.DUBLIN_CORE_FK, entity.dublinCoreFk)
                .where(RESOURCE_LINK.ID.eq(entity.id))
                .execute()
    }

    fun delete(entity: ResourceLinkEntity, dsl: DSLContext = instanceDsl) {
        dsl
                .deleteFrom(RESOURCE_LINK)
                .where(RESOURCE_LINK.ID.eq(entity.id))
                .execute()
    }
}