package com.kabadiwalaconnect

import android.app.Application
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.KabadiwalaRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KabadiwalaApplication : Application() {

    companion object {
        private var instance: KabadiwalaApplication? = null
        fun getInstance(): KabadiwalaApplication = instance!!
    }

    val repository: KabadiwalaRepository = KabadiwalaRepositoryImpl()

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize any required components
        CoroutineScope(Dispatchers.IO).launch {
            repository.refreshMarketPrices()
        }
    }
}