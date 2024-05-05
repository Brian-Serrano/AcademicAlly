package thesis.academic.ally.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class OptionalCurrentUserDeserializer : JsonDeserializer<OptionalCurrentUser<*>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OptionalCurrentUser<*> {
        val jsonObject = json?.asJsonObject

        return when (jsonObject?.get("type")?.asString ?: "") {
            "success" -> {
                val data = context?.deserialize<Any>(jsonObject?.get("data"), (typeOfT as ParameterizedType).actualTypeArguments[0])
                    ?: IllegalStateException("Error")
                val currentUser = context?.deserialize<DrawerData>(jsonObject?.get("currentUser"), DrawerData::class.java)
                    ?: DrawerData()
                OptionalCurrentUser.CurrentUserData(data, currentUser)
            }
            "unauthorized" -> {
                val data = context?.deserialize<Any>(jsonObject?.get("data"), (typeOfT as ParameterizedType).actualTypeArguments[0])
                    ?: IllegalStateException("Error")
                OptionalCurrentUser.UserData(data)
            }
            "error" -> {
                val error = context?.deserialize<String>(jsonObject?.get("error"), String::class.java)
                    ?: ""
                OptionalCurrentUser.Error<Nothing>(error)
            }
            else -> throw IllegalStateException("Error")
        }
    }
}