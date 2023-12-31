package com.presta.customer.ui.components.processingTransaction.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.presta.customer.network.payments.model.PrestaPollingResponse
import com.presta.customer.prestaDispatchers

class ProcessingTransactionStoreFactory(
    private val storeFactory: StoreFactory
) {
    fun create(): ProcessingTransactionStore =
        object: ProcessingTransactionStore, Store<ProcessingTransactionStore.Intent, ProcessingTransactionStore.State, Nothing> by storeFactory.create(
            name = "ProcessingTransactionStore",
            initialState = ProcessingTransactionStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}
    private sealed class Msg {
        data class PaymentPollingLoaded(val paymentStatus: PrestaPollingResponse): Msg()
        data class ProcessingTransactionLoading(val isLoading: Boolean = true): Msg()
        data class ProcessingTransactionFailed(val error: String?): Msg()
        data class  ClearError(val error: String?): Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<ProcessingTransactionStore.Intent, Unit, ProcessingTransactionStore.State, Msg, Nothing>(
        prestaDispatchers.main
    ) {

        override fun executeAction(action: Unit, getState: () -> ProcessingTransactionStore.State) {

        }

        override fun executeIntent(intent: ProcessingTransactionStore.Intent, getState: () -> ProcessingTransactionStore.State): Unit =
            when(intent) {
                is ProcessingTransactionStore.Intent.UpdatePaymentStatus -> dispatch(Msg.PaymentPollingLoaded(paymentStatus = intent.paymentStatus))
                is ProcessingTransactionStore.Intent.UpdateError -> dispatch(Msg.ClearError(intent.error))
                is ProcessingTransactionStore.Intent.UpdateLoading -> dispatch(Msg.ProcessingTransactionLoading(intent.isLoading))
            }
    }

    private object ReducerImpl: Reducer<ProcessingTransactionStore.State, Msg> {
        override fun ProcessingTransactionStore.State.reduce(msg: Msg): ProcessingTransactionStore.State =
            when (msg) {
                is Msg.PaymentPollingLoaded -> copy(paymentStatus = msg.paymentStatus)
                is Msg.ProcessingTransactionLoading -> copy(isLoading = msg.isLoading)
                is Msg.ProcessingTransactionFailed -> copy(error = msg.error)
                is Msg.ClearError -> copy(error = msg.error)
            }
    }
}