package com.hippo.ehviewer.ktbuilder

import com.google.net.cronet.okhttptransport.CronetInterceptor
import com.hippo.ehviewer.cronet.cronetHttpClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okio.FileSystem
import okio.Path

inline fun httpClient(builder: OkHttpClient.Builder.() -> Unit): OkHttpClient = OkHttpClient.Builder().apply(builder).build()
inline fun http3Client(isCronetAvailable: Boolean, client: OkHttpClient, builder: OkHttpClient.Builder.() -> Unit): OkHttpClient = client.newBuilder().apply {
    builder()
    takeIf { isCronetAvailable }?.let { addInterceptor(CronetInterceptor.newBuilder(cronetHttpClient).build()) }
}.build()
fun OkHttpClient.Builder.cache(directory: Path, maxSize: Long, fileSystem: FileSystem = FileSystem.SYSTEM) = cache(Cache(fileSystem, directory, maxSize))
