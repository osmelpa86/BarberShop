package it.ssplus.barbershop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
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
        button = root!!.findViewById(R.id.button)





        imageView5 = root!!.findViewById(R.id.imageView5)


        button.setOnClickListener {
            println("Pruebaaaaaaaaaaaaaaaaa")
            println("Prueba" + "->" + iconPicker.value)
            imageView5.setImageBitmap(iconPicker.value)

        }
        return root
    }
}