package com.presta.customer.ui.components.processLoanDisbursement

import com.presta.customer.ui.components.auth.store.AuthStore
import kotlinx.coroutines.flow.StateFlow

interface ProcessLoanDisbursementComponent {
    val authState: StateFlow<AuthStore.State>
    val authStore: AuthStore
    val amount: Double
    val fees: Double

    fun onAuthEvent(event: AuthStore.Intent)
}