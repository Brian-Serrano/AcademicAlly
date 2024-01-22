package com.serrano.academically.api

import android.content.Context
import com.auth0.jwt.JWT
import com.google.gson.Gson
import com.serrano.academically.activity.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import retrofit2.Invocation
import java.util.Date

class AuthInterceptor(
    private val context: Context,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(chain.request())

        val shouldNotAttachAuthHeader = invocation
            .method()
            .annotations
            .any { it.annotationClass == Unauthorized::class }

        return if (shouldNotAttachAuthHeader) {
            chain.proceed(chain.request())
        } else {
            runBlocking {
                val token = context.userDataStore.data.first().authToken
                chain.proceed(chain.request().newBuilder().addHeader("Authorization", token).build())
            }
        }
    }
}