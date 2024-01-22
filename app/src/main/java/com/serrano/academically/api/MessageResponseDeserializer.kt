package com.serrano.academically.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MessageResponseDeserializer : JsonDeserializer<MessageResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MessageResponse {
        val jsonObject = json?.asJsonObject

        return when (jsonObject?.get("type")?.asString ?: "") {
            "success" -> {
                val type = object : TypeToken<List<String>>() {}.type
                val achievements = context?.deserialize<List<String>>(jsonObject?.get("achievements"), type)
                    ?: emptyList()
                MessageResponse.AchievementResponse(achievements)
            }
            "duplicate" -> {
                val message = context?.deserialize<String>(jsonObject?.get("message"), String::class.java)
                    ?: ""
                return MessageResponse.DuplicateMessageResponse(message)
            }
            "error" -> {
                val error = context?.deserialize<String>(jsonObject?.get("error"), String::class.java)
                    ?: ""
                MessageResponse.ErrorResponse(error)
            }
            else -> throw IllegalStateException("Error")
        }
    }
}