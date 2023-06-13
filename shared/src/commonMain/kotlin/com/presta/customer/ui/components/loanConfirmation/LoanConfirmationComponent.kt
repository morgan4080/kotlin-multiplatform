package com.presta.customer.ui.components.loanConfirmation

import com.presta.customer.network.loanRequest.model.LoanType
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.shortTermLoans.store.ShortTermLoansStore
import kotlinx.coroutines.flow.StateFlow

interface LoanConfirmationComponent {
    val refId: String
    val amount: Double
    val loanPeriod : String
    val loanInterest:String
    val loanName:String
    val loanPeriodUnit:String
    val loanOperation: String
    val loanType:LoanType
     val referencedLoanRefId:String?
    fun onConfirmSelected(
        refid:String,
        amount:Double,
        loanPeriod:String,
        loanType:LoanType,
        loanName:String,
        referencedLoanRefId:String?)
    fun onBackNavSelected()
    val authStore: AuthStore

    val authState: StateFlow<AuthStore.State>

    val shortTermloansStore: ShortTermLoansStore

    val shortTermloansState: StateFlow<ShortTermLoansStore.State>
    fun onAuthEvent(event: AuthStore.Intent)
    fun onEvent(event: ShortTermLoansStore.Intent)

}