package it.ssplus.barbershop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.ssplus.barbershop.R
import it.ssplus.iconpickert.IconPickert

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var iconPickert: IconPickert
    private lateinit var imageView5: ImageView
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        iconPickert = root!!.findViewById(R.id.iconPickert)
        button = root!!.findViewById(R.id.button)





        imageView5 = root!!.findViewById(R.id.imageView5)


        button.setOnClickListener {
            println("Pruebaaaaaaaaaaaaaaaaa")
            println("Prueba" + "->" + iconPickert.value)
            imageView5.setImageDrawable(iconPickert.value)
        }
        return root
    }
}