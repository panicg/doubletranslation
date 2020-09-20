package com.kevin.common

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import com.panic.doubletranslation.common.model.KakaoModel
import com.panic.doubletranslation.common.model.KakaoModelImpl
import com.panic.doubletranslation.common.network.KakaoAPI
import com.panic.doubletranslation.common.network.NetworkConstantData
import com.panic.doubletranslation.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class CommonApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this.applicationContext
        applicationHandler = Handler(this.mainLooper)



        startKoin {
            androidContext(this@CommonApplication)
            modules(moduleAPI) //API
            modules(moduleModel) //Model
            modules(moduleViewModel) //ViewModel
        }

    }

    val moduleAPI = module {
        single<KakaoAPI> {
            Retrofit.Builder().apply {
                baseUrl(NetworkConstantData.KAKAO_BASE_URL)
                addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                addConverterFactory(GsonConverterFactory.create())
                client(client)
            }.build().create(KakaoAPI::class.java)
        }
    }

    val moduleModel = module {
        factory<KakaoModel> { KakaoModelImpl(get()) }
    }

    val moduleViewModel = module {
        viewModel {
            MainViewModel(get()) //스플래시
        }
    }


    val client: OkHttpClient = OkHttpClient.Builder().apply {
        val log = HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(log)
    }.build()


    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun registerComponentCallbacks(callback: ComponentCallbacks) {
        super.registerComponentCallbacks(callback)
    }

    override fun unregisterComponentCallbacks(callback: ComponentCallbacks) {
        super.unregisterComponentCallbacks(callback)
    }

    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks) {
        super.registerActivityLifecycleCallbacks(callback)
    }

    override fun unregisterActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks) {
        super.unregisterActivityLifecycleCallbacks(callback)
    }

    override fun registerOnProvideAssistDataListener(callback: OnProvideAssistDataListener) {
        super.registerOnProvideAssistDataListener(callback)
    }

    override fun unregisterOnProvideAssistDataListener(callback: OnProvideAssistDataListener) {
        super.unregisterOnProvideAssistDataListener(callback)
    }

    companion object {
        lateinit var appContext : Context
        lateinit var applicationHandler: Handler
    }
}
