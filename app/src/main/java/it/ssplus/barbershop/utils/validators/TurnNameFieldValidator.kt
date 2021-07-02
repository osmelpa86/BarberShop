package it.ssplus.barbershop.utils.validation

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.utils.validators.ValidationUtils

class TurnNameFieldValidator(
    errorContainer: TextInputLayout,
    mainActivity: Activity,
    internal var inputValue: String,
    internal var isUpdate: Boolean,
    allTurnNames: ArrayList<String>
) : BaseValidator(errorContainer, mainActivity) {

    internal var allTurnNames: ArrayList<String>

    init {
        mEmptyMessage = mainActivity.resources.getString(R.string.message_validation_entry_field)
        mErrorMessage = mainActivity.resources.getString(R.string.message_validation_only_spaces)
        this.allTurnNames = allTurnNames
    }

    override fun isValid(charSequence: String): Boolean {
        var flag = true

        if (charSequence == null || charSequence.length == 0 || ValidationUtils.onlySpaces(
                charSequence
            )
        ) {
            flag = false
        } else {
            if (isUpdate == true) {
                if (!charSequence.equals(inputValue) && charSequence in allTurnNames) {
                    flag = false
                    mErrorMessage = mainActivity.resources.getString(R.string.message_validation_exits_turn_name)
                } else {
                    flag = true
                }
            } else if (isUpdate == false) {
                if (charSequence in allTurnNames) {
                    flag = false
                    mErrorMessage = mainActivity.resources.getString(R.string.message_validation_exits_turn_name)
                } else {
                    flag = true
                }
            }
        }

        return flag
    }
}
