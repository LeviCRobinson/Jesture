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
        val request = GraphQLRequest(query = query)

        val response = gestureApi.getGestures(request)
        var result: List<Gesture>? = null
        if (response.isSuccessful) {
            result = response.body()?.data?.allGestures?.map { it.toDomainModel() }

        } else {
            Log.e(LOG_TAG, "Error: ${response.errorBody()?.string()}")
        }
        return result
    }

    override suspend fun createGesture(gesture: Gesture): Gesture? {
        val query = """
            mutation CreateGesture(${'$'}gestureInput: GestureWithFramesInput!) {
              createGesture(gestureInput: ${'$'}gestureInput) {
                id
                name
                description
                frames {
                  accelX
                  accelY
                  accelZ
                }
              }
            }
        """.trimIndent()

        val variables = mapOf(
            "gestureInput" to mapOf(
                "name" to gesture.name.trim(),
                "description" to gesture.description.trim(),
                "frames" to gesture.accelerometerReadings.map {
                    mapOf(
                        "accelX" to it.accelX,
                        "accelY" to it.accelY,
                        "accelZ" to it.accelZ
                    )
                }
            )
        )

        val request = GraphQLRequest(query = query, variables = variables)
        val response = gestureApi.createGesture(request)
        var result: Gesture? = null
        if (response.isSuccessful) {
            result = response.body()?.data?.createGestureWithFrames?.toDomainModel()
        } else {
            Log.e(LOG_TAG, "Error: ${response.errorBody()?.string()}")
        }

        return result
    }

    override suspend fun deleteGesture(gesture: Gesture): String? {
        val query = """
            mutation DeleteGesture(${'$'}gestureId: Int!){
                deleteGesture(gestureId: ${'$'}gestureId)
            }
        """.trimIndent()

        val variables = mapOf(
            "gestureId" to gesture.id
        )

        val request = GraphQLRequest(query = query, variables = variables)
        val response = gestureApi.deleteGesture(request)
        var result: String? = null
        if (response.isSuccessful) {
            result = response.body()?.data?.deleteGesture
        } else {
            Log.e(LOG_TAG, "Error: ${response.errorBody()?.string()}")
        }

        return result
    }
}