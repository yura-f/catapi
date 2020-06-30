package ru.z13.catapi.app.features.base

import android.os.Bundle
import moxy.MvpAppCompatActivity

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
abstract class BaseActivity: MvpAppCompatActivity() {
    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }
}