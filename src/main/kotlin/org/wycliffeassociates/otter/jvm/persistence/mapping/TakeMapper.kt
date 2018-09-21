package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Single
import jooq.tables.pojos.TakeEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.repo.MarkerDao
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TakeMapper(private val markerDao: MarkerDao) : Mapper<Single<TakeEntity>, Single<Take>> {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    override fun mapFromEntity(type: Single<TakeEntity>): Single<Take> {
        return type
                .flatMap {
                    val theTake = Take(
                            it.filename,
                            File(it.path),
                            it.number,
                            Calendar.getInstance().apply {
                                time = dateFormatter.parse(it.timestamp)
                            },
                            it.unheard == 1,
                            listOf(),
                            it.id
                    )
                    markerDao
                            .getByTake(theTake)
                            .map {
                                theTake.markers = it
                                theTake
                            }
                }
    }

    override fun mapToEntity(type: Single<Take>): Single<TakeEntity> {
        return type
                .map{
                    TakeEntity(
                            it.id,
                            null, // Set by dao
                            it.filename,
                            it.path.path,
                            it.number,
                            dateFormatter.format(it.timestamp.time),
                            if (it.isUnheard) 1 else 0
                    )
                }
    }
}