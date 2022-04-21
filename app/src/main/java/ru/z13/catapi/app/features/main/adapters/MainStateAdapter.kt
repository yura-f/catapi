package ru.z13.catapi.app.features.main.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.z13.catapi.app.features.favorites.FavoritesFragment
import ru.z13.catapi.app.features.home.HomeFragment

/**
 * @author Yura F (yura-f.github.io)
 */
class MainStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment.newInstance()
            1 -> FavoritesFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }
}