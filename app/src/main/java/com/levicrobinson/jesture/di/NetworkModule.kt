package com.levicrobinson.jesture.di

import com.levicrobinson.jesture.BuildConfig
import com.levicrobinson.jesture.data.api.GestureApi
import com.levicrobinson.jesture.data.repository.GestureRepositoryImpl
import com.levicrobinson.jesture.domain.repository.GestureRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGestureApi(retrofit: Retrofit): GestureApi =
        retrofit.create(GestureApi::class.java)

    @Provides
    @Singleton
    fun provideGestureRepository(api: GestureApi): GestureRepository =
        GestureRepositoryImpl(api)
}
