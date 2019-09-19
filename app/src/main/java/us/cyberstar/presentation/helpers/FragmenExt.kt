package us.cyberstar.presentation.helpers

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.replaceFragment(fragmentContainer: Int, fragment: Fragment) {
    supportFragmentManager.inTransaction { replace(fragmentContainer, fragment) }
}

fun AppCompatActivity.addFragment(fragmentContainer: Int, fragment: Fragment, add: Boolean = false) {
    supportFragmentManager.beginTransaction().apply {
        add(fragmentContainer, fragment)
        if (add)
            addToBackStack(null)
        commit()
    }
}

fun Fragment?.isAdded(): Boolean {
    return this != null && isAdded ?: { false }()
}

fun AppCompatActivity.findFragment(tag: String): Fragment? {
    return supportFragmentManager.findFragmentByTag(tag)
}