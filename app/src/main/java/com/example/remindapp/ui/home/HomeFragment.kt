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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.databinding.FragmentHomeBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.component.AlarmReceiver
import com.example.remindapp.util.convertDateToMillis
import com.example.remindapp.util.getCurrentDay
import com.example.remindapp.util.getCurrentTime
import com.example.remindapp.util.getTargetTime

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

    override fun onStart() {
        super.onStart()
        setAlarm()
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
        remindAdapter = RemindAdapter(
            object: RemindAdapter.RemindItemClickListener {
                override fun onClick(itemIdx: Int) {
                    val bundle = Bundle()
                    bundle.putInt("selection", itemIdx)
                    findNavController().navigate(R.id.action_navigation_home_to_navigation_edit, bundle)
                }
            },
            object: RemindAdapter.CheckBoxClickListener {
                override fun onClick(view: View, item: Remind) {
                    val tmpView = view as CheckBox
                    if (tmpView.isChecked) { setAlarm() } else { cancelAlarm() }
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
        })
    }

    private fun setAlarm() {
        val remindList = homeViewModel.reminds.value ?: return
        if (remindList.isEmpty()) return

        val (currentHour, currentMin) = getCurrentTime().split(":").map { it.toInt() }
        val filtered = remindList.filter { it.active }.filter { it.hour >= currentHour }.filter { it.minute > currentMin }
        var dayPlus = false
        val target = if (filtered.isNullOrEmpty()) {
            dayPlus = true
            remindList.first { it.active }
        } else {
            filtered.first()
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            requireContext(),
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val millisStr = "${getCurrentDay()} ${getTargetTime(target.hour, target.minute)}"
        val curMillis = convertDateToMillis(millisStr, dayPlus)

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
        setAlarm()
    }

    companion object {
        const val ALARM_REQUEST_CODE = 10001
    }

}