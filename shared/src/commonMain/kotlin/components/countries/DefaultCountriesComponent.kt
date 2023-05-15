package components.countries

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.presta.customer.MR

class DefaultCountriesComponent(
    componentContext: ComponentContext,
    private val onSelectedCountry: (country: String) -> Unit,
    private val onBackClicked: () -> Unit,
) : CountriesComponent, ComponentContext by componentContext {
    override val model: Value<CountriesComponent.Model> =
        MutableValue(
            CountriesComponent.Model(
                countriesJSON = MR.files.Countries
            )
        )

    override fun onSelected(country: String) {
        onSelectedCountry(country)
    }

    override fun onBack() {
        onBackClicked()
    }
}