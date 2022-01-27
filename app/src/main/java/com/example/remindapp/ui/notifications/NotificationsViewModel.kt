package com.example.remindapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindapp.model.repository.IRemindRepo

class NotificationsViewModel(private val repo: IRemindRepo) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    class NotiViewModelFactory(private val repo: IRemindRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationsViewModel(repo) as T
        }

    }
}