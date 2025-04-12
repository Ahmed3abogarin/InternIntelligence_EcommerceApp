package com.example.ecommerceapp.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.ecommerceapp.remote.ApiInterface
import com.example.ecommerceapp.firebase.FirebaseCommon
import com.example.ecommerceapp.util.Constants.INTRODUCTIONSP
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFireStoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application: Application,
    ): SharedPreferences = application.getSharedPreferences(INTRODUCTIONSP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ) = FirebaseCommon(firestore, auth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference

    @Provides
    @Singleton
    fun provideRetrofit(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }
}