package com.example.remindapp.ui.edit

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.remindapp.R
import com.example.remindapp.databinding.FragmentEditBinding
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.SELECTION

class EditFragment : Fragment() {

    private lateinit var editViewModel: EditViewModel
    private var _binding: FragmentEditBinding? = null

    private val binding get() = _binding!!

    private var uri: Uri? = null
    private val ringtoneCallback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = result.data
                intent ?: return@ActivityResultCallback

                uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                editViewModel.setUri(uri.toString())
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repo =
            RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(requireContext().applicationContext)))
        editViewModel =
            ViewModelProvider(
                this,
                EditViewModel.EditViewModelFactory(repo)
            ).get(EditViewModel::class.java)
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editVM = editViewModel
        binding.editFragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        setTouchListener()
        setObservers()

        val idx = arguments?.get(SELECTION) as Int// -1 이면 추가
        fetchRemind(idx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchRemind(idx: Int) {
        editViewModel.fetchRemind(idx)
    }

    private fun setTouchListener() {
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    fun saveRemindButton() {
        with(binding) {
            if (etEditFragmentRemindTitle.text.toString() == "") {
                Toast.makeText(requireContext(), "Fill title", Toast.LENGTH_SHORT).show()
                return
            }
            val hour = tpEditFragmentRemindTimeSelection.hour
            val minute = tpEditFragmentRemindTimeSelection.minute
            val title = etEditFragmentRemindTitle.text.toString()
            val song = containerEditFragmentNotificationSong.tvEditFragmentNotificationSongTitle.text
            if (song == "") {
                Toast.makeText(requireContext(), "Select ringtone", Toast.LENGTH_SHORT).show()
                return
            }
            editViewModel.saveAlarm(title, hour, minute)
        }
    }

    fun selectRingtone() {
        val ringtoneIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        }
        ringtoneCallback.launch(ringtoneIntent)
    }

    private fun setObservers() {
        editViewModel.alarmSaved.observe(viewLifecycleOwner, { saved ->
            if (saved) {
                findNavController().popBackStack()
            }
        })
    }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun getTitleFromUri(uri: String?): String {
        if (uri == null) return ""
        val ringtone = RingtoneManager.getRingtone(requireContext(), uri.toUri())
        return ringtone.getTitle(requireContext())
    }

}