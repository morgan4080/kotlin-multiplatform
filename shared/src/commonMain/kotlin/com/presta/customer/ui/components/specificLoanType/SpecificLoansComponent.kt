package com.presta.customer.ui.components.specificLoanType

import com.presta.customer.network.loanRequest.model.LoanType
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.shortTermLoans.store.ShortTermLoansStore
import kotlinx.coroutines.flow.StateFlow

interface SpecificLoansComponent {

    val loanName: String
    val loanOperation: String
     val referencedLoanRefId: String?


    fun onConfirmSelected(
        refid: String,
        amount: Double,
        loanPeriod: Int,
        loanType: LoanType,
        LoanName: String,
        interest: Double,
        loanPeriodUnit: String,
        maxPeriodUnit: Int,
        referencedLoanRefId: String?,
        currentTerm: Boolean
    )

    fun onBackNavSelected()

    val authStore: AuthStore

    val authState: StateFlow<AuthStore.State>

    val shortTermloansStore: ShortTermLoansStore

    val shortTermloansState: StateFlow<ShortTermLoansStore.State>
    fun onAuthEvent(event: AuthStore.Intent)
    fun onEvent(event: ShortTermLoansStore.Intent)

}