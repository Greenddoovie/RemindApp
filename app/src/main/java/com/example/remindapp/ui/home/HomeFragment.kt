package com.example.remindapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.databinding.FragmentHomeBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.ui.home.model.RemindItem
import com.example.remindapp.util.RemindManager
import kotlinx.coroutines.launch

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
        val repo =
            RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))

        homeViewModel =
            ViewModelProvider(
                this,
                HomeViewModel.HomeViewModelFactory(repo)
            ).get(HomeViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapters()
        fetchRemindList()
        setObserver()
        observeRemindItemChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        parentFragmentManager.clearFragmentResultListener(RESULT_KEY_CHECK_CHANGE)
    }

    private fun observeRemindItemChange() {
        parentFragmentManager.setFragmentResultListener(
            RESULT_KEY_CHECK_CHANGE,
            viewLifecycleOwner
        ) { _, result ->
            val idx = result.getInt(BUNDLE_KEY_IDX)
            lifecycleScope.launch {
                homeViewModel.getRemind(idx)?.let {
                    setPendingRemind(it)
                }
            }
        }
    }

    fun clickRemindButton() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_edit)
    }

    private fun setObserver() {
        homeViewModel.reminds.observe(viewLifecycleOwner, { remindList ->
            binding.remindItemList =
                remindList.map { remind -> RemindItem.from(remind, ::clickItem, ::clickCheckBox) }
            binding.executePendingBindings()
        })
    }

    private fun clickItem(remindItem: RemindItem) = findNavController().navigate(
        R.id.action_navigation_home_to_navigation_edit,
        bundleOf(SELECTION to remindItem.id)
    )

    private fun clickCheckBox(remindItem: RemindItem) {
        val adapterPosition = remindAdapter.currentList.indexOf(remindItem)
        val vh =
            binding.containerRemindItem.findViewHolderForAdapterPosition(adapterPosition) ?: return
        val checkBox = vh.itemView.findViewById<AppCompatCheckBox>(R.id.cb_active)

        val remind = RemindItem.to(remindItem)
        if (checkBox.isChecked) setPendingRemind(remind) else cancelRemind(remind)
        homeViewModel.update(remind, checkBox.isChecked)
    }

    private fun setAdapters() {
        remindAdapter = RemindAdapter()
        binding.containerRemindItem.adapter = remindAdapter
    }

    private fun fetchRemindList() = homeViewModel.fetchReminds()

    private fun cancelRemind(remind: Remind) {
        context?.let { it ->
            RemindManager.cancelRemind(it, remind)
        }
    }

    private fun setPendingRemind(remind: Remind) {
        context?.let { it ->
            RemindManager.setPendingRemind(it, remind)
        }
    }

    companion object {
        const val SELECTION = "selection"
        const val RESULT_KEY_CHECK_CHANGE = "NotificationChange"
        const val BUNDLE_KEY_IDX = "RemindIdx"
    }

}