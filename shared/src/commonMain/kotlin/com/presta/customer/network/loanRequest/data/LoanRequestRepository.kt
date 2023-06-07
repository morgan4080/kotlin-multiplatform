package com.presta.customer.network.loanRequest.data

import com.presta.customer.network.loanRequest.model.DisbursementMethod
import com.presta.customer.network.loanRequest.model.LoanRequestResponse
import com.presta.customer.network.loanRequest.model.LoanType
import com.presta.customer.network.payments.model.PaymentsResponse

interface LoanRequestRepository {
    suspend fun requestLoan(
        token: String,
        amount: Int,
        currentTerm: String,
        customerRefId: String,
        disbursementAccountReference: String,
        disbursementMethod: DisbursementMethod,
        loanPeriod: Int,
        loanType: LoanType,
        productRefId: String,
        referencedLoanRefId: String?,
        requestId: String?,
        sessionId: String
    ): Result<LoanRequestResponse>

    suspend fun pollPaymentStatus(
        token: String,
        correlationId: String
    ): Result<LoanRequestResponse>

}