package com.presta.customer.network.loanRequest.client

import com.presta.customer.network.NetworkConstants
import com.presta.customer.network.loanRequest.errorHandler.loanRequestErrorHandler
import com.presta.customer.network.loanRequest.model.DisbursementMethod
import com.presta.customer.network.loanRequest.model.LoanQuotationResponse
import com.presta.customer.network.loanRequest.model.LoanRequestResponse
import com.presta.customer.network.loanRequest.model.LoanType
import com.presta.customer.network.loanRequest.model.PrestaLoanPollingResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class LoanRequestData @OptIn(ExperimentalSerializationApi::class) constructor(
    val amount: Int,
    val currentTerm: Boolean,
    val customerRefId: String,
    val disbursementAccountReference: String,
    val disbursementMethod: DisbursementMethod,
    val loanPeriod: Int,
    @EncodeDefault val loanType: LoanType,
    val productRefId: String,
    val referencedLoanRefId: String?,
    val requestId: String? = null,
    val sessionId: String,
)

class PrestaLoanRequestClient(
    private val httpClient: HttpClient
) {
    suspend fun sendLoanRequest(
        token: String,
        amount: Int,
        currentTerm: Boolean,
        customerRefId: String,
        disbursementAccountReference: String,
        disbursementMethod: DisbursementMethod,
        loanPeriod: Int,
        loanType: LoanType,
        productRefId: String,
        referencedLoanRefId: String?,
        requestId: String? = null,
        sessionId: String
    ): LoanRequestResponse {
        return loanRequestErrorHandler {
            httpClient.post(NetworkConstants.PrestaLoanRequest.route) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(
                    LoanRequestData(
                        amount = amount,
                        currentTerm = currentTerm,
                        customerRefId = customerRefId,
                        disbursementAccountReference = disbursementAccountReference,
                        disbursementMethod = disbursementMethod,
                        loanPeriod = loanPeriod,
                        loanType = loanType,
                        productRefId = productRefId,
                        referencedLoanRefId = referencedLoanRefId,
                        requestId = requestId,
                        sessionId = sessionId
                    )
                )
            }
        }
    }
    suspend fun pollLoanApplicationStatus(
        token: String,
        requestId: String
    ): PrestaLoanPollingResponse {
        println(":::::::::::::::;requestId")
        println(requestId)
        return loanRequestErrorHandler {
            httpClient.get("${NetworkConstants.PrestaLoanRequest.route}/${requestId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
        }
    }
    suspend fun sendLoanQuotationRequest(
        token: String,
        amount: Int,
        currentTerm: Boolean,
        customerRefId: String,
        disbursementAccountReference: String,
        disbursementMethod: DisbursementMethod,
        loanPeriod: Int,
        loanType: LoanType,
        productRefId: String,
        referencedLoanRefId: String?,
        requestId: String? = null,
        sessionId: String
    ): LoanQuotationResponse {
        return loanRequestErrorHandler {
            httpClient.post(NetworkConstants.PrestaLoanQuotation.route) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(
                    LoanRequestData(
                        amount = amount,
                        currentTerm = currentTerm,
                        customerRefId = customerRefId,
                        disbursementAccountReference = disbursementAccountReference,
                        disbursementMethod = disbursementMethod,
                        loanPeriod = loanPeriod,
                        loanType = loanType,
                        productRefId = productRefId,
                        referencedLoanRefId = referencedLoanRefId,
                        requestId = requestId,
                        sessionId = sessionId
                    )
                )
            }
        }
    }
}
