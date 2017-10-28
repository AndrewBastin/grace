package andrewbastin.grace.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val fragmentList: MutableList<Fragment> = mutableListOf()
    val fragmentTitleList: MutableList<String> = mutableListOf()

    /**
     * Return the Fragment associated with a specified position.
     */
    override fun getItem(position: Int) = fragmentList[position]

    /**
     * Return the number of views available.
     */
    override fun getCount() = fragmentList.size

    /**
     * Return the title of the page associated with a specified position
     */
    override fun getPageTitle(position: Int) = fragmentTitleList[position]

    fun addPage(title: String, fragment: Fragment) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

}