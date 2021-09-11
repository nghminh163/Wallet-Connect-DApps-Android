package com.nghminh163.dappsnft

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.nghminh163.dappswalletconnect.DApp
import com.nghminh163.dappswalletconnect.DAppActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.walletconnect.Session


class MainActivity : DAppActivity() {
    lateinit var statusTxt: TextView
    lateinit var connectBtn: Button
    lateinit var disconnectBtn: Button
    lateinit var sendBtn: Button

    override fun onStatus(status: Session.Status) {
        when (status) {
            Session.Status.Approved -> sessionApproved()
            Session.Status.Closed -> sessionClosed()
            Session.Status.Connected -> requestConnectionToWallet()
            Session.Status.Disconnected,
            is Session.Status.Error -> {
                // TODO: Handle error
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setThreadPolicy()
        setView()

    }

    override fun onStart() {
        super.onStart()
        initialSetup(this)
    }


    override fun initialSetup(cb: Session.Callback) {
        DApp(applicationContext)
        super.initialSetup(cb)
    }

    override fun onDestroy() {
        DApp.session.removeCallback(this)
        super.onDestroy()
    }

    fun setThreadPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun setView() {
        statusTxt = findViewById(R.id.status_txt)
        connectBtn = findViewById(R.id.connect_btn)
        disconnectBtn = findViewById(R.id.disconnect_btn)
        sendBtn = findViewById(R.id.send_transactions)
        val that = this;
        connectBtn.setOnClickListener {
            networkScope.launch {
                DApp.startSession(that)
            }
        }
        disconnectBtn.setOnClickListener {
            networkScope.launch {
                DApp.session.kill()
            }
        }
        sendBtn.setOnClickListener {
            sendFunction()
        }
    }

    private fun sessionApproved() {
        uiScope.launch {
            var status = ""

            withContext(Dispatchers.IO) {
                status = DApp.session.approvedAccounts().toString()
            }
            statusTxt?.text =
                "Connected: ${status}"
            connectBtn?.visibility = View.GONE
            disconnectBtn?.visibility = View.VISIBLE
        }
    }

    private fun requestConnectionToWallet() {
        uiScope.launch {
            val i = Intent(Intent.ACTION_VIEW)
            var wcUri = ""
            withContext(Dispatchers.IO) {
                wcUri = DApp.config.toWCUri()
            }
            i.data = Uri.parse(wcUri)
            startActivity(i)
        }

    }

    private fun sessionClosed() {
        uiScope.launch {
            statusTxt?.text = "Disconnected"
            connectBtn?.visibility = View.VISIBLE
            disconnectBtn?.visibility = View.GONE
        }
    }

    private fun navigateToWallet() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("wc:")
        startActivity(i)
    }


    private fun sendFunction() {
        uiScope.launch {

            var from = ""
            withContext(Dispatchers.IO) {
                from = DApp.session.approvedAccounts()?.first().toString()
            }

            val _txRequest = System.currentTimeMillis()
            DApp.session.performMethodCall(
                Session.MethodCall.SendTransaction(
                    _txRequest,
                    from,
                    "0x24EdA4f7d0c466cc60302b9b5e9275544E5ba552",
                    null,
                    null,
                    null,
                    "0x5AF3107A4000",
                    ""
                ),
                ::handleResponse
            )
            navigateToWallet()

        }
    }


    private fun handleResponse(resp: Session.MethodCall.Response) {

    }

}