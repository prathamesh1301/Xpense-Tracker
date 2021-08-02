package com.example.xpensetracker


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.xpensetracker.Fragments.HistoryFragment
import com.example.xpensetracker.Fragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home=HomeFragment()
        val history=HistoryFragment()

        setCurrentFragment(home)

        val bottomNavigation=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeIcon-> setCurrentFragment(home)

                R.id.historyIcon-> setCurrentFragment(history)
                else -> setCurrentFragment(home)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view,fragment)
            commit()
        }

}