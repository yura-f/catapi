package ru.z13.catapi.app.features.base

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

/**
 * @author Yura F (yura-f.github.io)
 */
interface BaseView: MvpView {
    @Skip
    fun showError(error: Throwable)

    @Skip
    fun showProgress()

    @Skip
    fun hideProgress()
}