package com.panic.doubletranslation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.panic.doubletranslation.common.model.KakaoModel
import com.panic.doubletranslation.view.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.lang.StringBuilder

class MainViewModel(val kakaoModel : KakaoModel) : BaseViewModel() {

    private val _translatedText1 = MutableLiveData<String>()
    val translatedText1: LiveData<String>
        get() = _translatedText1

    private val _translatedText2 = MutableLiveData<String>()
    val translatedText2: LiveData<String>
        get() = _translatedText2

    fun reqTransalate(isTop : Boolean, query : String, src : String, target1 : String, target2 : String? = null){
        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHanlder) {
            kakaoModel.reqTranslation(
                query = query,
                src_lang = src,
                target_lang = target1
            ).apply {
                val strBuilder = StringBuilder().apply {
                    translated_text.forEachIndexed { index, strings ->
                        strings.forEach {
                            append(it)
                        }
                        if (index != translated_text.count()){
                            append("\n")
                        }
                    }
                }
                if (isTop) {
                    _translatedText1.postValue(strBuilder.toString())
                    reqTransalate(
                        isTop = false,
                        query = strBuilder.toString(),
                        src = target1,
                        target1 = target2!!,
                        target2 = null)
                } else {
                    _translatedText2.postValue(strBuilder.toString())
                }
            }
        }
    }
}