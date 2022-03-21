package com.example.recursivecontainermanager

import android.app.Application
import android.content.Context
import com.example.recursivecontainermanager.database.DataBase

class MainApplication: Application() {
    val database: DataBase by lazy { DataBase.getDatabase(this) }



    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        var context: Context? = null
        fun getAppContext() = context
    }
}