package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Islamic Stories — Me'raj, Sirat, Prophets, Khalifas
 * Source: islamic-stories-data.js → islamic_stories.json
 * Per migration plan §14: prophets/khalifas are arrays (not objects).
 */
@Serializable
data class IslamicStory(
    val id: String = "",
    val title: String = "",
    val arabic: String = "",
    val bangla: String = "",
    val description: String = "",
    val period: String = "",
    val category: String = ""
)

@Serializable
data class IslamicStoriesData(
    val prophets: List<IslamicStory> = emptyList(),
    val khalifas: List<IslamicStory> = emptyList(),
    val meraj: List<IslamicStory> = emptyList(),
    val sirat: List<IslamicStory> = emptyList()
)
