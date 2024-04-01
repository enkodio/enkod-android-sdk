package com.enkod.androidsdk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class OnOpenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.apply {
            val bundle = getIntent().extras
            if (bundle != null) {
                Log.i("handleExtras", "${intent.getStringExtra(EnKodSDK.intentName)} $${intent.getStringExtra(EnKodSDK.url)}")
                EnKodSDK.handleExtras(this@OnOpenActivity, bundle)

            }
        }
        finish()
    }
}
