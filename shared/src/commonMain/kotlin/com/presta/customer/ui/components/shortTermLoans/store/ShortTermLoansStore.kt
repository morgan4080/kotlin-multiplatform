package com.presta.customer.ui.components.shortTermLoans.store

import com.arkivanov.mvikotlin.core.store.Store
import com.presta.customer.network.shortTermLoans.model.PrestaLoanEligibilityResponse
import com.presta.customer.network.shortTermLoans.model.PrestaLoanOfferMaturityResponse
import com.presta.customer.network.shortTermLoans.model.PrestaShortTermProductsListResponse
import com.presta.customer.network.shortTermLoans.model.PrestaShortTermTopUpListResponse

interface ShortTermLoansStore: Store<ShortTermLoansStore.Intent,ShortTermLoansStore.State,Nothing> {

    sealed class Intent{
        data class GetPrestaShortTermProductList(val token: String, val refId: String): Intent()
        data class GetPrestaShortTermTopUpList(val token: String, val session_id:String, val refId: String): Intent()
        data  class  GetPrestaShortTermProductById(val token: String, val loanId: String): Intent()
        data class GetPrestaLoanEligibilityStatus(val token: String,val  session_id: String, val customerRefId: String,): Intent()
        data  class  GetLoanProductById(val token: String, val loanRefId: String): Intent()
    }
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val prestaShortTermProductList: List<PrestaShortTermProductsListResponse> = emptyList(),
        val  prestaShortTermTopUpList: PrestaShortTermTopUpListResponse? = null,
        val prestaShortTermLoanProductById: PrestaShortTermProductsListResponse? = null,
        val prestaLoanEligibilityStatus: PrestaLoanEligibilityResponse? = null,
        val prestaLoanProductById:PrestaLoanOfferMaturityResponse?=null


    )
}
