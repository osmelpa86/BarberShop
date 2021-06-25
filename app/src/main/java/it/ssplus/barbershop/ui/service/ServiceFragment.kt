package it.ssplus.barbershop.ui.service

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.ssplus.barbershop.R

class ServiceFragment : Fragment() {

    companion object {
        fun newInstance() = ServiceFragment()
    }

    private lateinit var viewModel: ServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ServiceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}