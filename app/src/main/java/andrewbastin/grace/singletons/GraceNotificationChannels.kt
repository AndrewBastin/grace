package andrewbastin.grace.singletons

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object GraceNotificationChannels {

    val ID_PLAYBACK = "andrewbastin.grace.notifications:playback"

    @TargetApi(26)
    fun createChannels(context: Context) {

        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // region Playback Channel

        val playbackChannel = NotificationChannel(ID_PLAYBACK, "Playback Controls", NotificationManager.IMPORTANCE_LOW).apply {
            description = "Notifications which provide media playback controls"
            setShowBadge(false)
            enableLights(false)
            enableVibration(false)
        }

        notifManager.createNotificationChannel(playbackChannel)

        // endregion
    }

}