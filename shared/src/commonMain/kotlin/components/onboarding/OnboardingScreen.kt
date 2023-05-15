package components.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.presta.customer.MR
import components.countries.Country
import composables.ActionButton
import composables.TextInputContainer
import dev.icerock.moko.resources.compose.fontFamilyResource
import helpers.LocalSafeArea
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(component: OnboardingComponent) {
    val model by component.model.subscribeAsState()
    val country: Country = Json.decodeFromString(model.country)
    Scaffold (modifier = Modifier.fillMaxHeight(1f).padding(LocalSafeArea.current)) {
        LazyColumn (
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 30.dp)
        ) {
            item {
                Row {
                    Text(
                        text = model.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = fontFamilyResource(MR.fonts.Poppins.light)
                    )
                }
            }
            item {
                Row {
                    Text(
                        text = model.title,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = fontFamilyResource(MR.fonts.Poppins.bold)
                    )
                }
            }
            item {
                Column (
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    loop@ for (input in model.inputs) {
                        if (input.fieldType == OnboardingComponent.InputFields.ORGANISATION) {
                            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                                TextInputContainer(
                                    label = input.inputLabel,
                                    inputValue = "",
                                    enabled = false,
                                    callback = {
                                        component.onSelectOrganisation()
                                    }
                                )
                            }
                            break@loop
                        }
                    }
                    Row(modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()) {
                        loop@ for (input in model.inputs) {
                            if (input.fieldType == OnboardingComponent.InputFields.ORGANISATION) {
                                continue@loop
                            }

                            if (input.fieldType == OnboardingComponent.InputFields.COUNTRY) {
                                Column (modifier = Modifier.fillMaxWidth(0.36f).padding(end = 10.dp)) {
                                    TextInputContainer(
                                        label = input.inputLabel,
                                        inputValue = "+${country.code}",
                                        imageUrl = "https://flagcdn.com/28x21/${country.alpha2Code.lowercase()}.png",
                                        enabled = false,
                                        callback = {
                                            component.onSelectCountry()
                                        }
                                    )
                                }
                            }

                            if (input.fieldType == OnboardingComponent.InputFields.PHONE_NUMBER) {
                                Column (modifier = Modifier.fillMaxWidth()) {
                                    TextInputContainer(
                                        label = input.inputLabel,
                                        inputValue = ""
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row {
                    ActionButton("Continue", onClickContainer = {
                        component.onSubmit()
                    }, loading = false)
                }
            }
        }
    }
}