package components.sign

import com.arkivanov.decompose.value.Value

interface SignComponent {
    val model: Value<Model>

    fun onSelected(item: String)

    data class Model(
        val items: List<String>,
    )
}