package com.example.remindapp.ui.edit

import androidx.lifecycle.*
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import com.example.remindapp.util.RemindManager
import com.example.remindapp.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(private val remindRepository: IRemindRepo) : ViewModel() {

    private val _alarmSaved = SingleLiveEvent<Boolean>()
    val alarmSaved get() = _alarmSaved

    private val _remind = MutableLiveData<Remind>()
    val remind: LiveData<Remind> get() = _remind

    private lateinit var uri: String

    fun saveAlarm(title: String, hour: Int, minute: Int) {
        val uri = uri
        viewModelScope.launch {
            val newRemind = withContext(Dispatchers.IO) {
                val tmpRemind = remind.value
                if (tmpRemind != null) {
                    remindRepository.update(title, hour, minute, uri, true, tmpRemind.id)
                    remindRepository.getRemind(tmpRemind.id)
                } else {
                    Remind(title, hour, minute, uri, true).also { remind ->
                        remindRepository.insert(remind)
                    }
                    remindRepository.getRemind(title,hour, minute, uri)
                }
            }
            _remind.value = newRemind
            _alarmSaved.value = true
        }

    }

    fun fetchRemind(idx: Int) {
        if (idx == -1) return
        viewModelScope.launch {
            val tmpRemind = withContext(Dispatchers.IO) {
                remindRepository.getRemind(idx)
            }
            _remind.value = tmpRemind
            uri = tmpRemind.uri
        }
    }

    fun setUri(uri: String) {
        this.uri = uri
    }

    class EditViewModelFactory(private val remindRepository: RemindRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditViewModel(remindRepository) as T
        }
    }
}