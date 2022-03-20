package com.example.recursivecontainermanager

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.recursivecontainermanager.databinding.ActivityMainBinding
import com.example.recursivecontainermanager.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (appLinkAction == Intent.ACTION_VIEW) {
            if (appLinkData==null) return
            viewModel.setItemFromLink(
                appLinkData.toString().substringAfterLast('/')
            )
        }
    }

}