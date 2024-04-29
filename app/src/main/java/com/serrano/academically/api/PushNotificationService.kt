package com.serrano.academically.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serrano.academically.R
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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelId = "academically"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.academically_icon_round)
            .setVibrate(LongArray(5) { 1000 })
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, "academically-push-notification", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}