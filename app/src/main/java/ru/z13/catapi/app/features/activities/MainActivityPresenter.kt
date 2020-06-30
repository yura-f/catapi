package ru.z13.catapi.app.features.activities

import ru.z13.catapi.app.Screens
import ru.z13.catapi.app.features.base.BasePresenter
import javax.inject.Inject

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
class MainActivityPresenter @Inject constructor(): BasePresenter<MainActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        router.newRootScreen(Screens.MainScreen)
    }

}