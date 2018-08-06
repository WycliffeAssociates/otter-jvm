package app.ui.languageSelector

import app.ui.welcomeScreen.WelcomeScreen
import app.UIColorsObject.Colors
import app.widgets.WidgetsStyles
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import io.reactivex.disposables.CompositeDisposable
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class ProfileLanguageSelection : View() {

    private val compositeDisposable = CompositeDisposable()
    private val viewModel = ProfileLanguageSelectionViewModel()

    private val rightArrow = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
    private val closeIcon = MaterialIconView(MaterialIcon.CLOSE, "25px")

    val hint = messages["languageSelectorHint"]

    override val root = HBox()

    init {

        compositeDisposable.addAll(
                viewModel.updateSelectedTargets.subscribe {
                    viewModel.updateSelectedTargetLanguages(it)
                },
                viewModel.updatePreferredTarget.subscribe {
                    viewModel.updateSelectedSourceLanguages(it)
                },
                viewModel.updateSelectedSources.subscribe {
                    viewModel.updatePreferredTargetLanguages(it)
                },
                viewModel.updatePreferredSource.subscribe {
                    viewModel.updatePreferredSourceLanguages(it)
                }
        )

        with(root) {
            importStylesheet(LanguageSelectorStyle::class)

            borderpane {

                top {
                    hbox {
                        alignment = Pos.BOTTOM_RIGHT
                        button(messages["close"], closeIcon) {
                            importStylesheet(WidgetsStyles::class)
                            addClass(WidgetsStyles.rectangleButtonDefault)
                            style {
                                alignment = Pos.CENTER
                                closeIcon.fill = c(Colors["primary"])
                                effect = DropShadow(10.0, c(Colors["baseBackground"]))
                            }
                            action {
                                find(ProfileLanguageSelection::class).replaceWith(WelcomeScreen::class)
                            }
                        }
                        style {
                            alignment = javafx.geometry.Pos.BOTTOM_RIGHT
                            paddingRight = 40.0
                            paddingTop = 40.0
                        }
                    }
                }
                // target languages
                left {

                    this += LanguageSelector(
                            viewModel.getTargetLanguageOptions(),
                            messages["targetLanguages"],
                            MaterialIconView(MaterialIcon.RECORD_VOICE_OVER, "20px"),
                            hint,
                            c(Colors["primary"]),
                            viewModel.updateSelectedTargets,
                            viewModel.updatePreferredTarget
                    )
                }

                val wut = stackpane {
                    circle {
                        style {
                            radius = 120.0
                            fill = c(Colors["baseLight"])
                        }
                    }
                }
                center {
                    this += wut
                }

                // source languages
                right  {
                    this += LanguageSelector(
                            viewModel.getSourceLanguageOptions(),
                            messages["sourceLanguages"],
                            MaterialIconView(MaterialIcon.HEARING, "20px"),
                            hint,
                            tornadofx.c(Colors["secondary"]),
                            viewModel.updateSelectedSources,
                            viewModel.updatePreferredSource
                    )
                }
            }

        }
    }
}
