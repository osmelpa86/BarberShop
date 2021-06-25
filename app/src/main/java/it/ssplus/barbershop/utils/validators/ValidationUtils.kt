package it.ssplus.barbershop.utils.validators

object ValidationUtils {
    fun emtyField(text: String): Boolean {
        return text.isEmpty()
    }

    fun onlySpaces(text: String): Boolean {
        return text.trim { it <= ' ' }.isEmpty()
    }
}
