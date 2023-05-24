package com.presta.customer.di

import com.presta.customer.network.authDevice.data.AuthRepository
import com.presta.customer.network.authDevice.data.AuthRepositoryImpl
import com.presta.customer.network.onBoarding.data.OnBoardingRepository
import com.presta.customer.network.onBoarding.data.OnBoardingRepositoryImpl
import com.presta.customer.network.otp.data.OtpRepository
import com.presta.customer.network.otp.data.OtpRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<OnBoardingRepository> { OnBoardingRepositoryImpl() }
    single<OtpRepository> { OtpRepositoryImpl() }
}