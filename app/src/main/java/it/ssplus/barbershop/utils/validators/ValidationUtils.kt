package it.ssplus.barbershop.utils.validators

object ValidationUtils {
    fun emptyField(text: String): Boolean {
        return text.isEmpty()
    }

    fun onlySpaces(text: String): Boolean {
        return text.trim { it <= ' ' }.isEmpty()
    }
}
