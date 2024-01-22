package com.serrano.academically.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class UserCacheSerializer : Serializer<UserCache> {

    override val defaultValue: UserCache
        get() = UserCache()

    override suspend fun readFrom(input: InputStream): UserCache {
        return try {
            Json.decodeFromString(
                deserializer = UserCache.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (se: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserCache, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = UserCache.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}