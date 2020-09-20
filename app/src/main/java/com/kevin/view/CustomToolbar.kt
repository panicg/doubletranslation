package com.kevin.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.panic.doubletranslation.R
import com.panic.doubletranslation.databinding.CustomToolbarBinding

class CustomToolbar : Toolbar, Toolbar.OnMenuItemClickListener {

    private var mBinding: CustomToolbarBinding? = null


    private var DEFAULT_TITLE_COLOR : Int = 0
//    private var DEFAULT_BACKGROUND_COLOR = ExColor.baseColor1
    private var DEFAULT_BACKGROUND_COLOR = 0
    private var DEFAULT_TITLE_SIZE = 20f

    private var title: String? = ""
    private var titleTextColor = DEFAULT_TITLE_COLOR
    private var titleTextSize = 0f
    private var bgColor = DEFAULT_BACKGROUND_COLOR
    private var menuId = -1

    private var internalItemClickListener: OnMenuItemClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyleAttr, 0)

        title = a.getString(R.styleable.CustomToolbar_customTitle)
        titleTextColor = a.getColor(R.styleable.CustomToolbar_customTitleColor, DEFAULT_TITLE_COLOR)

        titleTextSize = a.getDimension(R.styleable.CustomToolbar_customTitleSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TITLE_SIZE, resources.displayMetrics))

        bgColor = a.getColor(R.styleable.CustomToolbar_backgroundColor, DEFAULT_BACKGROUND_COLOR)
//        menuId = a.getResourceId(R.styleable.CustomToolbar_inflateMenu, -1)
        a.recycle()

        init()

    }


    private fun init() {
        mBinding = CustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        initMenu()
        refreshTitle()
    }

    private fun initMenu() {
        if (menuId != -1) {
            inflateMenu(menuId)
            super.setOnMenuItemClickListener(this)
        }
    }

    override fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
        refreshTitle()
    }

    fun getTitleTextColor(): Int {
        return titleTextColor
    }

    override fun setTitleTextColor(titleTextColor: Int) {
        this.titleTextColor = titleTextColor
        refreshTitle()
    }

    fun getTitleTextSize(): Float {
        return titleTextSize
    }

    fun setTitleTextSize(titleTextSize: Float) {
        this.titleTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, titleTextSize, resources.displayMetrics)
        refreshTitle()
    }


    private fun refreshTitle() {
        setBackgroundColor(bgColor)
        mBinding?.apply {
            customToolbarTitle.text = title
            customToolbarTitle.setTextColor(titleTextColor)
            customToolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
        }

    }

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener) {
        internalItemClickListener = listener
        super.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return if (internalItemClickListener != null) {
            internalItemClickListener!!.onMenuItemClick(item)
        } else false
    }


}
