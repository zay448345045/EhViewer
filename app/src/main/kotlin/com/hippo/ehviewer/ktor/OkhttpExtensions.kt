package com.hippo.ehviewer.ktor

import com.hippo.ehviewer.EhApplication.Companion.nonCacheOkHttpClient
import com.hippo.ehviewer.client.EchRejectedExceptionInterceptor
import eu.kanade.tachiyomi.network.interceptor.UncaughtExceptionInterceptor
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.ExperimentalOkHttpApi

@OptIn(ExperimentalOkHttpApi::class)
fun OkHttpConfig.configureClient() {
    preconfigured = nonCacheOkHttpClient
    addInterceptor(EchRejectedExceptionInterceptor())
    addInterceptor(UncaughtExceptionInterceptor())
}
