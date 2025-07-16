package com.levicrobinson.jesture.data.repository

import android.util.Log
import com.levicrobinson.jesture.data.LOG_TAG
import com.levicrobinson.jesture.data.api.GestureApi
import com.levicrobinson.jesture.data.model.GraphQLRequest
import com.levicrobinson.jesture.data.model.toDomainModel
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class GestureRepositoryImpl @Inject constructor(
    private val gestureApi: GestureApi
) : GestureRepository {
    override suspend fun fetchGestures(): List<Gesture>? {
        val query = """
            query {
    allGestures {
        id
        name
        description
        frames {
            id
            accelX
            accelY
            accelZ
        }
    }
}
        """.trimIndent()
        val request = GraphQLRequest(query=query)

        val response = gestureApi.getGestures(request)
        var result: List<Gesture>? = null
        if (response.isSuccessful) {
            result = response.body()?.data?.allGestures?.map { it.toDomainModel() }

        } else {
            Log.e(LOG_TAG, "Error: ${response.errorBody()?.string()}")
        }
        return result
    }
}