package com.nkhearn.psm.websocket

import android.util.Log
import com.google.gson.Gson
import com.nkhearn.psm.models.WebSocketMessage
import okhttp3.*

class SolarWebSocketListener(
    private val gson: Gson,
    private val onMessageReceived: (WebSocketMessage) -> Unit,
    private val onStatusChanged: (Boolean) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("SolarWebSocket", "Connected")
        onStatusChanged(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("SolarWebSocket", "Message: $text")
        try {
            val message = gson.fromJson(text, WebSocketMessage::class.java)
            onMessageReceived(message)
        } catch (e: Exception) {
            Log.e("SolarWebSocket", "Error parsing message", e)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d("SolarWebSocket", "Closing: $code / $reason")
        onStatusChanged(false)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("SolarWebSocket", "Error: " + t.message)
        onStatusChanged(false)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("SolarWebSocket", "Closed: $code / $reason")
        onStatusChanged(false)
    }
}
