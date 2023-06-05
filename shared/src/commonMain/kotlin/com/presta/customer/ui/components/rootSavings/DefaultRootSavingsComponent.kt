package com.presta.customer.ui.components.rootSavings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.presta.customer.ui.components.addSavings.AddSavingsComponent
import com.presta.customer.ui.components.addSavings.DefaultAddSavingsComponent
import com.presta.customer.ui.components.processingTransaction.DefaultProcessingTransactionComponent
import com.presta.customer.ui.components.processingTransaction.ProcessingTransactionComponent
import com.presta.customer.ui.components.savings.DefaultSavingsComponent
import com.presta.customer.ui.components.savings.SavingsComponent
import com.presta.customer.ui.components.savingsTransactionHistory.DefaultSavingsTransactionHistoryComponent
import com.presta.customer.ui.components.savingsTransactionHistory.SavingsTransactionHistoryComponent
import prestaDispatchers

class DefaultRootSavingsComponent(
    componentContext: ComponentContext,
    val storeFactory: StoreFactory,
    val pop: () -> Unit = {},
): RootSavingsComponent, ComponentContext by componentContext {
    private val savingsNavigation = StackNavigation<ConfigSavings>()

    private val _childSavingsStack =
        childStack(
            source = savingsNavigation,
            initialConfiguration = ConfigSavings.SavingsHome,
            handleBackButton = true,
            childFactory = ::createSavingsChild,
            key = "applySavingsStack"
        )

    override val childSavingsStack: Value<ChildStack<*, RootSavingsComponent.ChildSavings>> = _childSavingsStack

    private fun createSavingsChild(config: ConfigSavings, componentContext: ComponentContext): RootSavingsComponent.ChildSavings =
        when (config) {
            is ConfigSavings.SavingsHome -> RootSavingsComponent.ChildSavings.SavingsHomeChild(
                savingsHomeComponent(componentContext)
            )
            is ConfigSavings.AddSavings -> RootSavingsComponent.ChildSavings.AddSavingsChild(
                addSavingsComponent(componentContext)
            )
            is ConfigSavings.ProcessingTransaction -> RootSavingsComponent.ChildSavings.ProcessingTransactionChild(
                processingTransactionComponent(componentContext)
            )
            is ConfigSavings.SavingsTransactionHistory -> RootSavingsComponent.ChildSavings.TransactionHistoryChild(
                savingsTransactionHistoryComponent(componentContext)
            )

        }

    private fun savingsHomeComponent(componentContext: ComponentContext): SavingsComponent =
        DefaultSavingsComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            mainContext = prestaDispatchers.main,
            onPop = {
                pop()
            },
            onAddSavingsClicked = {
                savingsNavigation.push(ConfigSavings.AddSavings)

            },
            onSeeAlClicked = {
                savingsNavigation.push(ConfigSavings.SavingsTransactionHistory)
            }

        )

    private fun addSavingsComponent(componentContext: ComponentContext): AddSavingsComponent =
        DefaultAddSavingsComponent(
           componentContext = componentContext,
            storeFactory = storeFactory,
            mainContext = prestaDispatchers.main,
            onConfirmClicked = {
                //Navigate to processing Transaction
                savingsNavigation.push(ConfigSavings.ProcessingTransaction)

            },
            onBackNavClicked = {
                savingsNavigation.pop()
            }
        )


    private fun processingTransactionComponent(componentContext: ComponentContext): ProcessingTransactionComponent =
        DefaultProcessingTransactionComponent(
            componentContext = componentContext
        )

    private fun savingsTransactionHistoryComponent(componentContext: ComponentContext): SavingsTransactionHistoryComponent =
        DefaultSavingsTransactionHistoryComponent(
            componentContext = componentContext
        )


    private sealed class ConfigSavings : Parcelable {
        @Parcelize
        object SavingsHome: ConfigSavings()

        @Parcelize
        object AddSavings: ConfigSavings()

        @Parcelize
        object ProcessingTransaction: ConfigSavings()

        @Parcelize
        object SavingsTransactionHistory: ConfigSavings()

    }
}