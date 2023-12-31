package com.presta.customer.ui.components.otp

import com.presta.customer.Platform
import com.presta.customer.network.onBoarding.model.PinStatus
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.otp.store.OtpStore
import com.presta.customer.ui.components.root.DefaultRootComponent
import kotlinx.coroutines.flow.StateFlow

interface OtpComponent {
    val platform: Platform
    val authStore: AuthStore

    val otpStore: OtpStore

    val authState: StateFlow<AuthStore.State>

    val state: StateFlow<OtpStore.State>
    fun onAuthEvent(event: AuthStore.Intent)
    fun onEvent(event: OtpStore.Intent)
    fun navigate(memberRefId: String?, phoneNumber: String, isTermsAccepted: Boolean, isActive: Boolean, onBoardingContext: DefaultRootComponent.OnBoardingContext, pinStatus: PinStatus?)
}