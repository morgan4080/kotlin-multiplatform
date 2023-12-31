package com.presta.customer.ui.composables

import ProductSelectionCard
import ShimmerBrush
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presta.customer.MR
import com.presta.customer.Platform
import com.presta.customer.network.shortTermLoans.model.LoanPeriodUnit
import com.presta.customer.network.shortTermLoans.model.PrestaLoanEligibilityResponse
import com.presta.customer.ui.components.auth.store.AuthStore
import com.presta.customer.ui.components.shortTermLoans.ShortTermLoansComponent
import com.presta.customer.ui.components.shortTermLoans.store.ShortTermLoansStore
import com.presta.customer.ui.theme.actionButtonColor
import dev.icerock.moko.resources.compose.fontFamilyResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShortTermProductList(
    component: ShortTermLoansComponent,
    state: ShortTermLoansStore.State,
    authState: AuthStore.State,
    onEvent: (ShortTermLoansStore.Intent) -> Unit,
    eligibilityResponse: PrestaLoanEligibilityResponse?
) {
    var refreshing by remember { mutableStateOf(false) }
    val refreshScope = rememberCoroutineScope()
    fun refresh() = refreshScope.launch {
        refreshing = true
        authState.cachedMemberData?.let {
            ShortTermLoansStore.Intent.GetPrestaShortTermProductList(
                it.accessToken,
                authState.cachedMemberData.refId
            )
        }?.let { onEvent.invoke(it) }
        delay(1500)
        refreshing = false

    }

    var productRefId= ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
    ) {

        Text(
            text = if (eligibilityResponse !== null && eligibilityResponse.isEligible)"Select Loan Product" else   "",
            modifier = Modifier.padding(top = 22.dp),
            fontSize = 14.sp,
            fontFamily = fontFamilyResource(MR.fonts.Poppins.medium)
        )
        
        val refreshState = rememberPullRefreshState(refreshing, ::refresh,)

        Box(Modifier.pullRefresh(refreshState)) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .fillMaxHeight(0.9f)

            ) {
                if (!refreshing){
                    state.prestaShortTermProductList.map { shortTermProduct ->
                        productRefId=shortTermProduct.refId.toString()
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                ProductSelectionCard(shortTermProduct.name!!,

                                  description =   if (shortTermProduct.interestRate != null) "Interest ${shortTermProduct.interestRate}%/"+ shortTermProduct.interestPeriodCycle else "",
                                    onClickContainer = {
                                        val loanName = shortTermProduct.name
                                        // check eligibility for selected loan
                                        if (eligibilityResponse !== null && shortTermProduct.minAmount !== null && eligibilityResponse.amountAvailable < shortTermProduct.minAmount) {
                                            component.platform.showToast("Not eligible for $loanName")
                                            return@ProductSelectionCard
                                        }
                                        component.onProductSelected(
                                            shortTermProduct.refId.toString(),
                                            loanName,
                                            referencedLoanRefId = component.referencedLoanRefId
                                        )
                                    })
                            }
                        }
                    }
                }

                if (productRefId=="") {
                    items(6) {

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                            .background(color = MaterialTheme.colorScheme.background),){
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.background),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .defaultMinSize(40.dp, 40.dp)
                                        .background(
                                            ShimmerBrush(
                                                targetValue = 1300f,
                                                showShimmer = true
                                            )
                                        )
                                        .fillMaxWidth()
                                ) {
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 50.dp))
                }

                item {
                    if (
                        state.error !== null || authState.error !== null
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                            .padding(16.dp)
                        ) {
                            state.error?.let { txt -> Text(style = MaterialTheme.typography.labelSmall, text = txt) }
                            authState.error?.let { txt -> Text(style = MaterialTheme.typography.labelSmall, text = txt) }
                        }
                    }
                }
            }

            PullRefreshIndicator(refreshing, refreshState,
                Modifier
                    .align(Alignment.TopCenter),
            contentColor = actionButtonColor)
        }

    }

}


