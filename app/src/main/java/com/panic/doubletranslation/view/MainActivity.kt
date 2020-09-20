package com.panic.doubletranslation.view

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.ads.*
import com.kevin.common.CommonApplication
import com.kevin.common.alert
import com.panic.doubletranslation.R
import com.panic.doubletranslation.databinding.ActivityMainBinding
import com.panic.doubletranslation.view.base.BaseActivity
import com.panic.doubletranslation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    val lanList: ArrayList<Pair<String, String>> = ArrayList()

    lateinit var lanListId: Array<String>
    lateinit var lanListDesc: Array<String>

    lateinit var arrayAdapter: ArrayAdapter<CharSequence>


    override val layoutResourceId: Int = R.layout.activity_main
    override val baseViewModel: MainViewModel by viewModel()

    override fun initStartView() {


        lanListId = resources.getStringArray(R.array.language_id)
        lanListDesc = resources.getStringArray(R.array.language_desc)
        lanListId.forEachIndexed { index, s ->
            lanList.add(Pair(lanListId[index], lanListDesc[index]))
        }

        arrayAdapter = ArrayAdapter.createFromResource(
            this@MainActivity,
            R.array.language_desc,
            R.layout.spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }




        viewDataBinding.run {
            view = this@MainActivity
            sp1Src.adapter = arrayAdapter
            sp1Target.adapter = arrayAdapter
            sp2Target.adapter = arrayAdapter

            sp1Src.selectedItemPosition



            ivClear.setOnClickListener {
                etSrc1.setText("")
            }

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {

        baseViewModel.run {
            viewDataBinding.run {
                translatedText1.observe(this@MainActivity, Observer {
                    progress1.visibility = View.GONE
                    etSrc2.text = it
                })

                translatedText2.observe(this@MainActivity, Observer {
                    progress2.visibility = View.GONE
                    tvOutput.text = it
                })
            }
        }
    }

    fun clickCopy(v: View) {
        viewDataBinding.run {
            when (v) {
                ivCopy1 -> {
                    val clipboard: ClipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("DoubleTranslation", etSrc1.text)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this@MainActivity, "클립보드에 복사하였습니다.", Toast.LENGTH_SHORT).show()
                }
                ivCopy2 -> {
                    val clipboard: ClipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("DoubleTranslation", tvOutput.text)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this@MainActivity, "클립보드에 복사하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun clickTranslation(v: View) {
        viewDataBinding.run {
            if (sp1Src.selectedItemPosition == 0) {
                alert(message = "입력할 언어를 선택하세요")
            } else if (sp1Target.selectedItemPosition == 0) {
                alert(message = "첫번째 번역할 언어를 선택하세요")
            } else if (sp2Target.selectedItemPosition == 0) {
                alert(message = "이어서 두번째 번역할 언어를 선택하세요")
            } else if (etSrc1.text.toString().trim().isEmpty()) {
                alert(message = "내용을 입력하세요")
            } else if (sp1Src.selectedItemPosition == sp1Target.selectedItemPosition || sp1Src.selectedItemPosition == sp2Target.selectedItemPosition || sp1Target.selectedItemPosition == sp2Target.selectedItemPosition){
                alert(message = "같은 언어로 번역할 수 없습니다.")
            } else {
                progress1.visibility = View.VISIBLE
                progress2.visibility = View.VISIBLE
                baseViewModel.reqTransalate(
                    isTop = true,
                    query = etSrc1.text.toString(),
                    src = lanListId[sp1Src.selectedItemPosition],
                    target1 = lanListId[sp1Target.selectedItemPosition],
                    target2 = lanListId[sp2Target.selectedItemPosition]
                )
                tvDesc1.text =
                    "${lanListDesc[sp1Src.selectedItemPosition]} → ${lanListDesc[sp1Target.selectedItemPosition]}"
                tvDesc2.text =
                    "${lanListDesc[sp1Target.selectedItemPosition]} → ${lanListDesc[sp2Target.selectedItemPosition]}"
            }
        }
    }
}