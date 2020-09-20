package com.panic.doubletranslation.common.model

import com.panic.doubletranslation.common.network.KakaoAPI
import com.panic.doubletranslation.common.network.response.BaseResponse
import com.panic.doubletranslation.common.network.response.TranslationRes
import io.reactivex.Single
import retrofit2.http.Query

interface KakaoModel {
    suspend fun reqTranslation(
        query: String,
        src_lang: String,
        target_lang: String
    ): TranslationRes
}

class KakaoModelImpl(val kakao: KakaoAPI) : KakaoModel {
    override suspend fun reqTranslation(
        query: String,
        src_lang: String,
        target_lang: String
    ): TranslationRes {
        return kakao.reqTranslation(
            query = query,
            src_lang = src_lang,
            target_lang = target_lang
        )
    }
}
