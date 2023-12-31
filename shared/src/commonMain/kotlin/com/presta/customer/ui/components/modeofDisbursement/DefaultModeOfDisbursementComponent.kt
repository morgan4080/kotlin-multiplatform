package com.presta.customer.ui.components.modeofDisbursement

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.presta.customer.network.loanRequest.model.LoanType
import com.presta.customer.network.onBoarding.model.PinStatus
import com.presta.customer.organisation.OrganisationModel
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.auth.store.AuthStoreFactory
import com.presta.customer.ui.components.modeofDisbursement.store.ModeOfDisbursementStore
import com.presta.customer.ui.components.modeofDisbursement.store.ModeOfDisbursementStoreFactory
import com.presta.customer.ui.components.profile.coroutineScope
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

class DefaultModeOfDisbursementComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    mainContext: CoroutineContext,
    private val onMpesaClicked: (
        correlationId: String,
        refId: String,
        amount: Double,
        fees: Double,
        loanPeriod: Int,
        loanType: LoanType,
        interestRate: Double,
        LoanName: String,
        loanPeriodUnit: String,
        referencedLoanRefId: String?,
        currentTerm: Boolean,
        loanOperation:String
    ) -> Unit,
    private val onBankClicked: (
        correlationId: String,
        refId: String,
        amount: Double,
        fees: Double,
        loanPeriod: Int,
        loanType: LoanType,
        interestRate: Double,
        LoanName: String,
        loanPeriodUnit: String,
        referencedLoanRefId: String?,
        currentTerm: Boolean,
        loanOperation:String
    ) -> Unit,
    private val onBackNavClicked: () -> Unit,
    private val TransactionSuccessful: () -> Unit,
    override val refId: String,
    override val amount: Double,
    override val loanPeriod: Int,
    override val loanType: LoanType,
    override val fees: Double,
    override val referencedLoanRefId: String?,
    override val currentTerm: Boolean,
    override val interestRate: Double,
    override val loanName: String,
    override val loanPeriodUnit: String,
    override val correlationId: String,
    override val loanOperation: String,
) : ModeOfDisbursementComponent, ComponentContext by componentContext {
    override val authStore: AuthStore =
        instanceKeeper.getStore {
            AuthStoreFactory(
                storeFactory = storeFactory,
                componentContext = componentContext,
                phoneNumber = null,
                isTermsAccepted = true,
                isActive = true,
                pinStatus = PinStatus.SET,
                onLogOut = {

                }
            ).create()
        }
    override val modeOfDisbursementStore =
        instanceKeeper.getStore {
            ModeOfDisbursementStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val modeOfDisbursementState: StateFlow<ModeOfDisbursementStore.State> =
        modeOfDisbursementStore.stateFlow

    override fun onAuthEvent(event: AuthStore.Intent) {
        authStore.accept(event)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val authState: StateFlow<AuthStore.State> = authStore.stateFlow
    override fun onRequestLoanEvent(event: ModeOfDisbursementStore.Intent) {
        modeOfDisbursementStore.accept(event)
    }

    private val scope = coroutineScope(mainContext + SupervisorJob())

    override fun onMpesaSelected(
        correlationId: String,
        refId: String,
        amount: Double,
        fees: Double,
        loanPeriod: Int,
        loanType: LoanType,
        interestRate: Double,
        LoanName: String,
        loanPeriodUnit: String,
        referencedLoanRefId: String?,
        currentTerm: Boolean,
        loanOperation: String
    ) {

        onMpesaClicked(
            correlationId,
            refId,
            amount,
            fees,
            loanPeriod,
            loanType,
            interestRate,
            LoanName,
            loanPeriodUnit,
            referencedLoanRefId,
            currentTerm,
            loanOperation
        )

    }



    override fun onBankSelected(
        correlationId: String,
        refId: String,
        amount: Double,
        fees: Double,
        loanPeriod: Int,
        loanType: LoanType,
        interestRate: Double,
        LoanName: String,
        loanPeriodUnit: String,
        referencedLoanRefId: String?,
        currentTerm: Boolean,
        loanOperation: String
    ) {
        onBankClicked(
            correlationId,
            refId,
            amount,
            fees,
            loanPeriod,
            loanType,
            interestRate,
            LoanName,
            loanPeriodUnit,
            referencedLoanRefId,
            currentTerm,
            loanOperation
        )
    }

    override fun onBackNavSelected() {
        onBackNavClicked()
    }

    override fun successFulTransaction() {
        TransactionSuccessful
    }

    init {
        onAuthEvent(AuthStore.Intent.GetCachedMemberData)
    }
}