package it.ssplus.barbershop.utils

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.R.id
import com.google.android.material.snackbar.Snackbar

object SnackBarUtil {
    /**
     * Get the snackbar view
     *
     * @param snackbar
     * @return
     */
    private fun getSnackBarLayout(snackbar: Snackbar?): View? {
        return snackbar?.view
    }

    /**
     * Set snackbar Background color
     *
     * @param snackbar
     * @param background
     * @return
     */

    fun getColorfulSnackBar(
        snackbar: Snackbar,
        background: String
    ): Snackbar {
        return getColorfulSnackBar(snackbar, background, null, null)
    }

    /**
     * Set snackbar background using color resource
     *
     * @param snackbar
     * @param context
     * @param background
     * @return
     */
    fun getColorfulSnackBar(
        snackbar: Snackbar,
        context: Context,
        background: Int
    ): Snackbar {
        return getColorfulSnackBar(snackbar, context, background, 0, 0)
    }

    /**
     * Set snackbar background & textcolor using Hex color code
     *
     * @param snackbar
     * @param background
     * @param textColor
     * @return
     */
    fun getColorfulSnackBar(
        snackbar: Snackbar,
        background: String,
        textColor: String?
    ): Snackbar {
        return getColorfulSnackBar(snackbar, background, textColor, null)
    }

    /**
     * Set snackbar background & textcolor using color resource
     *
     * @param snackbar
     * @param context
     * @param background
     * @param textColor
     * @return
     */
    fun getColorfulSnackBar(
        snackbar: Snackbar,
        context: Context,
        background: Int,
        textColor: Int
    ): Snackbar {
        return getColorfulSnackBar(snackbar, context, background, textColor, 0)
    }

    /**
     * Set snackbar background, textcolor & Action button color using Hex color code
     *
     * @param snackbar
     * @param background
     * @param textColor
     * @param actionColor
     * @return
     */
    private fun getColorfulSnackBar(
        snackbar: Snackbar,
        background: String,
        textColor: String?,
        actionColor: String?
    ): Snackbar {
        val snackBarView = getSnackBarLayout(snackbar)
        if (snackBarView != null) {
            if (!TextUtils.isEmpty(background)) snackBarView.setBackgroundColor(
                Color.parseColor(
                    background
                )
            )

            // Changing snackbar text color
            //android.support.design.R.id.snackbar_text
            val snackView = snackbar.view
            val textView =
                snackView.findViewById<View>(id.snackbar_text) as TextView
            if (!TextUtils.isEmpty(textColor)) textView.setTextColor(
                Color.parseColor(
                    textColor
                )
            )

            // Changing Action button text color
            val snackViewButton =
                snackView.findViewById<View>(id.snackbar_action) as Button
            if (!TextUtils.isEmpty(actionColor)) snackViewButton.setTextColor(
                Color.parseColor(
                    actionColor
                )
            )
        }
        return snackbar
    }

    /**
     * Set snackbar background, textcolor & Action button color using color resource
     *
     * @param snackbar
     * @param context
     * @param background
     * @param textColor
     * @param actionColor
     * @return
     */
    private fun getColorfulSnackBar(
        snackbar: Snackbar,
        context: Context,
        background: Int,
        textColor: Int,
        actionColor: Int
    ): Snackbar {
        val snackBarView = getSnackBarLayout(snackbar)
        if (snackBarView != null) {
            try {
                if (background != 0) snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        background
                    )
                )

                // Changing snackbar text color


                val snackView = snackbar.view
                val textView =
                    snackView.findViewById<View>(id.snackbar_text) as TextView
                if (textColor != 0) textView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        textColor
                    )
                )

                // Changing Action button text color


                val snackViewButton =
                    snackView.findViewById<View>(id.snackbar_action) as Button
                if (textColor > 0) snackViewButton.setTextColor(
                    ContextCompat.getColor(
                        context,
                        actionColor
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return snackbar
    }

    /**
     * Set snackbar background, textcolor & Action button color using color resource and background drawable
     *
     * @param snackbar
     * @param context
     * @param background
     * @param textColor
     * @param actionColor
     * @return
     */
    fun getColorfulAndDrawableBacgroundSnackBar(
        snackbar: Snackbar,
        context: Context?,
        background: Int,
        textColor: Int,
        actionColor: Int
    ): Snackbar {
        val snackBarView = getSnackBarLayout(snackbar)
        if (snackBarView != null) {
            try {
                if (background != 0) snackBarView.background = drawable(
                    context!!,
                    background
                )

                // Changing snackbar text color


                val snackView = snackbar.view
                val textView =
                    snackView.findViewById<View>(id.snackbar_text) as TextView
                if (textColor != 0) textView.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        textColor
                    )
                )

                // Changing Action button text color


                val snackViewButton =
                    snackView.findViewById<View>(id.snackbar_action) as Button
                if (textColor > 0) snackViewButton.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        actionColor
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return snackbar
    }
}