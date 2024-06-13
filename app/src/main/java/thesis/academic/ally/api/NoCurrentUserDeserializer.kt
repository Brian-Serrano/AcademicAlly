package thesis.academic.ally.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NoCurrentUserDeserializer : JsonDeserializer<NoCurrentUser<*>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): NoCurrentUser<*> {
        val jsonObject = json?.asJsonObject

        return when (jsonObject?.get("type")?.asString ?: "") {
            "success" -> {
                val data = context?.deserialize<Any>(jsonObject?.get("data"), (typeOfT as ParameterizedType).actualTypeArguments[0])
                    ?: IllegalStateException("Error")
                NoCurrentUser.Success(data)
            }
            "error" -> {
                val error = context?.deserialize<String>(jsonObject?.get("error"), String::class.java)
                    ?: ""
                NoCurrentUser.Error<Nothing>(error)
            }
            else -> throw IllegalStateException("Error")
        }
    }

}