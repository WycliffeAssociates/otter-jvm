package org.wycliffeassociates.otter.jvm.persistence.database.queries

import jooq.tables.CollectionEntity
import jooq.tables.ContentDerivative
import jooq.tables.ContentEntity
import org.jooq.DSLContext
import org.jooq.impl.DSL

class DeriveProjectQuery {
    fun execute(sourceId: Int, projectId: Int, metadataId: Int, dsl: DSLContext) {
        // Copy the chapters
        copyChapters(dsl, sourceId, projectId, metadataId)

        // Copy the content
        copyContent(dsl, sourceId, projectId)

        // Link the derivative content
        linkContent(dsl, sourceId, projectId)
    }

    private fun copyChapters(dsl: DSLContext, sourceId: Int, projectId: Int, metadataId: Int) {
        // Copy all the chapter collections
        dsl.insertInto(
                CollectionEntity.COLLECTION_ENTITY,
                CollectionEntity.COLLECTION_ENTITY.PARENT_FK,
                CollectionEntity.COLLECTION_ENTITY.SOURCE_FK,
                CollectionEntity.COLLECTION_ENTITY.LABEL,
                CollectionEntity.COLLECTION_ENTITY.TITLE,
                CollectionEntity.COLLECTION_ENTITY.SLUG,
                CollectionEntity.COLLECTION_ENTITY.SORT,
                CollectionEntity.COLLECTION_ENTITY.RC_FK
        ).select(
                dsl.select(
                        DSL.value(projectId),
                        CollectionEntity.COLLECTION_ENTITY.ID,
                        CollectionEntity.COLLECTION_ENTITY.LABEL,
                        CollectionEntity.COLLECTION_ENTITY.TITLE,
                        CollectionEntity.COLLECTION_ENTITY.SLUG,
                        CollectionEntity.COLLECTION_ENTITY.SORT,
                        DSL.value(metadataId)
                )
                        .from(CollectionEntity.COLLECTION_ENTITY)
                        .where(CollectionEntity.COLLECTION_ENTITY.PARENT_FK.eq(sourceId))
        ).execute()
    }

    private fun copyContent(dsl: DSLContext, sourceId: Int, metadataId: Int) {
        dsl.insertInto(
                ContentEntity.CONTENT_ENTITY,
                ContentEntity.CONTENT_ENTITY.COLLECTION_FK,
                ContentEntity.CONTENT_ENTITY.LABEL,
                ContentEntity.CONTENT_ENTITY.START,
                ContentEntity.CONTENT_ENTITY.SORT
        )
                .select(
                        dsl.select(
                                CollectionEntity.COLLECTION_ENTITY.ID,
                                DSL.field("verselabel", String::class.java),
                                DSL.field("versestart", Int::class.java),
                                DSL.field("versesort", Int::class.java)
                        )
                                .from(
                                        dsl.select(
                                                ContentEntity.CONTENT_ENTITY.ID.`as`("verseid"),
                                                ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`as`("chapterid"),
                                                ContentEntity.CONTENT_ENTITY.LABEL.`as`("verselabel"),
                                                ContentEntity.CONTENT_ENTITY.START.`as`("versestart"),
                                                ContentEntity.CONTENT_ENTITY.SORT.`as`("versesort")
                                        )
                                                .from(ContentEntity.CONTENT_ENTITY)
                                                .where(ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`in`(
                                                        dsl
                                                                .select(CollectionEntity.COLLECTION_ENTITY.ID)
                                                                .from(CollectionEntity.COLLECTION_ENTITY)
                                                                .where(CollectionEntity.COLLECTION_ENTITY.PARENT_FK.eq(sourceId))
                                                ))
                                )
                                .leftJoin(CollectionEntity.COLLECTION_ENTITY)
                                .on(CollectionEntity.COLLECTION_ENTITY.SOURCE_FK.eq(DSL.field("chapterid", Int::class.java))
                                        .and(CollectionEntity.COLLECTION_ENTITY.RC_FK.eq(metadataId)))
                ).execute()
    }

    private fun linkContent(dsl: DSLContext, sourceId: Int, projectId: Int) {
        dsl.insertInto(
                ContentDerivative.CONTENT_DERIVATIVE,
                ContentDerivative.CONTENT_DERIVATIVE.CONTENT_FK,
                ContentDerivative.CONTENT_DERIVATIVE.SOURCE_FK
        ).select(
                dsl.select(
                        DSL.field("derivedid", Int::class.java),
                        DSL.field("sourceid", Int::class.java)
                )
                        .from(
                                dsl.select(
                                        DSL.field("sourceid", Int::class.java),
                                        DSL.field("sourcesort", Int::class.java),
                                        CollectionEntity.COLLECTION_ENTITY.SLUG.`as`("sourcechapter")
                                )
                                        .from(
                                                dsl.select(
                                                        ContentEntity.CONTENT_ENTITY.ID.`as`("sourceid"),
                                                        ContentEntity.CONTENT_ENTITY.SORT.`as`("sourcesort"),
                                                        ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`as`("chapterid")
                                                ).from(ContentEntity.CONTENT_ENTITY).where(
                                                        ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`in`(
                                                                dsl
                                                                        .select(CollectionEntity.COLLECTION_ENTITY.ID)
                                                                        .from(CollectionEntity.COLLECTION_ENTITY)
                                                                        .where(CollectionEntity.COLLECTION_ENTITY.PARENT_FK.eq(sourceId))
                                                        )
                                                )
                                        )
                                        .leftJoin(CollectionEntity.COLLECTION_ENTITY)
                                        .on(CollectionEntity.COLLECTION_ENTITY.ID.eq(DSL.field("chapterid", Int::class.java)))
                        )
                        .leftJoin(
                                dsl
                                        .select(
                                                DSL.field("derivedid", Int::class.java),
                                                DSL.field("derivedsort", Int::class.java),
                                                CollectionEntity.COLLECTION_ENTITY.SLUG.`as`("derivedchapter")
                                        )
                                        .from(
                                                dsl
                                                        .select(
                                                                ContentEntity.CONTENT_ENTITY.ID.`as`("derivedid"),
                                                                ContentEntity.CONTENT_ENTITY.SORT.`as`("derivedsort"),
                                                                ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`as`("chapterid")
                                                        )
                                                        .from(ContentEntity.CONTENT_ENTITY)
                                                        .where(ContentEntity.CONTENT_ENTITY.COLLECTION_FK.`in`(
                                                                dsl
                                                                        .select(CollectionEntity.COLLECTION_ENTITY.ID)
                                                                        .from(CollectionEntity.COLLECTION_ENTITY)
                                                                        .where(CollectionEntity.COLLECTION_ENTITY.PARENT_FK.eq(projectId))
                                                        ))
                                        )
                                        .leftJoin(CollectionEntity.COLLECTION_ENTITY)
                                        .on(CollectionEntity.COLLECTION_ENTITY.ID.eq(DSL.field("chapterid", Int::class.java)))
                        )
                        .on(
                                DSL.field("sourcesort", Int::class.java)
                                        .eq(DSL.field("derivedsort", Int::class.java))
                                        .and(DSL.field("sourcechapter", Int::class.java)
                                                .eq(DSL.field("derivedchapter", Int::class.java)))
                        )
        ).execute()
    }
}