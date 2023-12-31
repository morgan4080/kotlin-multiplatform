package com.presta.customer.ui.components.auth.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.presta.customer.network.authDevice.data.AuthRepository
import com.presta.customer.network.authDevice.model.PrestaCheckAuthUserResponse
import com.presta.customer.network.authDevice.model.PrestaLogInResponse
import com.presta.customer.network.authDevice.model.RefreshTokenResponse
import com.presta.customer.network.onBoarding.model.PinStatus
import com.presta.customer.prestaDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val componentContext: ComponentContext,
    private val phoneNumber: String?,
    private val pinStatus: PinStatus?,
    private val isTermsAccepted: Boolean,
    private val isActive: Boolean,
    private val onLogOut: () -> Unit = {},
): KoinComponent {
    private val authRepository by inject<AuthRepository>()

    fun create(): AuthStore =
        object : AuthStore, Store<AuthStore.Intent, AuthStore.State, Nothing> by storeFactory.create(
            name = "AuthStore",
            initialState = AuthStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class AuthLoading(val isLoading: Boolean = true) : Msg()
        object ClearAuthDetails: Msg()
        data class AuthInitData(val phoneNumber: String?, val isTermsAccepted: Boolean, val isActive: Boolean, val pinStatus: PinStatus?) : Msg()
        data class UpdateContext(
            val context: Contexts,
            val title: String,
            val label: String,
            val pinCreated: Boolean,
            val pinConfirmed: Boolean,
            val error: String?
        ) : Msg()
        data class LoginFulfilled(val logInResponse: PrestaLogInResponse) : Msg()
        data class ConnectivityFulfilled(val isOnline: Boolean) : Msg()
        data class RefreshFulfilled(val refreshResponse: RefreshTokenResponse) : Msg()

        data class CheckAuthenticatedUserLoaded(val authUserResponse: PrestaCheckAuthUserResponse): Msg()
        data class AuthFailed(val error: String?) : Msg()

        data class CachedMemberData(val accessToken: String, val refreshToken: String, val refId: String, val session_id: String, val registrationFees: Double, val registrationFeeStatus: String, val phoneNumber: String, val expires_in: Long, val refresh_expires_in: Long, val tenantId: String): Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<AuthStore.Intent, Unit, AuthStore.State, Msg, Nothing>(
        prestaDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> AuthStore.State) {
            dispatch(Msg.AuthInitData(phoneNumber, isTermsAccepted, isActive, pinStatus))
        }

        override fun executeIntent(intent: AuthStore.Intent, getState: () -> AuthStore.State): Unit =
            when (intent) {
                is AuthStore.Intent.LoginUser -> loginUser(
                    intent.phoneNumber,
                    intent.pin,
                    intent.tenantId,
                    intent.refId,
                    intent.registrationFees,
                    intent.registrationFeeStatus
                )
                is AuthStore.Intent.CheckAuthenticatedUser -> checkAuthenticatedUser(intent.token)
                is AuthStore.Intent.UpdateError -> updateError(error = intent.error)
                is AuthStore.Intent.GetCachedMemberData -> getCachedMemberData()
                is AuthStore.Intent.UpdateContext -> dispatch(Msg.UpdateContext(
                    context = intent.context,
                    title = intent.title,
                    label = intent.label,
                    pinCreated = intent.pinCreated,
                    pinConfirmed = intent.pinConfirmed,
                    error = intent.error,
                ))
                is AuthStore.Intent.RefreshToken -> updateAuthToken(intent.tenantId,intent.refId)
                is AuthStore.Intent.LogOutUser -> logOutUser()
                is AuthStore.Intent.UpdateOnlineState -> dispatch(Msg.ConnectivityFulfilled(isOnline = intent.isOnline))
                is AuthStore.Intent.UpdateRefreshToken -> dispatch(Msg.RefreshFulfilled(refreshResponse = intent.refreshResponse))
                is AuthStore.Intent.UpdateLoading -> dispatch(Msg.AuthLoading())
            }

        private var loginUserJob: Job? = null

        private fun loginUser(
            phoneNumber: String,
            pin: String,
            tenantId: String,
            refId: String,
            registrationFees: Double,
            registrationFeeStatus: String
        ) {
            if (loginUserJob?.isActive == true) return

            dispatch(Msg.AuthLoading())

            loginUserJob  = scope.launch {
                authRepository
                    .loginUser(phoneNumber, pin, tenantId, refId, registrationFees, registrationFeeStatus)
                    .onSuccess {response ->
                        dispatch(Msg.LoginFulfilled(response))
                    }
                    .onFailure { e ->
                        dispatch(Msg.AuthFailed(e.message))
                    }

                dispatch(Msg.AuthLoading(false))
            }
        }

        private fun updateError(error: String?) {
            dispatch(Msg.AuthFailed(error))
        }

        private var checkAuthenticatedUserJob: Job? = null

        private fun checkAuthenticatedUser(token: String) {
            if (checkAuthenticatedUserJob?.isActive == true) return

            dispatch(Msg.AuthLoading())

            checkAuthenticatedUserJob = scope.launch {
                authRepository.checkAuthenticatedUser(token)
                    .onSuccess { response ->
                        dispatch(Msg.CheckAuthenticatedUserLoaded(response))
                    }
                    .onFailure { e ->
                        dispatch(Msg.AuthFailed(e.message))
                    }

                dispatch(Msg.AuthLoading(false))
            }
        }

        private var getUserAuthTokenJob: Job? = null

        private fun getCachedMemberData() {
            if (getUserAuthTokenJob?.isActive == true) return

            getUserAuthTokenJob = scope.launch {
                val response = authRepository.getCachedUserData()
                dispatch(Msg.CachedMemberData(
                    accessToken = response.access_token,
                    refreshToken = response.refresh_token,
                    refId = response.refId,
                    session_id = response.session_id,
                    registrationFees = response.registrationFees,
                    registrationFeeStatus = response.registrationFeeStatus,
                    phoneNumber = response.phoneNumber,
                    expires_in = response.expires_in,
                    refresh_expires_in = response.refresh_expires_in,
                    tenantId = response.tenantId
                ))
            }
        }

        private var updateAuthTokenJob: Job? = null

        private fun updateAuthToken(tenantId: String, refId: String) {
            if (updateAuthTokenJob?.isActive == true) return

            dispatch(Msg.AuthLoading())

            updateAuthTokenJob = scope.launch {
               authRepository.updateAuthToken(tenantId, refId)
                   .onSuccess { response ->
                       dispatch(Msg.RefreshFulfilled(response))
                   }
                   .onFailure { e ->
                       dispatch(Msg.AuthFailed(e.message))
                   }
            }
        }

        private var logOutUserJob: Job? = null

        private fun logOutUser() {
            if (logOutUserJob?.isActive == true) return

            dispatch(Msg.AuthLoading())

            logOutUserJob = scope.launch {
                // clear userAuthEntity DB

                authRepository.logOutUser()

                dispatch(Msg.ClearAuthDetails)

                dispatch(Msg.AuthLoading(false))

                onLogOut()
            }
        }
    }

    private object ReducerImpl: Reducer<AuthStore.State, Msg> {
        override fun AuthStore.State.reduce(msg: Msg): AuthStore.State =
            when (msg) {
                is Msg.AuthLoading -> copy(isLoading = msg.isLoading)
                is Msg.CheckAuthenticatedUserLoaded -> copy(authUserResponse = msg.authUserResponse)
                is Msg.AuthInitData -> copy(
                    phoneNumber = msg.phoneNumber,
                    isTermsAccepted = msg.isTermsAccepted,
                    isActive = msg.isActive,
                    pinStatus = msg.pinStatus
                )
                is Msg.UpdateContext -> copy(
                    context = msg.context,
                    title = msg.title,
                    label = msg.label,
                    pinCreated = msg.pinCreated,
                    pinConfirmed = msg.pinConfirmed,
                    error = msg.error
                )
                is Msg.LoginFulfilled -> copy(
                    loginResponse = msg.logInResponse,
                    isLoggedIn = true
                )
                is Msg.RefreshFulfilled -> copy(refreshTokenResponse = msg.refreshResponse)
                is Msg.AuthFailed -> copy(
                    error = msg.error,
                    isLoggedIn = false
                )
                is Msg.CachedMemberData -> copy(cachedMemberData = CachedMemberData(
                    accessToken = msg.accessToken,
                    refreshToken = msg.refreshToken,
                    session_id = msg.session_id,
                    refId = msg.refId,
                    registrationFees = msg.registrationFees,
                    registrationFeeStatus = msg.registrationFeeStatus,
                    phoneNumber = msg.phoneNumber,
                    expires_in = msg.expires_in,
                    refresh_expires_in = msg.refresh_expires_in,
                    tenantId = msg.tenantId
                ))
                is Msg.ClearAuthDetails -> copy(
                    loginResponse = null,
                    refreshTokenResponse = null,
                    authUserResponse = null
                )
                is Msg.ConnectivityFulfilled -> copy(
                    isOnline = msg.isOnline
                )
            }
    }
}