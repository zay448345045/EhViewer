
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.xbill.DNS.Lookup
import org.xbill.DNS.Type

@RunWith(AndroidJUnit4::class)
class EchUpdaterTest {

    @Test
    fun testlookup() {
        val query = Lookup("cloudflare-ech.com", Type.HTTPS)
        val result = query.run().takeIf { query.result == Lookup.SUCCESSFUL }?.get(0)
        Log.d("testlookup", query.result.toString())
        Log.d("testlookup", result?.rdataToString().toString())
    }
}
