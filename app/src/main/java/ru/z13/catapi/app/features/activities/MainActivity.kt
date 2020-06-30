package ru.z13.catapi.app.features.activities

import android.os.Bundle
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import ru.z13.catapi.app.App
import ru.z13.catapi.app.R
import ru.z13.catapi.app.features.base.BaseActivity
import javax.inject.Inject
import javax.inject.Provider

@AddToEndSingle
interface MainActivityView : MvpView {
}

class MainActivity : BaseActivity(),
    MainActivityView {
    override val layoutRes = R.layout.activity_main

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val idMainContainer = R.id.mainContainer

    private val navigator: Navigator = object : SupportAppNavigator(this, idMainContainer) {
        override fun applyCommands(commands: Array<Command>) {
            super.applyCommands(commands)
            supportFragmentManager.executePendingTransactions()
        }
    }

    @Inject
    lateinit var presenterProvider: Provider<MainActivityPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(idMainContainer)
        fragment?.let {
            super.onBackPressed()
        }
    }
}