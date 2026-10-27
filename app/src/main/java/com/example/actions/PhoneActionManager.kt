package com.example.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.example.data.model.ActionType
import com.example.data.model.PhoneAction
import java.util.UUID

private const val TAG = "PhoneActionManager"

sealed interface ActionResult {
    data class Success(val message: String) : ActionResult
    data class Error(val message: String) : ActionResult
}

class PhoneActionManager(private val context: Context) {

    /**
     * Parse text to identify if a supported phone action was requested.
     */
    fun detectPhoneAction(text: String): PhoneAction? {
        val lower = text.lowercase().trim()

        // 1. YouTube
        if (lower.contains("youtube") && (lower.contains("kholo") || lower.contains("open") || lower.contains("chalao") || lower.contains("dekho"))) {
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.OPEN_YOUTUBE,
                title = "Open YouTube",
                description = "Launch the YouTube application or browser stream",
                intentUri = "https://www.youtube.com",
                packageName = "com.google.android.youtube"
            )
        }

        // 2. Camera
        if ((lower.contains("camera") || lower.contains("photo") || lower.contains("selfie")) &&
            (lower.contains("kholo") || lower.contains("open") || lower.contains("khula") || lower.contains("start"))
        ) {
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.OPEN_CAMERA,
                title = "Open Camera",
                description = "Launch the camera app to take photos or record video"
            )
        }

        // 3. Google Maps
        if ((lower.contains("maps") || lower.contains("map") || lower.contains("navigation") || lower.contains("rasta")) &&
            (lower.contains("kholo") || lower.contains("open") || lower.contains("start"))
        ) {
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.OPEN_MAPS,
                title = "Open Google Maps",
                description = "Open Maps navigation for nearby locations or routes",
                intentUri = "geo:0,0?q=nearby"
            )
        }

        // 4. Timer
        val timerMatch = Regex("""(\d+)\s*(minute|min|second|sec|ghanta|hour)""").find(lower)
        if (lower.contains("timer") || (lower.contains("alarm") && timerMatch != null)) {
            val amount = timerMatch?.groupValues?.get(1)?.toIntOrNull() ?: 5
            val unit = timerMatch?.groupValues?.get(2) ?: "minute"
            val totalSeconds = when {
                unit.startsWith("hour") || unit.startsWith("ghanta") -> amount * 3600
                unit.startsWith("sec") -> amount
                else -> amount * 60
            }

            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.SET_TIMER,
                title = "Set ${amount} $unit Timer",
                description = "Configure a $amount $unit timer on your Android device",
                timerSeconds = totalSeconds
            )
        }

        // 5. Phone Dialer
        val phoneMatch = Regex("""(\+?\d[\d\s-]{7,15}\d)""").find(lower)
        if (lower.contains("dial") || lower.contains("call") || lower.contains("phone")) {
            val number = phoneMatch?.groupValues?.get(1)?.replace(Regex("[\\s-]"), "") ?: ""
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.OPEN_DIALER,
                title = if (number.isNotEmpty()) "Dial $number" else "Open Phone Dialer",
                description = "Open the Android phone dialer with number pre-filled",
                phoneNumber = number
            )
        }

        // 6. SMS
        if (lower.contains("sms") || (lower.contains("message") && lower.contains("likhe"))) {
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.SEND_SMS,
                title = "Compose SMS Message",
                description = "Open the default messaging app to review and send SMS"
            )
        }

        // 7. Settings
        if (lower.contains("settings") && (lower.contains("kholo") || lower.contains("open"))) {
            return PhoneAction(
                id = UUID.randomUUID().toString(),
                type = ActionType.OPEN_SETTINGS,
                title = "Open Android Settings",
                description = "Navigate to device settings"
            )
        }

        return null
    }

    /**
     * Executes the phone action safely with error catching.
     */
    fun executeAction(action: PhoneAction): ActionResult {
        return try {
            when (action.type) {
                ActionType.OPEN_YOUTUBE -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.intentUri ?: "https://www.youtube.com")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        action.packageName?.let { setPackage(it) }
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        // Fallback to web browser if YouTube app is not installed
                        val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(fallback)
                    }
                    ActionResult.Success("Opened YouTube successfully.")
                }

                ActionType.OPEN_CAMERA -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Camera opened successfully.")
                }

                ActionType.OPEN_MAPS -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.intentUri ?: "geo:0,0?q=maps")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Google Maps opened.")
                }

                ActionType.SET_TIMER -> {
                    val seconds = action.timerSeconds ?: 300
                    val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                        putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                        putExtra(AlarmClock.EXTRA_MESSAGE, "RAHUL AI Timer")
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Timer configured for ${seconds / 60} min.")
                }

                ActionType.OPEN_DIALER -> {
                    val uri = if (!action.phoneNumber.isNullOrBlank()) {
                        Uri.parse("tel:${action.phoneNumber}")
                    } else {
                        Uri.parse("tel:")
                    }
                    val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Dialer opened.")
                }

                ActionType.SEND_SMS -> {
                    val uri = if (!action.phoneNumber.isNullOrBlank()) {
                        Uri.parse("smsto:${action.phoneNumber}")
                    } else {
                        Uri.parse("smsto:")
                    }
                    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                        action.extraText?.let { putExtra("sms_body", it) }
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Messaging app opened.")
                }

                ActionType.OPEN_SETTINGS -> {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("Device settings opened.")
                }

                ActionType.OPEN_URL -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.intentUri ?: "https://google.com")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("URL opened in browser.")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute phone action: ${action.title}", e)
            ActionResult.Error("Could not launch action: ${e.localizedMessage ?: "Application not found"}")
        }
    }
}
