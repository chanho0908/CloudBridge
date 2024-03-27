package com.myproject.cloudbridge.ui.storemenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMenuListBinding

class MenuListFragment : Fragment() {
    private var _binding: FragmentMenuListBinding? = null
    private val binding: FragmentMenuListBinding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMenuListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
    }

    private fun initView() {

    }

    private fun initToolbar(view: View){
        with(binding.toolbar)  {
            inflateMenu(R.menu.add_menu)

            setNavigationOnClickListener{
                activity?.finish()
            }

            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.add_menu_item -> {
                        val action = MenuListFragmentDirections.actionMenuListFragmentToMenuAddFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                }
                true
            }
        }
    }
}