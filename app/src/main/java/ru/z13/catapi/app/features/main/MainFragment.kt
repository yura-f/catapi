package ru.z13.catapi.app.features.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_main.*
import ru.z13.catapi.app.App
import ru.z13.catapi.app.R
import ru.z13.catapi.app.features.base.BaseFragment
import ru.z13.catapi.app.features.main.adapters.MainStateAdapter

/**
 * @author Yura Fedorchenko (z-13.github.io)
 */
class MainFragment: BaseFragment() {
    override val layoutRes = R.layout.fragment_main

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private var adapter: MainStateAdapter? = null

    override fun onAttach(context: Context) {
        App.appComponent.inject(this)

        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MainStateAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigation.setOnNavigationItemSelectedListener {
            val numPage = when (it.itemId) {
                R.id.nav_home -> 0
                R.id.nav_favorites -> 1
                else -> 0
            }
            viewPager.setCurrentItem(numPage, false)

            return@setOnNavigationItemSelectedListener false
        }
    }
}