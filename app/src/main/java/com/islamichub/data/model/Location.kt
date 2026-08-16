package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Bangladesh locations — divisions → districts → upazilas
 * Source: location-data.js → locations.json
 * Used for manual prayer time location selection.
 */
@Serializable
data class BangladeshLocations(
    val divisions: Map<String, Map<String, List<String>>> = emptyMap()
)
