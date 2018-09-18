package org.wycliffeassociates.otter.jvm.persistence.mapping

import jooq.tables.pojos.TakeEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Take
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TakeMapper : Mapper<TakeEntity, Take> {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    override fun mapFromEntity(type: TakeEntity): Take {
        return Take(
                type.id,
                type.filename,
                File(type.path),
                type.number,
                Calendar.getInstance().apply {
                    time = dateFormatter.parse(type.timestamp)
                },
                type.heard == 1
        )
    }

    override fun mapToEntity(type: Take): TakeEntity {
        return TakeEntity(
                type.id,
                null, // Set by dao
                type.filename,
                type.path.path,
                type.number,
                dateFormatter.format(type.timestamp),
                if (type.isUnheard) 1 else 0
        )
    }
}