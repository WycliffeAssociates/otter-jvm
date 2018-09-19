package org.wycliffeassociates.otter.jvm.persistence

import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapperTest
import java.io.File
import java.util.*

object TestDataStore {
    val languages = listOf(
            Language("ar", "العَرَبِيَّة", "Arabic", "rtl", true),
            Language("en", "English", "English", "ltr", true),
            Language("atj", "Atikamekw", "Atikamekw", "ltr", false),
            Language("bbs", "Bakpinka", "Bakpinka", "ltr", false),
            Language("nfl", "Äiwoo", "Ayiwo", "ltr", false)
    )
    val resourceContainers = listOf(
            ResourceContainer(
                    "rc0.2",
                    "Someone or Organization",
                    "One or two sentence description of the resource.",
                    "text/usfm",
                    "ulb",
                    with(Calendar.getInstance()) {
                        time = Date(1450328400000)
                        this
                    },
                    languages.first(), // no id initially set!
                    with(Calendar.getInstance()) {
                        time = Date(1450803690000)
                        this
                    },
                    "Name of Publisher",
                    "Bible",
                    "book",
                    "Unlocked Literal Bible",
                    3,
                    File("/path/to/my/container")
            ),
            ResourceContainer(
                    "rc0.2",
                    "J.R.R. Tolkien",
                    "An epic masterpiece of fiction.",
                    "text/usfm",
                    "lotr",
                    with(Calendar.getInstance()) {
                        time = Date(-486864000000)
                        this
                    },
                    languages[1], // no id initially set!
                    with(Calendar.getInstance()) {
                        time = Date(-448156800000)
                        this
                    },
                    "Allen & Unwin",
                    "Fiction",
                    "book",
                    "The Lord of the Rings",
                    1,
                    File("/path/to/my/amazing/esource")
            )
    )

    val collections = listOf(
            Collection(
                    1,
                    "rom",
                    "book",
                    "romans",
                    resourceContainers.first()
            ),
            Collection(
                    2,
                    "bible-ot",
                    "anthology",
                    "old_testament",
                    resourceContainers.last()
            ),
            Collection(
                    3,
                    "bible-nt",
                    "anthology",
                    "new_testament",
                    resourceContainers.last()
            )
    )

}