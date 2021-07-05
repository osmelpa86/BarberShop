package it.ssplus.barbershop.utils.validators

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R

class PhoneFieldValidator(
    errorContainer: TextInputLayout,
    mainActivity: Activity,
) : BaseValidator(errorContainer, mainActivity) {

    override fun isValid(charSequence: String): Boolean {
        var flag = true

        if (charSequence.isNotBlank()) {
            if (charSequence.length != 8) {
                flag = false
                mErrorMessage =
                    mainActivity.resources.getString(R.string.message_validation_lenght_phone)
            }
        }
        return flag
    }
}
