package org.wycliffeassociates.otter.jvm.persistence

import org.wycliffeassociates.otter.common.data.model.Language

object TestDataStore {
    val languages = listOf(
            Language(0, "ar", "العَرَبِيَّة", "Arabic", true, true),
            Language(0, "en", "English", "English", false, true),
            Language(0, "atj", "Atikamekw", "Atikamekw", false, false),
            Language(0, "bbs", "Bakpinka", "Bakpinka", false, false),
            Language(0, "nfl", "Äiwoo", "Ayiwo", false, false)
    )
}