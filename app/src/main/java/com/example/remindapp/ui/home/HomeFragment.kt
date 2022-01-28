package com.example.remindapp.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.component.AlarmReceiver
import com.example.remindapp.databinding.FragmentHomeBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.*

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = homeViewModel
        setAdapters()
        setObservers()

    }

    override fun onStart() {
        super.onStart()
        fetchRemindList()
        checkAlarmState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun clickRemindButton() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_edit)
    }

    private fun setAdapters() {
        remindAdapter = RemindAdapter(
            object: RemindAdapter.RemindItemClickListener {
                override fun onClick(itemIdx: Int) {
                    Bundle().let {
                        it.putInt(SELECTION, itemIdx)
                        findNavController().navigate(R.id.action_navigation_home_to_navigation_edit, it)
                    }
                }
            },
            object: RemindAdapter.CheckBoxClickListener {
                override fun onClick(view: View, item: Remind) {
                    val tmpView = view as CheckBox
                    checkAlarmState()
                    homeViewModel.update(item, tmpView.isChecked)
                }
            }
        )
        binding.containerRemindItem.adapter = remindAdapter
    }

    private fun fetchRemindList() {
        homeViewModel.fetchReminds()
    }

    private fun setObservers() {
        homeViewModel.reminds.observe(viewLifecycleOwner, { _ ->
            checkAlarmState()
        })
    }

    private fun checkAlarmState() {
        cancelAlarm()
        homeViewModel.reminds.value?.let {
            setAlarm(it, requireContext())
        }
    }

    private fun cancelAlarm() {
        val pending = PendingIntent.getBroadcast(
            requireContext(),
            ALARM_REQUEST_CODE,
            Intent(requireContext(), AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.cancel()
    }

    companion object {
        const val ALARM_REQUEST_CODE = 10001
    }

}