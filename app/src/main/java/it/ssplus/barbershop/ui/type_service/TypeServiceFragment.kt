package it.ssplus.barbershop.ui.type_service

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.ssplus.barbershop.R

class TypeServiceFragment : Fragment() {

    companion object {
        fun newInstance() = TypeServiceFragment()
    }

    private lateinit var viewModel: TypeServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_type_service, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TypeServiceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}