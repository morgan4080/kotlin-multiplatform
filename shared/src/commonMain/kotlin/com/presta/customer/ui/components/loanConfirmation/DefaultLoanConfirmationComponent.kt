package com.presta.customer.ui.components.loanConfirmation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.presta.customer.network.onBoarding.model.PinStatus
import com.presta.customer.organisation.OrganisationModel
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.auth.store.AuthStoreFactory
import com.presta.customer.ui.components.profile.coroutineScope
import com.presta.customer.ui.components.shortTermLoans.store.ShortTermLoansStore
import com.presta.customer.ui.components.shortTermLoans.store.ShortTermLoansStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
    val scope = CoroutineScope(context)
    lifecycle.doOnDestroy(scope::cancel)
    return scope
}
fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope =
    CoroutineScope(context, lifecycle)

class DefaultLoanConfirmationComponent(
    componentContext: ComponentContext,
    private val onConfirmClicked: () -> Unit,
    private val onBackNavClicked: () -> Unit,
    storeFactory: StoreFactory,
    refId: String,
    mainContext: CoroutineContext,
) : LoanConfirmationComponent, ComponentContext by componentContext{
    var  specificLoanId=refId

    private val models = MutableValue(
        LoanConfirmationComponent.Model(
            items = listOf()
        )
    )
    override val model: Value<LoanConfirmationComponent.Model> = models
    override fun onConfirmSelected() {
        onConfirmClicked()
    }

    override fun onBackNavSelected() {
       onBackNavClicked()
    }


    private val scope = coroutineScope(mainContext + SupervisorJob())

    override val authStore: AuthStore =
        instanceKeeper.getStore {
            AuthStoreFactory(
                storeFactory = storeFactory,
                phoneNumber = null,
                isTermsAccepted = false,
                isActive = false,
                pinStatus = PinStatus.SET
            ).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val authState: StateFlow<AuthStore.State> =authStore.stateFlow

    override val shortTermloansStore: ShortTermLoansStore =
        instanceKeeper.getStore {
            ShortTermLoansStoreFactory(
                storeFactory = storeFactory
            ).create()
        }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val shortTermloansState: StateFlow<ShortTermLoansStore.State> = shortTermloansStore.stateFlow

    override fun onAuthEvent(event: AuthStore.Intent) {
        authStore.accept(event)
    }
    override fun onEvent(event: ShortTermLoansStore.Intent) {
        shortTermloansStore.accept(event)
    }
    private var authUserScopeJob: Job? = null
    private fun checkAuthenticatedUser() {
        if (authUserScopeJob?.isActive == true) return
        authUserScopeJob = scope.launch {
            authState.collect { state ->
                if (state.cachedMemberData !== null) {
                    onAuthEvent(
                        AuthStore.Intent.CheckAuthenticatedUser(
                            token = state.cachedMemberData.accessToken
                        ))
                    onEvent(
                        ShortTermLoansStore.Intent.GetPrestaShortTermProductList(
                            token = state.cachedMemberData.accessToken,
                            refId = state.cachedMemberData.refId
                        )
                    )

                    onEvent(
                        ShortTermLoansStore.Intent.GetPrestaShortTermProductById(
                        token = state.cachedMemberData.accessToken,
                        loanId = specificLoanId
                    ))
                }
            }
        }
    }
    private var refreshTokenScopeJob: Job? = null

    private fun refreshToken() {
        if (refreshTokenScopeJob?.isActive == true) return
        refreshTokenScopeJob = scope.launch {
            authState.collect { state ->
                if (state.cachedMemberData !== null) {
                    onAuthEvent(
                        AuthStore.Intent.RefreshToken(
                            tenantId = OrganisationModel.organisation.tenant_id,
                            refId = state.cachedMemberData.refId
                        ))
                }
                this.cancel()
            }
        }
    }

    init {
        onAuthEvent(AuthStore.Intent.GetCachedMemberData)

        checkAuthenticatedUser()

        refreshToken()

    }

}