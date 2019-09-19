package us.cyberstar.presentation.base

import us.cyberstar.presentation.delegate.toolbar.ToolbarManager

abstract class BaseToolbarActivity : BaseActivity(), ToolbarManager {

    protected fun setupActionBar(showBackButton: Boolean) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(showBackButton)
            setHomeButtonEnabled(showBackButton)
            setDisplayShowTitleEnabled(true)
        }
    }

    override fun showBackButton() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun hideBackButton() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun changeTitle(title: String) {
        supportActionBar?.let { setTitle(title) }
    }

    override fun hideToolbar() {
        supportActionBar?.hide()
    }

    override fun showToolbar() {
        supportActionBar?.show()
    }
}