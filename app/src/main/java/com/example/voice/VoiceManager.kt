package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "VoiceManager"

sealed interface SpeechInputState {
    data object Idle : SpeechInputState
    data object Listening : SpeechInputState
    data class Success(val recognizedText: String) : SpeechInputState
    data class Error(val message: String) : SpeechInputState
}

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _speechInputState = MutableStateFlow<SpeechInputState>(SpeechInputState.Idle)
    val speechInputState: StateFlow<SpeechInputState> = _speechInputState.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _ttsError = MutableStateFlow<String?>(null)
    val ttsError: StateFlow<String?> = _ttsError.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to instantiate TextToSpeech", e)
            _ttsError.value = "Text-to-speech could not be initialized on this device."
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    Log.w(TAG, "TTS Utterance error code: $errorCode")
                }
            })
            Log.d(TAG, "TTS initialized successfully.")
        } else {
            isTtsInitialized = false
            Log.e(TAG, "TTS initialization failed with status: $status")
            _ttsError.value = "TTS engine initialization failed."
        }
    }

    fun startListening(languageCode: String = "en-US") {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _speechInputState.value = SpeechInputState.Error(
                "Speech recognition service is not available on this device or emulator."
            )
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _speechInputState.value = SpeechInputState.Listening
                    }

                    override fun onBeginningOfSpeech() {
                        _speechInputState.value = SpeechInputState.Listening
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        // Keep listening state until results arrive
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client speech recognition error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech was recognized. Please try speaking closer."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy, please try again."
                            SpeechRecognizer.ERROR_SERVER -> "Recognition server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                            else -> "Speech recognition error ($error)"
                        }
                        Log.w(TAG, "SpeechRecognizer error: $message")
                        _speechInputState.value = SpeechInputState.Error(message)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognized = matches[0]
                            _speechInputState.value = SpeechInputState.Success(recognized)
                        } else {
                            _speechInputState.value = SpeechInputState.Error("Could not recognize speech.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            _speechInputState.value = SpeechInputState.Listening
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            _speechInputState.value = SpeechInputState.Error(e.localizedMessage ?: "Failed to start listening")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping SpeechRecognizer", e)
        }
        if (_speechInputState.value is SpeechInputState.Listening) {
            _speechInputState.value = SpeechInputState.Idle
        }
    }

    fun resetSpeechState() {
        _speechInputState.value = SpeechInputState.Idle
    }

    fun speak(
        text: String,
        languageCode: String = "en",
        pitch: Float = 1.0f,
        speechRate: Float = 1.0f
    ) {
        if (!isTtsInitialized || tts == null) {
            _ttsError.value = "Text-to-speech engine is not ready."
            return
        }

        // Clean markdown symbols for natural TTS speech
        val cleanedText = text
            .replace(Regex("```[\\s\\S]*?```"), "Code snippet omitted.")
            .replace(Regex("`([^`]+)`"), "$1")
            .replace(Regex("[*#_~>]"), "")
            .trim()

        if (cleanedText.isBlank()) return

        stopSpeaking()

        val locale = when (languageCode) {
            "bn" -> Locale("bn", "IN")
            "hi" -> Locale("hi", "IN")
            else -> Locale.US
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Language $languageCode is not supported by device TTS, falling back to English.")
            tts?.setLanguage(Locale.US)
        }

        tts?.setPitch(pitch)
        tts?.setSpeechRate(speechRate)

        val utteranceId = "myra_utterance_${System.currentTimeMillis()}"
        _isSpeaking.value = true
        tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun clearTtsError() {
        _ttsError.value = null
    }

    fun release() {
        stopListening()
        stopSpeaking()
        try {
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }
}
