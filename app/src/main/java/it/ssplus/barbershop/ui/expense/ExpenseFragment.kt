package it.ssplus.barbershop.ui.expense

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.ssplus.barbershop.R


class ExpenseFragment : Fragment() {

    companion object {
        fun newInstance() = ExpenseFragment()
    }

    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        expenseViewModel =
            ViewModelProvider(this).get(ExpenseViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_expense, container, false)
        val textView: TextView = root.findViewById(R.id.text_expense)
        expenseViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.expense, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.expense_category -> {
                findNavController().navigate(R.id.expense_category)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}