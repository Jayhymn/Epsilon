package com.jayhymn.smartchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.jayhymn.smartchat.fragments.ActiveChats
import com.jayhymn.smartchat.fragments.Calls
import com.jayhymn.smartchat.fragments.Pic
import com.jayhymn.smartchat.fragments.Status
import kotlinx.android.synthetic.main.activity_core_activty.*

class CoreActivty : AppCompatActivity() {
    private lateinit var tableLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core_activty)

        tableLayout = findViewById(R.id.tabLayout)


        viewPager = findViewById(R.id.viewpager)
        tabLayout.setupWithViewPager(viewpager)
        tableLayout.getTabAt(1)!!.select()

        val fragment = ActiveChats()
        switchFragments(fragment)

        tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                val text = p0!!.text.toString()
                val fragment = when(text){
                    "STATUS" -> Status()
                    "CALLS" -> Calls()
                    "CHATS" -> ActiveChats()
                    else -> Pic()
                }
                if (fragment !== ActiveChats()){
                    switchFragments(fragment)
                } else supportFragmentManager.popBackStackImmediate()
            }

        })
    }

    override fun onBackPressed() {
        val position = tableLayout.selectedTabPosition

        if (position !== 1){
            tableLayout.getTabAt(1)!!.select()
            switchFragments(ActiveChats())
        } else super.onBackPressed()
    }
    fun switchFragments(fragment: Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.core_parent,fragment)
        transaction.commit()

//        transaction.setCustomAnimations(
//            R.anim.left_right,
//            R.anim.right_left,R.anim.left_right, R.anim.right_left)

    }
}
