package it.ssplus.barbershop.utils.validation

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.utils.validators.ValidationUtils

class TurnHourFieldValidator(
    errorContainer: TextInputLayout,
    mainActivity: Activity,
    internal var inputValue: String,
    internal var isUpdate: Boolean,
    allTurnHours: ArrayList<String>
) : BaseValidator(errorContainer, mainActivity) {

    internal var allTurnHours: ArrayList<String>

    init {
        mEmptyMessage = mainActivity.resources.getString(R.string.message_validation_entry_field)
        mErrorMessage = mainActivity.resources.getString(R.string.message_validation_only_spaces)
        this.allTurnHours = allTurnHours
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
                if (!charSequence.equals(inputValue) && charSequence in allTurnHours) {
                    flag = false
                    mErrorMessage = mainActivity.resources.getString(R.string.message_validation_exits_turn_hour)
                } else {
                    flag = true
                }
            } else if (isUpdate == false) {
                if (charSequence in allTurnHours) {
                    flag = false
                    mErrorMessage = mainActivity.resources.getString(R.string.message_validation_exits_turn_hour)
                } else {
                    flag = true
                }
            }
        }

        return flag
    }
}
