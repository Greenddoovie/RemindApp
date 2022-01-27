package com.example.remindapp.ui.notifications

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.component.AlarmService
import com.example.remindapp.databinding.FragmentNotificationsBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase

class NotificationFragment : Fragment() {

    private lateinit var notificationViewModel: NotificationViewModel
    private var bound: Boolean = false
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmService: AlarmService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AlarmService.AlarmBinder
            alarmService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))
        notificationViewModel =
            ViewModelProvider(this, NotificationViewModel.NotificationViewModelFactory(repo)).get(NotificationViewModel::class.java)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        setClickListener()

        val id = arguments?.get("remindIdx") as Int
        if (id != -1) { fetchRemind(id) }
    }

    override fun onStart() {
        super.onStart()
        bindAlarmService()
    }

    override fun onStop() {
        super.onStop()
        unbindAlarmService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchRemind(id: Int) {
        notificationViewModel.fetchRemind(id)
    }

    private fun setObserver() {
        notificationViewModel.remind.observe(viewLifecycleOwner, { remind ->
            binding.tvNotiFragmentTitle.text = remind.title
            binding.tvNotiFragmentTime.text = getString(R.string.display_time, remind.hour, remind.minute)
        })
    }

    private fun setClickListener() {
        binding.btNotiFragmentDismiss.setOnClickListener {
            alarmService.stopService()
            notificationViewModel.update()
            findNavController().popBackStack()
        }
    }

    private fun bindAlarmService() {
        Intent(requireContext(), AlarmService::class.java).also {
            requireContext().bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindAlarmService() {
        requireContext().unbindService(connection)
        bound = false
    }
}