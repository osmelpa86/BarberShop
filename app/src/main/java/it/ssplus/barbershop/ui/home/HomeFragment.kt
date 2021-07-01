package it.ssplus.barbershop.ui.home

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.iconpickert.IconPicker

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var iconPicker: IconPicker
    private lateinit var imageView5: ImageView
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        iconPicker = root!!.findViewById(R.id.iconPickert)

        val spinner: TextInputLayout = root.findViewById(R.id.spinner)
        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (spinner.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (spinner.editText as? AutoCompleteTextView)?.setDropDownBackgroundDrawable(
            (ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background_roud_shape))
        )

        return root
    }
}