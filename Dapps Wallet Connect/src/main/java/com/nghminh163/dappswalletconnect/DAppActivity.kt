package com.nghminh163.dappswalletconnect

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.walletconnect.Session
import org.walletconnect.nullOnThrow

abstract class DAppActivity : AppCompatActivity(), Session.Callback {
    val uiScope = CoroutineScope(Dispatchers.Main)
    val networkScope = CoroutineScope(
        Dispatchers.IO
    )

    override fun onMethodCall(call: Session.MethodCall) {
    }

    open fun initialSetup(cb: Session.Callback) {
        val session = nullOnThrow { DApp.session } ?: return
        session.addCallback(cb)
    }


}