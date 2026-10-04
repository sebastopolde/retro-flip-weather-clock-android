package fr.papycha.htcweatherclock.location

object LocationProviderSpec {
    fun providers(hasFinePermission: Boolean): List<String> =
        if (hasFinePermission) listOf("network", "gps") else listOf("network")
}
