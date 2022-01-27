package com.example.remindapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.remindapp.databinding.FragmentNotificationsBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase

class NotificationFragment : Fragment() {

    private lateinit var notificationViewModel: NotificationViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))
        notificationViewModel =
            ViewModelProvider(this, NotificationViewModel.NotificationViewModelFactory(repo)).get(NotificationViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.get("remindIdx")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}