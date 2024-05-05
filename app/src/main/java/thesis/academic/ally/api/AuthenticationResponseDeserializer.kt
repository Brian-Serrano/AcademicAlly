package thesis.academic.ally.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class AuthenticationResponseDeserializer : JsonDeserializer<AuthenticationResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AuthenticationResponse {
        val jsonObject = json?.asJsonObject

        return when (jsonObject?.get("type")?.asString ?: "") {
            "success" -> {
                val token = context?.deserialize<String>(jsonObject?.get("token"), String::class.java)
                    ?: ""
                val type = object : TypeToken<List<String>>() {}.type
                val achievements = context?.deserialize<List<String>>(jsonObject?.get("achievements"), type)
                    ?: emptyList()
                AuthenticationResponse.SuccessResponse(token, achievements)
            }
            "noAssessment" -> {
                val token = context?.deserialize<String>(jsonObject?.get("token"), String::class.java)
                    ?: ""
                AuthenticationResponse.SuccessNoAssessment(token)
            }
            "validationError" -> {
                val isValid = context?.deserialize<Boolean>(jsonObject?.get("isValid"), Boolean::class.java)
                    ?: false
                val message = context?.deserialize<String>(jsonObject?.get("message"), String::class.java)
                    ?: ""
                AuthenticationResponse.ValidationError(isValid, message)
            }
            "error" -> {
                val error = context?.deserialize<String>(jsonObject?.get("error"), String::class.java)
                    ?: ""
                AuthenticationResponse.ErrorResponse(error)
            }
            else -> throw IllegalStateException("Error")
        }
    }
}