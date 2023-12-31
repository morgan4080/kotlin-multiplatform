package com.presta.customer.ui.components.addSavings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.presta.customer.network.payments.data.PaymentTypes

interface AddSavingsStore: Store<AddSavingsStore.Intent, AddSavingsStore.State, Nothing> {
    sealed class Intent {
        data class MakePayment(
            val token: String,
            val phoneNumber: String,
            val loanRefId: String?,
            val beneficiaryPhoneNumber: String?,
            val amount: Double,
            val paymentType: PaymentTypes
        ): Intent()
        data class UpdateError(val error: String?): Intent()
        data class ClearCorrelationId(val correlationId: String?): Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val correlationId: String? = null
    )
}