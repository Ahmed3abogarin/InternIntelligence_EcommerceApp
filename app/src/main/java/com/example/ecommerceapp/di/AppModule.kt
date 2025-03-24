package com.example.ecommerceapp.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
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
        application: Application
    ) = application.getSharedPreferences(INTRODUCTIONSP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore, auth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}