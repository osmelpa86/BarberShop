package it.ssplus.barbershop.utils.validators

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R

class CellFieldValidator(
    errorContainer: TextInputLayout,
    mainActivity: Activity,
) : BaseValidator(errorContainer, mainActivity) {

    init {
        mEmptyMessage = mainActivity.resources.getString(R.string.message_validation_entry_field)
        mErrorMessage = mainActivity.resources.getString(R.string.message_validation_only_spaces)
    }

    override fun isValid(charSequence: String): Boolean {
        val flag: Boolean

        if (charSequence.isEmpty() || ValidationUtils.onlySpaces(
                charSequence
            )
        ) {
            flag = false
        } else {
            when {
                charSequence.startsWith('5').not() -> {
                    flag = false
                    mErrorMessage =
                        mainActivity.resources.getString(R.string.message_validation_incorrect_start_cell)
                }
                charSequence.length != 8 -> {
                    flag = false
                    mErrorMessage =
                        mainActivity.resources.getString(R.string.message_validation_lenght_phone)
                }
                else -> {
                    flag = true
                }
            }
        }

        return flag
    }
}
