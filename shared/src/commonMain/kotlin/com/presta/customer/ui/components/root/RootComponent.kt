package com.presta.customer.ui.components.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.presta.customer.ui.components.auth.AuthComponent
import com.presta.customer.ui.components.onBoarding.OnBoardingComponent
import com.presta.customer.ui.components.otp.OtpComponent
import com.presta.customer.ui.components.registration.RegistrationComponent
import com.presta.customer.ui.components.rootBottomStack.RootBottomComponent
import com.presta.customer.ui.components.splash.SplashComponent
import com.presta.customer.ui.components.welcome.WelcomeComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        class SplashChild(val component: SplashComponent) : Child()
        class WelcomeChild(val component: WelcomeComponent) : Child()
        class OnboardingChild(val component: OnBoardingComponent) : Child()
        class OTPChild(val component: OtpComponent) : Child()
        class RegisterChild(val component: RegistrationComponent) : Child()
        class AuthChild(val component: AuthComponent) : Child()
        class RootBottomChild(val component: RootBottomComponent) : Child()
    }
}