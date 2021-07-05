package it.ssplus.barbershop.utils.validators

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R

class TurnNameFieldValidator(
    errorContainer: TextInputLayout,
    mainActivity: Activity,
    internal var inputValue: String,
    internal var isUpdate: Boolean,
    allTurnNames: ArrayList<String>
) : BaseValidator(errorContainer, mainActivity) {

    private var allTurnNames: ArrayList<String>

    init {
        mEmptyMessage = mainActivity.resources.getString(R.string.message_validation_entry_field)
        mErrorMessage = mainActivity.resources.getString(R.string.message_validation_only_spaces)
        this.allTurnNames = allTurnNames
    }

    override fun isValid(charSequence: String): Boolean {
        var flag = true

        if (charSequence.isEmpty() || ValidationUtils.onlySpaces(
                charSequence
            )
        ) {
            flag = false
        } else {
            if (isUpdate) {
                if (charSequence != inputValue && charSequence in allTurnNames) {
                    flag = false
                    mErrorMessage = mainActivity.resources.getString(R.string.message_validation_exits_turn_name)
                } else {
                    flag = true
                }
            } else if (!isUpdate) {
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
