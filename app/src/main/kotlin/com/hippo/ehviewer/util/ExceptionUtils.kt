/*
 * Copyright 2016 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hippo.ehviewer.util

import com.hippo.ehviewer.R
import eu.kanade.tachiyomi.util.system.logcat
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import splitties.init.appCtx

class LowSpeedException(
    url: String,
    speed: Long,
) : IOException("Response speed too slow [url=$url, speed=${FileUtils.humanReadableByteCount(speed)}]")

fun Throwable.displayString(): String = logcat(this).let {
    when (this) {
        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException, is LowSpeedException -> appCtx.getString(R.string.error_timeout)
        else -> message ?: appCtx.getString(R.string.error_unknown)
    }
}

private fun getReadableStringInternal(e: Throwable) = when (e) {
    is MalformedURLException -> appCtx.getString(R.string.error_invalid_url)
    is SocketTimeoutException -> appCtx.getString(R.string.error_timeout)
    is UnknownHostException -> appCtx.getString(R.string.error_unknown_host)
    is ProtocolException -> if (e.message!!.startsWith("Too many follow-up requests:")) {
        appCtx.getString(R.string.error_redirection)
    } else {
        appCtx.getString(R.string.error_socket)
    }
    is SocketException, is SSLException -> appCtx.getString(R.string.error_socket)
    else -> e.message ?: appCtx.getString(R.string.error_unknown)
}

object ExceptionUtils {
    fun getReadableString(e: Throwable): String {
        logcat(e)
        val cause = e.cause
        return if (e is IOException && cause != null) {
            getReadableStringInternal(cause)
        } else {
            getReadableStringInternal(e)
        }
    }
}
