package com.example.remindapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.databinding.FragmentHomeBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var remindAdapter: RemindAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))
        homeViewModel =
            ViewModelProvider(this, HomeViewModel.HomeViewModelFactory(repo)).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonClickListener()
        setAdapters()
        setObservers()
        fetchRemindList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setButtonClickListener() {
        binding.btAddRemind.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_navigation_home_to_navigation_edit)
        )
    }

    private fun setAdapters() {
        remindAdapter = RemindAdapter(object: RemindAdapter.RemindItemClickListener {
            override fun onClick(itemIdx: Int) {
                val bundle = Bundle()
                bundle.putInt("selection", itemIdx)
                findNavController().navigate(R.id.action_navigation_home_to_navigation_edit, bundle)
            }
        })
        binding.containerRemindItem.adapter = remindAdapter
    }

    private fun fetchRemindList() {
        homeViewModel.fetchReminds()
    }

    private fun setObservers() {
        homeViewModel.reminds.observe(viewLifecycleOwner, { reminds ->
            remindAdapter.submitList(reminds)
        })
    }

}