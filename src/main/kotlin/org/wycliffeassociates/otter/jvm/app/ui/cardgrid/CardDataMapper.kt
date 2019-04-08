package org.wycliffeassociates.otter.jvm.app.ui.cardgrid

import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content

class CardDataMapper {

    companion object {
        fun mapContentListToCards(contentList: List<Content>): List<CardData> {
                val cardList: MutableList<CardData> = mutableListOf()
                contentList.forEach {
                    val newCardData = CardData(
                        it.labelKey,
                        "content",
                        it.start.toString(),
                        it.sort,
                        contentSource = it)
                    cardList.add(newCardData)
                }
                return cardList
        }

        fun mapCollectionListToCards(collectionList: List<Collection>) : List<CardData> {
            val cardList: MutableList<CardData> = mutableListOf()
            collectionList.forEach {
                val newCardData = CardData(it.labelKey,
                    "collection",
                    it.titleKey,
                    it.sort,
                    collectionSource =  it)
                cardList.add(newCardData)
            }
            return cardList
        }
    }
}