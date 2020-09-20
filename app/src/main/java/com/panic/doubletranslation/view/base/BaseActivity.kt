package com.panic.doubletranslation.view.base

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer

abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel> : AppCompatActivity() {

    lateinit var viewDataBinding: T
    abstract val layoutResourceId: Int

    abstract val baseViewModel: R

    abstract fun initStartView()
    abstract fun initDataBinding()

    abstract fun initAfterBinding()

    private var isSetBackButtonValid = false

    lateinit var progressView: ProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)



        initStartView()
        initDataBinding()
        initProgressObserve()
        initAfterBinding()
    }

    override fun onBackPressed() {

        val manager = supportFragmentManager

        (manager.fragments.firstOrNull() as? BaseFragment<*, *>)?.let { fragment ->
            if (fragment.onBackPressed()) {
                return
            }
            super.onBackPressed()
        } ?: run {
            super.onBackPressed()
        }
    }

    private fun initProgressObserve() {
        progressView = ProgressView(this)
        baseViewModel.progress.observe(this, Observer { flag ->
            when (flag) {
                1 -> showProgress(Color.WHITE)
                2 -> showProgress(Color.TRANSPARENT)
                3 -> hideProgress()
            }
        })
    }

    private fun showProgress(backgroundColor: Int) {
        progressView.show(viewDataBinding.root as ViewGroup, backgroundColor)
    }

    private fun hideProgress() {
        progressView.hide()
    }
}