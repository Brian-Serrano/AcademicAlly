package thesis.academic.ally.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class UserCacheSerializer(
    private val cryptoManager: CryptoManager
) : Serializer<UserCache> {

    override val defaultValue: UserCache
        get() = UserCache()

    override suspend fun readFrom(input: InputStream): UserCache {
        return try {
            Json.decodeFromString(
                deserializer = UserCache.serializer(),
                string = cryptoManager.decrypt(input).decodeToString()
            )
        } catch (se: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserCache, output: OutputStream) {
        cryptoManager.encrypt(
            bytes = Json.encodeToString(
                serializer = UserCache.serializer(),
                value = t
            ).encodeToByteArray(),
            outputStream = output
        )
    }
}