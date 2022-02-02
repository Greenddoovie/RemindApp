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
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.component.AlarmService
import com.example.remindapp.databinding.FragmentNotificationsBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.SELECTED_REMIND_IDX

class NotificationFragment : Fragment() {

    private lateinit var callback: OnBackPressedCallback
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
        val repo =
            RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))
        notificationViewModel =
            ViewModelProvider(this, NotificationViewModel.NotificationViewModelFactory(repo)).get(
                NotificationViewModel::class.java
            )
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notiFragment = this
        binding.notiViewModel = notificationViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onStart() {
        super.onStart()
        bindAlarmService()
        val idx = arguments?.get(SELECTED_REMIND_IDX) as Int
        if (idx != -1) {
            notificationViewModel.fetchRemind(idx)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindAlarmService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dismissOnClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    fun dismissOnClick() {
        if (bound) {
            alarmService.stopService()
            notificationViewModel.updateActiveToFalse()
        }
        findNavController().popBackStack()
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