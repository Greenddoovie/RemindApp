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
import com.example.remindapp.util.convertDateToMillis
import com.example.remindapp.util.getCurrentTime

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
        setAdapters()
        setObservers()
        fetchRemindList()
    }

    override fun onStart() {
        super.onStart()
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
                        it.putInt("selection", itemIdx)
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
        homeViewModel.reminds.observe(viewLifecycleOwner, { reminds ->
            remindAdapter.submitList(reminds)
            checkAlarmState()
        })
    }

    private fun checkAlarmState() {
        cancelAlarm()
        setAlarm()
    }

    private fun setAlarm() {
        val remindList = homeViewModel.reminds.value ?: return
        if (remindList.isEmpty()) return

        val (target, dayPlus) = findTarget(remindList)
        if (target == null) return

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pending = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("remindIdx", target.id)
        }.run {
            PendingIntent.getBroadcast(
                requireContext(),
                ALARM_REQUEST_CODE,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val curMillis = convertDateToMillis(target.hour, target.minute, dayPlus)
        if (curMillis == -1L) return

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, curMillis, pending)
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

    private fun filterActiveGreaterThanCurTime(reminds: List<Remind>): List<Remind> {
        val (currentHour, currentMin) = getCurrentTime()
        return reminds.filter { it.active }.filter { it.hour >= currentHour }.filter { it.minute > currentMin }
    }

    private fun findTarget(reminds: List<Remind>): Pair<Remind?, Boolean> {
        val filtered = filterActiveGreaterThanCurTime(reminds)
        return if (filtered.isNullOrEmpty()) {
            reminds.firstOrNull { it.active } to true
        } else {
            filtered.firstOrNull() to false
        }
    }

    companion object {
        const val ALARM_REQUEST_CODE = 10001
    }

}