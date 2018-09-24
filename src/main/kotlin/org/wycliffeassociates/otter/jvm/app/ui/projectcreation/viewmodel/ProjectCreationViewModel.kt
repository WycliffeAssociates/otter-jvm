package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel

import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model.ProjectCreationModel
import org.wycliffeassociates.otter.jvm.app.widgets.filterableComboBox.ComboBoxSelectionItem
import tornadofx.*

class ProjectCreationViewModel : ViewModel() {

    val model = ProjectCreationModel()

    val activeIdProperty = bind { model.actveIdProperty }
    val activeViewProperty = bind { model.activeViewProperty }
    val sourceLanguageProperty = bind{model.sourceLanguageProperty}
    val targetLanguageProperty = bind{model.targetLanguageProperty}

    val projectsProperty = bind{model.projectProperty}

    fun setActiveId(newIndex : Int) {
        model.activeId = newIndex
    }

    fun sourceLanguage(selection: ComboBoxSelectionItem) {
        println(selection.labelText)
        model.sourceLanguage = selection.labelText
    }

    fun targetLanguage(selection: ComboBoxSelectionItem) {
        println(selection.labelText)
        model.targetLanguage = selection.labelText
    }
}