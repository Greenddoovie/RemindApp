package com.example.remindapp.ui.notifications

import androidx.lifecycle.*
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.room.Remind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(private val repo: IRemindRepo) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private var _remind = MutableLiveData<Remind>()
    val remind: LiveData<Remind> get() = _remind

    fun fetchRemind(id: Int) {
        viewModelScope.launch {
            val tmpRemind = withContext(Dispatchers.IO) {
                repo.getRemind(id)
            }
            _remind.value = tmpRemind
        }
    }

    fun update() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tmp = _remind.value ?: return@withContext
                with(tmp) {
                    repo.update(title, hour, minute, uri, false, id)
                }
            }
        }
    }

    class NotificationViewModelFactory(private val repo: IRemindRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(repo) as T
        }
    }

}