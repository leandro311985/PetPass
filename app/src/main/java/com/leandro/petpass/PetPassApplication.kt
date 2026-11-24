package com.leandro.petpass

import android.app.Application

class PetPassApplication : Application() {
    val container: PetPassContainer by lazy { PetPassContainer(this) }
}
