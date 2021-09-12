package com.nghminh163.dappswalletconnect

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.*
import org.walletconnect.nullOnThrow
import java.io.File
import java.util.*

class DApp(val context: Context, metaData: Session.PeerMeta) {
    init {
        initClient()
        initMoshi()
        initSessionStorage()
        metaClient = metaData
    }

    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun initSessionStorage() {
        storage = FileWCSessionStore(
            File(context.cacheDir, "session_store.json").apply { createNewFile() },
            moshi
        )
    }

    companion object {
        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var storage: WCSessionStore
        lateinit var config: Session.Config
        lateinit var session: Session
        lateinit var metaClient: Session.PeerMeta
        fun resetSession() {
            nullOnThrow { session }?.clearCallbacks()
            val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
            config = Session.Config(
                UUID.randomUUID().toString(),
                "https://bridge.walletconnect.org",
                key
            )
            session = WCSession(
                config.toFullyQualifiedConfig(),
                MoshiPayloadAdapter(moshi),
                storage,
                OkHttpTransport.Builder(client, moshi),
                metaClient
            )
            session.offer()
        }

        fun startSession(cb: Session.Callback) {
            resetSession()
            session.addCallback(cb)

        }
    }
}
