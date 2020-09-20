package com.panic.doubletranslation.common.network

import com.panic.doubletranslation.common.network.response.BaseResponse
import com.panic.doubletranslation.common.network.response.TranslationRes
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface KakaoAPI {

    object URL {
        const val URL_TRANSLATION = "/v2/translation/translate"
    }

    @GET(URL.URL_TRANSLATION)
    suspend fun reqTranslation(
        @Header("Authorization") token: String = "KakaoAK ${NetworkConstantData.KAKAO_KEY}",
        @Query("query") query: String,
        @Query("src_lang") src_lang: String,
        @Query("target_lang") target_lang: String
    ): TranslationRes
}