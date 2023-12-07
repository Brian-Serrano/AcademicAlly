package com.serrano.academically.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object UserPrefSerializer : Serializer<UserPref> {
    override val defaultValue: UserPref
        get() = UserPref()

    override suspend fun readFrom(input: InputStream): UserPref {
        return try {
            Json.decodeFromString(
                deserializer = UserPref.serializer(),
                string = input.readBytes().decodeToString()
            )
        }
        catch (se: SerializationException) {
            se.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPref, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = UserPref.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}