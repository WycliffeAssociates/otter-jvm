package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.rxmodel.ResourceGroup
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceListStyles
import tornadofx.*

class ResourceGroupCard(group: ResourceGroup) : VBox() {
    init {
        addClass(ResourceListStyles.resourceGroupCard)
        label(group.title) {
            graphic = MaterialIconView(MaterialIcon.BOOKMARK_BORDER, "20px")
        }
        group.resources.forEach {
            add(
                    resourcecard(it)
            )
        }
    }
}

fun resourcegroupcard(group: ResourceGroup, init: ResourceGroupCard.() -> Unit = {}): ResourceGroupCard {
    val rgc = ResourceGroupCard(group)
    rgc.init()
    return rgc
}