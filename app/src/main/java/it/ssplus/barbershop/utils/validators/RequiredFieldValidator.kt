package it.ssplus.barbershop.utils.validators

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout

import it.ssplus.barbershop.R
import it.ssplus.barbershop.utils.validation.BaseValidator

class RequiredFieldValidator(errorContainer: TextInputLayout, mainActivity: Activity) :
    BaseValidator(errorContainer, mainActivity) {

    init {
        mEmptyMessage = mainActivity.resources.getString(R.string.message_validation_entry_field)
        mErrorMessage = mainActivity.resources.getString(R.string.message_validation_only_spaces)
    }

    override fun isValid(charSequence: String): Boolean {
        return !(charSequence == null || charSequence.length == 0 || ValidationUtils.onlySpaces(
            charSequence
        ))
    }
}
