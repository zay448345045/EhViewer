package com.hippo.ehviewer.client

import android.util.Log
import com.hippo.ehviewer.ui.settings.censoredDoh
import java.util.Base64
import javax.net.ssl.SSLSocket
import org.conscrypt.Conscrypt
import org.xbill.DNS.Lookup
import org.xbill.DNS.Type

private const val OUTER_SNI = "cloudflare-ech.com"
private const val CACHE_EXPIRATION_TIME = 5 * 60 * 1000
private val dnsjavaQuery = Lookup(OUTER_SNI, Type.HTTPS)
private var cachedEchConfig: ByteArray? = null
private var expirationTime: Long = 0

fun getCachedEchConfig(): ByteArray? = cachedEchConfig?.takeIf { System.currentTimeMillis() < expirationTime }?.also {
    Log.d("ECH", "Cache hit")
}

fun logEchConfigList(socket: SSLSocket, host: String) {
    Conscrypt.getEchConfigList(socket)?.also { echConfigList ->
        Log.d("ECH", "ECH Config List (${echConfigList.size} bytes) for $host:")
        Log.d("ECH", Base64.getEncoder().encodeToString(echConfigList))
    }
}

suspend fun fetchAndCacheEchConfig() {
    runCatching {
        val result = runCatching {
            censoredDoh?.lookUp(OUTER_SNI, "HTTPS")?.data?.firstOrNull()?.takeIf { it.isNotEmpty() }
        }.getOrElse {
            null
        } ?: run {
            dnsjavaQuery.run().takeIf { dnsjavaQuery.result == Lookup.SUCCESSFUL }?.get(0)?.rdataToString()
        } ?: return@runCatching
        Log.d("ECH", "Response for $OUTER_SNI is $result")
        val echConfig = Regex("ech=([A-Za-z0-9+/=]+)").find(result.toString())
            ?.groupValues?.getOrNull(1)?.let { Base64.getDecoder().decode(it) }
            ?: return@runCatching
        cachedEchConfig = echConfig
        expirationTime = System.currentTimeMillis() + CACHE_EXPIRATION_TIME
    }.onFailure {
        Log.w("ECH", "Failed to fetch ECH config", it)
    }
}
