package com.flickrs.imagesearch.di

import android.content.Context
import com.flickrs.imagesearch.data.remote.ApiService
import com.flickrs.imagesearch.BuildConfig
import com.flickrs.imagesearch.data.repository.NetworkDataSource
import com.flickrs.imagesearch.GlobalApplication
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): GlobalApplication =
        app as GlobalApplication

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext app: Context): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRetrofit(): ApiService = provideRetrofitApi()

    @Singleton
    @Provides
    fun providesNetworkDataSource(apiService: ApiService): NetworkDataSource =
        NetworkDataSource(apiService)

    /**
     * Provides an instance of [ApiService] configured with Retrofit.
     *
     * @param baseUrl The base URL for the API, defaults to [BuildConfig.BASE_URL].
     * @param client A function providing an instance of [OkHttpClient], defaults to [makeOkHttpClient].
     * @return An instance of [ApiService] configured with Retrofit.
     */
    private fun provideRetrofitApi(
        baseUrl: HttpUrl = BuildConfig.BASE_URL.toHttpUrl(),
        client: () -> OkHttpClient = { makeOkHttpClient() },
    ): ApiService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            ))
            .client(client())
            .build().create(ApiService::class.java)


    /**
     * Creates an instance of OkHttpClient with specified interceptors.
     *
     * @param logging A function providing an instance of logging interceptor, defaults to [loggingInterceptor].
     * @param authorization A function providing an instance of authorization interceptor, defaults to [authorizationInterceptor].
     * @return An instance of OkHttpClient configured with the specified interceptors.
     */
    private fun makeOkHttpClient(
        logging: () -> Interceptor = { loggingInterceptor() },
        authorization: () -> Interceptor = { authorizationInterceptor() },
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging())
            .addInterceptor(authorization())
            .build()


    /**
     * Interceptor for adding authorization headers to HTTP requests.
     *
     * This interceptor adds authorization headers to the HTTP request.
     *
     * @return An instance of Interceptor for adding authorization headers.
     */
    private fun authorizationInterceptor() = Interceptor {
        val url: HttpUrl = it.request().url
            .newBuilder()
            .build()
        val request: Request = it.request().newBuilder().url(url).build()
        it.proceed(request)
    }

    /**
     * Interceptor for logging HTTP request and response information.
     *
     * This interceptor logs HTTP request and response information, including headers and bodies.
     *
     * @return An instance of Interceptor for logging HTTP requests and responses.
     */
    private fun loggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }

}