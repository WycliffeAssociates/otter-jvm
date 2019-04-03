package org.wycliffeassociates.otter.jvm.resourcecardpage.styles

import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class ProgressBannerStyles : Stylesheet() {

    companion object {
        val progressBanner by cssclass()
    }

    init {
        progressBanner {
            padding = box(10.px, 100.px, 20.px, 70.px)
            backgroundColor += AppTheme.colors.white
        }
    }
}
