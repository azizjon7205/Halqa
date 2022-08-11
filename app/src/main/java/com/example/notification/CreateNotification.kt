package com.example.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.session.MediaSession
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.halqa.R
import com.example.model.BookData
import com.example.receiver.NotificationActionService
import com.example.utils.Constants.AUTHOR
import com.example.utils.Constants.JANGCHI

object CreateNotification {

    val CHANNEL_ID = "SOME_ID"

    val ACTION_PREVIOUS = "actionPrevious"
    val ACTION_PLAY = "actionPlay"
    val ACTION_NEXT = "actionNext"

    lateinit var notification: Notification

    fun createNotification(
        context: Context,
        bookData: BookData,
        playButton: Int,
        position: Int,
        size: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSession(context, "tag")

            val drawablePrevious: Int
            val pendingIntentPrevious: PendingIntent? = if (position == 0) {
                drawablePrevious = 0
                null
            } else {
                drawablePrevious = R.drawable.ic_previous
                val previousIntent = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PREVIOUS)
                PendingIntent.getBroadcast(
                    context,
                    0,
                    previousIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            val playIntent = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                playIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val drawableNext: Int
            val pendingIntentNext: PendingIntent? = if (position == size) {
                drawableNext = 0
                null
            } else {
                drawableNext = R.drawable.ic_next
                val nextIntent = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_NEXT)
                PendingIntent.getBroadcast(
                    context,
                    0,
                    nextIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

            val bookPhoto =
                if (bookData.bookName == JANGCHI) R.drawable.jangchi else R.drawable.halqa_image
            notification = Notification
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.halqamain_notification)
                .setContentTitle("${bookData.bookName} ${bookData.bob}")
                .setContentText(AUTHOR)
                .setOnlyAlertOnce(true)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        bookPhoto
                    )
                )
                .setShowWhen(false)
                .addAction(drawablePrevious, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(drawableNext, "Next", pendingIntentNext)
                .setStyle(
                    Notification.MediaStyle().setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build()

            notificationManagerCompat.notify(1, notification)

        }
    }
}