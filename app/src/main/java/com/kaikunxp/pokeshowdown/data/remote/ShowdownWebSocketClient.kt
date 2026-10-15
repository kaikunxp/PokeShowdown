package com.kaikunxp.pokeshowdown.data.remote

import android.util.Log
import com.kaikunxp.pokeshowdown.data.model.ShowdownMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class ShowdownWebSocketClient(
    private val serverUrl: String = "wss://sim3.psim.us/showdown/websocket"
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(10, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _messages = MutableSharedFlow<ShowdownMessage>(extraBufferCapacity = 500)
    val messages: SharedFlow<ShowdownMessage> = _messages.asSharedFlow()

    fun connect() {
        if (_isConnected.value) return
        Log.d("ShowdownWS", "Connecting to $serverUrl")

        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("ShowdownWS", "WebSocket Opened!")
                _isConnected.value = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("ShowdownWS", "IN: $text")
                val parsed = ProtocolParser.parseFrame(text)
                scope.launch {
                    for (msg in parsed) {
                        _messages.emit(msg)
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("ShowdownWS", "WebSocket Closed: $code / $reason")
                _isConnected.value = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("ShowdownWS", "WebSocket Failure: ${t.message}", t)
                _isConnected.value = false
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _isConnected.value = false
    }

    fun send(room: String, text: String) {
        val payload = if (room.isEmpty() || room == "lobby") {
            "|$text"
        } else {
            "$room|$text"
        }
        Log.d("ShowdownWS", "OUT: $payload")
        webSocket?.send(payload)
    }

    fun sendRaw(rawText: String) {
        Log.d("ShowdownWS", "OUT RAW: $rawText")
        webSocket?.send(rawText)
    }
}
