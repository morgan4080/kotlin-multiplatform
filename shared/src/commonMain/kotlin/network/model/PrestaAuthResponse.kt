package network.model
import kotlinx.serialization.Serializable

@Serializable
data class PrestaAuthResponse(
    val access_token: String
)
