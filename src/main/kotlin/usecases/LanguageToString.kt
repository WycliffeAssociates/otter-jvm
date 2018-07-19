package usecases

import data.Language

fun Language.toString() : String {
    return "${this.slug} (${this.name})"
}

