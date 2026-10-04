package fr.papycha.htcweatherclock.weather

import java.io.IOException

class RemoteServiceException(val statusCode: Int) : IOException("HTTP $statusCode")

sealed class AddressResolution {
    data class Found(val location: ResolvedLocation) : AddressResolution()
    data object NotFound : AddressResolution()
    data object NetworkError : AddressResolution()
    data object ServiceError : AddressResolution()
}

object AddressResolver {
    fun resolve(address: String, loader: (String) -> String): AddressResolution = try {
        val json = loader(GeocodingSpec.searchUrl(address.trim().ifEmpty { "Paris" }))
        val location = GeocodingSpec.parseFirst(json)
        when {
            location != null -> AddressResolution.Found(location)
            json.trim().matches(Regex("\\[\\s*]")) -> AddressResolution.NotFound
            else -> AddressResolution.ServiceError
        }
    } catch (_: RemoteServiceException) {
        AddressResolution.ServiceError
    } catch (_: IOException) {
        AddressResolution.NetworkError
    } catch (_: Exception) {
        AddressResolution.ServiceError
    }
}
