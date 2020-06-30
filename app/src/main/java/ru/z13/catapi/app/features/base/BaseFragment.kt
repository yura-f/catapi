package ru.z13.catapi.app.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
abstract class BaseFragment: MvpAppCompatFragment() {
    abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    open fun showLoading() {

    }

    open fun hideLoading() {

    }

    open fun showError(error: Throwable) {
        hideLoading()

        error.printStackTrace()
    }
}