package com.presta.customer.ui.components.processLoanDisbursement.poller

import com.presta.customer.network.loanRequest.data.LoanRequestRepository
import com.presta.customer.network.payments.model.PrestaPollingResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class CoroutineLoanRequestPoller(
    private val dataRepository: LoanRequestRepository,
    private val dispatcher: CoroutineDispatcher
): Poller {
    @OptIn(DelicateCoroutinesApi::class)
    override fun poll(delay: Long, token: String, correlationId: String): Flow<Result<PrestaPollingResponse>> {
        return channelFlow {
            while (!isClosedForSend) {
                val data =  dataRepository.pollPaymentStatus(token, correlationId)
                send(data)
                delay(delay)
            }
        }.flowOn(dispatcher)
    }

    override fun close() {
        dispatcher.cancel()
    }
}