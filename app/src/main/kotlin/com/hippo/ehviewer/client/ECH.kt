package com.hippo.ehviewer.client

import android.util.Log
import com.hippo.ehviewer.ui.settings.censoredDoh
import java.util.Base64
import javax.net.ssl.SSLSocket
import okhttp3.Interceptor
import okhttp3.Response
import org.conscrypt.Conscrypt
import org.conscrypt.EchRejectedException
import org.xbill.DNS.Lookup
import org.xbill.DNS.Type

private const val OUTER_SNI = "cloudflare-ech.com"
private const val DEFAULT_TTL = 300L
private val dnsjavaQuery = Lookup(OUTER_SNI, Type.HTTPS)
private var ttl: Long? = null
private var cachedEchConfig: ByteArray? = null
private var expirationTime: Long = 0

fun getCachedEchConfig(): ByteArray? = cachedEchConfig?.takeIf { System.currentTimeMillis() < expirationTime }?.also {
    Log.d("ECH", "Cache hit, TTL ${expirationTime - System.currentTimeMillis()}ms left")
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
            // TODO: ttl
            // https://github.com/relaycorp/doh-jvm/issues/8
        }.getOrElse {
            null
        } ?: run {
            var record = dnsjavaQuery.run().takeIf { dnsjavaQuery.result == Lookup.SUCCESSFUL }?.get(0)
            ttl = record?.ttl
            record?.rdataToString()
        } ?: return@runCatching
        Log.d("ECH", "Response for $OUTER_SNI is $result, TTL ${ttl ?: DEFAULT_TTL}")
        val echConfig = Regex("ech=([A-Za-z0-9+/=]+)").find(result.toString())
            ?.groupValues?.getOrNull(1)?.let { Base64.getDecoder().decode(it) }
            ?: return@runCatching
        cachedEchConfig = echConfig
        expirationTime = System.currentTimeMillis() + (ttl ?: DEFAULT_TTL).times(1000)
    }.onFailure {
        Log.w("ECH", "Failed to fetch ECH config", it)
    }
}

// TODO: Remove when Conscrypt fully implemented Retry Config
// https://github.com/google/conscrypt/pull/1044
class EchRejectedExceptionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = try {
        chain.proceed(chain.request())
    } catch (e: EchRejectedException) {
        expirationTime = 0
        throw e
    }
}
