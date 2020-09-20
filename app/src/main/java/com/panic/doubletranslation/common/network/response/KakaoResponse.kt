package com.panic.doubletranslation.common.network.response

import com.google.gson.annotations.SerializedName



data class TranslationRes(
    @SerializedName("translated_text") val translated_text : ArrayList<Array<String>>
)