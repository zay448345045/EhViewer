-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Ktor logger
-dontwarn org.slf4j.impl.StaticLoggerBinder

# https://issuetracker.google.com/222232895
-dontwarn androidx.window.extensions.**
-dontwarn androidx.window.sidecar.Sidecar*

-keepattributes LineNumberTable

-allowaccessmodification
-repackageclasses

# okhttp3.dnsoverhttps
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Keep all classes that inherit from JavaScriptInterface from being obfuscated
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# tech.relaycorp.doh

-dontwarn lombok.Generated
-dontwarn sun.net.spi.nameservice.NameServiceDescriptor

# dnsjava

-dontwarn com.sun.jna.Library
-dontwarn com.sun.jna.Memory
-dontwarn com.sun.jna.Native
-dontwarn com.sun.jna.Pointer
-dontwarn com.sun.jna.Structure$ByReference
-dontwarn com.sun.jna.Structure$FieldOrder
-dontwarn com.sun.jna.Structure
-dontwarn com.sun.jna.WString
-dontwarn com.sun.jna.platform.win32.Win32Exception
-dontwarn com.sun.jna.ptr.IntByReference
-dontwarn com.sun.jna.win32.W32APIOptions
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn java.net.spi.InetAddressResolverProvider
-dontwarn org.xbill.DNS.spi.DnsjavaInetAddressResolverProvider