package com.panic.doubletranslation.view.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kevin.common.CommonApplication.Companion.applicationHandler
import com.kevin.common.changeStatusBarColor

abstract class BaseFragment<T : ViewDataBinding, R : BaseViewModel> : Fragment() {

    enum class ToolbarTheme {
        NORMAL,
        WHITE
    }

    lateinit var viewDataBinding: T
    abstract val layoutResourceId: Int

    abstract val baseViewModel: R
    abstract val title: String
    abstract val toolbarTheme: ToolbarTheme

    lateinit var progressView: ProgressView

    var isAddNavigationButton = true
    protected var isOnCreated = false

    open fun onBackPressed(): Boolean = false

    abstract fun initStartView()
    abstract fun initDataBinding()

    abstract fun initAfterBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        changeStatusBarColor()
        initStartView()
        initDataBinding()
        initProgressObserve()
        initAfterBinding()

        applicationHandler.postDelayed({isOnCreated = true}, 1000)

    }

    protected open fun getStatusBarColor() : Int {
        return Color.WHITE
    }


    private fun changeStatusBarColor() {
        activity?.changeStatusBarColor(getStatusBarColor())
    }


    private fun initProgressObserve() {
        progressView = ProgressView(context!!)
        baseViewModel.progress.observe(this, Observer { flag ->

            when (flag) {
                1 -> {
                    showProgress(Color.WHITE)
                }
                2 -> {
                    showProgress(Color.TRANSPARENT)
                }
                3 -> {
                    hideProgress()
                }
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