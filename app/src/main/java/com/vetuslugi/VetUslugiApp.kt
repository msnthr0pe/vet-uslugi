package com.vetuslugi

import android.app.Application
import com.vetuslugi.di.AppContainer

class VetUslugiApp : Application() {
    val container by lazy { AppContainer(this) }
}
