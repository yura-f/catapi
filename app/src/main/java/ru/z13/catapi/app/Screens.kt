package ru.z13.catapi.app

import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.z13.catapi.app.features.main.MainFragment

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
object Screens {
    object MainScreen : SupportAppScreen() {
        override fun getFragment() = MainFragment.newInstance()
    }
}