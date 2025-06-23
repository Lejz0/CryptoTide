package com.example.cryptotide.ai

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.mediapipe.tasks.genai.llminference.LlmInference

class GemmaAnalysisService : Service() {

    private lateinit var llmService: LlmInference

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prompt = intent?.getStringExtra("prompt") ?: "No prompt received"

        startForegroundWithNotification("AI service started")

        Thread {
            try {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath("/data/local/tmp/gemma-2b-it-gpu-int4.bin")
                    .build()

                llmService = LlmInference.createFromOptions(this, options)
                val result = llmService.generateResponse(prompt)

                Log.d("GemmaService", "AI Result: $result")

                sendResultBroadcast(result)

                showCompletionNotification("AI service finished")
            } catch (e: Exception) {
                Log.e("GemmaService", "AI error", e)
            } finally {
                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    private fun sendResultBroadcast(result: String) {
        val intent = Intent("com.example.AI_RESULT")
        intent.putExtra("ai_result", result)
        Log.d("GemmaService", "Sending broadcast with result: ${result.take(50)}...")
        sendBroadcast(intent)
    }

    private fun showCompletionNotification(message: String) {
        val channelId = "gemma_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Gemma AI Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from Gemma AI Analysis"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Gemma AI Analysis")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        Log.d("GemmaService", "Showing completion notification")
        notificationManager.notify(2, notification)
    }

    private fun startForegroundWithNotification(message: String) {
        val channelId = "gemma_channel"
        val channelName = "Gemma AI Background Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Create PendingIntent to open the app when notification is clicked
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Gemma AI")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
