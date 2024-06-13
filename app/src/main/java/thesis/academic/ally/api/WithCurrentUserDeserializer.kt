package thesis.academic.ally.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class WithCurrentUserDeserializer : JsonDeserializer<WithCurrentUser<*>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WithCurrentUser<*> {
        val jsonObject = json?.asJsonObject

        return when (jsonObject?.get("type")?.asString ?: "") {
            "success" -> {
                val data = context?.deserialize<Any>(jsonObject?.get("data"), (typeOfT as ParameterizedType).actualTypeArguments[0])
                    ?: IllegalStateException("Error")
                val currentUser = context?.deserialize<DrawerData>(jsonObject?.get("currentUser"), DrawerData::class.java)
                    ?: DrawerData()
                WithCurrentUser.Success(data, currentUser)
            }
            "error" -> {
                val error = context?.deserialize<String>(jsonObject?.get("error"), String::class.java)
                    ?: ""
                WithCurrentUser.Error<Nothing>(error)
            }
            else -> throw IllegalStateException("Error")
        }
    }
}