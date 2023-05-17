package components.onBoarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.auth.store.AuthStore
import components.onBoarding.store.OnBoardingStore
import components.onBoarding.store.OnBoardingStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import organisation.Organisation
import organisation.OrganisationModel

class DefaultOnboardingComponent (
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    country: String,
    private val onSubmitClicked: () -> Unit,
    private val onSelectCountryClicked: () -> Unit,
    private val onSelectOrganisationClicked: () -> Unit
): OnBoardingComponent, ComponentContext by componentContext {

    override val onBoardingStore =
        instanceKeeper.getStore {
            OnBoardingStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<OnBoardingStore.State> = onBoardingStore.stateFlow

    override val models = MutableValue(
        OnBoardingComponent.Model(
            loading = false,
            inputs = listOf(
                OnBoardingComponent.InputMethod(
                    inputLabel = "Organisation",
                    fieldType = OnBoardingComponent.InputFields.ORGANISATION,
                    valueType = String,
                    errorMessage = ""
                ),
                OnBoardingComponent.InputMethod(
                    inputLabel = "Country",
                    fieldType = OnBoardingComponent.InputFields.COUNTRY,
                    valueType = Int,
                    errorMessage = ""
                ),
                OnBoardingComponent.InputMethod(
                    inputLabel = "Phone number",
                    fieldType = OnBoardingComponent.InputFields.PHONE_NUMBER,
                    valueType = Int,
                    errorMessage = ""
                )
            ),
            label = "Start your digital lending journey here.",
            title = "Get your loans appraised digitally from anywhere, anytime.",
            country = country,
            organisation = OrganisationModel.organisation,
            errorMessage = null
        )
    )

    override val model: Value<OnBoardingComponent.Model> = models

    override fun onSubmit(organisation: Organisation, phone_number: String?, email: String?) {
        models.update {
            it.copy(
                loading = true
            )
        }
        // call submit api
        // if successful call
        // update error message if need be and ignore on submit clicked
        onSubmitClicked()

        models.update {
            it.copy(
                loading = false
            )
        }
    }

    override fun onSelectCountry() {
       onSelectCountryClicked()
    }

    override fun onSelectOrganisation() {
        onSelectOrganisationClicked()
    }

    override fun onCountrySelected(country: String) {
        models.update {
            it.copy(
                country = country
            )
        }
    }
}