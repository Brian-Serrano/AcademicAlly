package com.serrano.academically.api

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serrano.academically.datastore.UserCache
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

    @Inject lateinit var academicallyApi: AcademicallyApi
    @Inject lateinit var userCacheRepository: UserCacheRepository
    @Inject lateinit var context: Context
    private val job = SupervisorJob()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(job).launch {
            val data = userCacheRepository.userDataStore.data.first()
            if (data.email.isNotEmpty() && data.password.isNotEmpty() && data.authToken.isNotEmpty()) {
                if (Utils.checkToken(data.authToken)) {
                    Utils.checkAuthentication(context, userCacheRepository, academicallyApi)
                } else {
                    academicallyApi.updateNotificationsToken(NotificationTokenBody(token))
                }
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}