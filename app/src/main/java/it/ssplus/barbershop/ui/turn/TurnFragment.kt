package it.ssplus.barbershop.ui.turn

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.ssplus.barbershop.R

class TurnFragment : Fragment() {

    companion object {
        fun newInstance() = TurnFragment()
    }

    private lateinit var viewModel: TurnViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_turn, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TurnViewModel::class.java)
        // TODO: Use the ViewModel
    }

}