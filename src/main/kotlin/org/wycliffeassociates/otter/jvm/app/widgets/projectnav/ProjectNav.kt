package org.wycliffeassociates.otter.jvm.app.widgets.projectnav

import com.jfoenix.controls.JFXToggleButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.effect.GaussianBlur
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import tornadofx.*

class ProjectNav : VBox() {
    var projectBox: VBox by singleAssign()
    var chapterBox: VBox by singleAssign()
    var chunkBox: VBox by singleAssign()
    val projectIcon = MaterialIconView(MaterialIcon.BOOK, "35px")
    val chapterIcon = MaterialIconView(MaterialIcon.CHROME_READER_MODE, "35px")
    val chunkIcon = MaterialIconView(MaterialIcon.BOOKMARK, "35px")
    val selectProjectTextProperty = SimpleStringProperty()
    var selectProjectText by selectProjectTextProperty

    val selectChapterTextProperty = SimpleStringProperty()
    var selectChapterText by selectChapterTextProperty

    val selectChunkTextProperty = SimpleStringProperty()
    var selectChunkText by selectChunkTextProperty

    var activeProjectProperty = SimpleObjectProperty<Collection>()

    var activeChapterProperty = SimpleObjectProperty<Collection>()

    var activeContentProperty = SimpleObjectProperty<Content>()


    init {
        importStylesheet<ProjectNavStyles>()
        vbox(10) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            add(JFXToggleButton().apply {
                text = "Resources"
                isDisableVisualFocus = true
                addClass(AppStyles.appToggleButton)
            })
            style {
                prefWidth = 200.0.px
                alignment = Pos.TOP_CENTER
                padding = box(15.0.px)
                backgroundColor += Color.WHITE
                effect = DropShadow(3.0, 3.0, 0.0, Color.LIGHTGRAY)
            }
            projectBox = vbox {
                style {
                    backgroundColor += c("#E6E8E9")
                    borderWidth += box(2.0.px)
                    borderColor += box(Color.GRAY)
                    borderRadius += box(5.0.px)
                    backgroundRadius += box(5.0.px)
                    maxWidth = 180.0.px
                    cursor = Cursor.HAND
                }
                if (activeProjectProperty.value != null) {
                    vbox {
                        hgrow = Priority.ALWAYS
                        maxHeight = 150.0
                        prefHeight = 150.0
                        addClass(ProjectNavStyles.projectNavCard)
                        label(activeProjectProperty.value.titleKey)
                        label(activeProjectProperty.value.resourceContainer!!.language.name)
                    }
                } else {
                    vbox(10.0) {
                        alignment = Pos.CENTER
                        hgrow = Priority.ALWAYS
                        maxHeight = 150.0
                        prefHeight = 150.0
                        add(projectIcon)
                        label(selectProjectTextProperty)
                    }
                }
                activeProjectProperty.onChange {
                    projectBox.children.clear()
                    if (it == null) {
                        vbox(10.0) {
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            maxHeight = 150.0
                            prefHeight = 150.0
                            add(projectIcon)
                            label(selectProjectTextProperty)
                        }
                    } else {
                        vbox {
                            maxHeight = 150.0
                            prefHeight = 150.0
                            label(it.titleKey)
                            label(it.resourceContainer!!.language.name)
                            addClass(ProjectNavStyles.projectNavCard)
                        }
                    }
                }
            }
            chapterBox = vbox {
                style {
                    backgroundColor += c("#E6E8E9")
                    borderWidth += box(2.0.px)
                    borderColor += box(Color.GRAY)
                    borderRadius += box(5.0.px)
                    backgroundRadius += box(5.0.px)
                    cursor = Cursor.HAND
                    maxWidth = 180.0.px

                }
                if (activeChapterProperty?.value != null) {
                    stackpane {
                        addClass(ProjectNavStyles.chapterNavCard)
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                        ellipse {
                            centerX = 50.0
                            centerY = 0.0
                            radiusX = 30.0
                            radiusY = 30.0
                            fill = Color.WHITE
                            effect = GaussianBlur(15.0)
                        }
                        vbox {
                            alignment = Pos.CENTER
                            label(activeChapterProperty.value.labelKey.toUpperCase())
                            label(activeChapterProperty.value.sort.toString())
                        }
                    }
                } else if (activeChapterProperty?.value == null || activeChapterProperty == null) {
                    vbox(10.0) {
                        alignment = Pos.CENTER
                        hgrow = Priority.ALWAYS
                        maxHeight = 150.0
                        prefHeight = 150.0
                        add(chapterIcon)
                        label(selectChapterTextProperty)
                    }
                }
                activeChapterProperty?.onChange {
                    chapterBox.children.clear()
                    if (it == null) {
                        vbox(10.0) {
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            maxHeight = 150.0
                            prefHeight = 150.0
                            add(chapterIcon)
                            label(selectChapterTextProperty)
                        }
                    } else {
                        stackpane {
                            addClass(ProjectNavStyles.chapterNavCard)
                            hgrow = Priority.ALWAYS
                            vgrow = Priority.ALWAYS
                            ellipse {
                                centerX = 50.0
                                centerY = 0.0
                                radiusX = 30.0
                                radiusY = 30.0
                                fill = Color.WHITE
                                effect = GaussianBlur(15.0)
                            }
                            vbox {
                                alignment = Pos.CENTER
                                label(it.labelKey.toUpperCase())
                                label(it.sort.toString())
                            }
                        }
                    }
                }
            }

            chunkBox = vbox {
                style {
                    backgroundColor += c("#E6E8E9")
                    borderWidth += box(2.0.px)
                    borderColor += box(Color.GRAY)
                    borderRadius += box(5.0.px)
                    backgroundRadius += box(5.0.px)
                    maxWidth = 180.0.px
                    cursor = Cursor.HAND

                }
                if (activeContentProperty?.value != null) {
                    stackpane {
                        addClass(ProjectNavStyles.chunkNavCard)
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                        ellipse {
                            centerX = 50.0
                            centerY = 0.0
                            radiusX = 30.0
                            radiusY = 30.0
                            fill = Color.WHITE
                            effect = GaussianBlur(15.0)
                        }
                        vbox {
                            alignment = Pos.CENTER
                            label(activeContentProperty.value.labelKey.toUpperCase())
                            label(activeContentProperty.value.sort.toString())
                        }
                    }
                } else if (activeContentProperty?.value == null || activeContentProperty == null) {
                    vbox(10.0) {
                        alignment = Pos.CENTER
                        hgrow = Priority.ALWAYS
                        maxHeight = 150.0
                        prefHeight = 150.0
                        add(chunkIcon)
                        label(selectChunkTextProperty)
                    }
                }
                activeContentProperty?.onChange {
                    chunkBox.children.clear()
                    if (it == null) {
                        vbox(10.0) {
                            alignment = Pos.CENTER
                            hgrow = Priority.ALWAYS
                            maxHeight = 150.0
                            prefHeight = 150.0
                            add(chunkIcon)
                            label(selectChunkTextProperty)
                        }
                    } else {
                        stackpane {
                            addClass(ProjectNavStyles.chunkNavCard)
                            hgrow = Priority.ALWAYS
                            vgrow = Priority.ALWAYS
                            ellipse {
                                centerX = 50.0
                                centerY = 0.0
                                radiusX = 30.0
                                radiusY = 30.0
                                fill = Color.WHITE
                                effect = GaussianBlur(15.0)
                            }
                            vbox {
                                alignment = Pos.CENTER
                                label(it.labelKey.toUpperCase())
                                label(it.sort.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

fun projectnav(init: ProjectNav.() -> Unit = {}): ProjectNav {
    val pn = ProjectNav()
    pn.init()
    return pn
}
