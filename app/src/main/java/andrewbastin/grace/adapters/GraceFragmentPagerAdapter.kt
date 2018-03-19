package andrewbastin.grace.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class GraceFragmentPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private val pageList = mutableListOf<Pair<String, Fragment>>()

    fun addPage(pageName: String, pageFragment: Fragment) {
        pageList.add(pageName to pageFragment)
    }

    override fun getItem(position: Int): Fragment = pageList[position].second

    override fun getCount() = pageList.size

    override fun getPageTitle(position: Int): CharSequence = pageList[position].first
}