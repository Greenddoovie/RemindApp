package com.example.remindapp.ui.edit

import androidx.lifecycle.*
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import com.example.remindapp.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(private val remindRepository: IRemindRepo) : ViewModel() {

    private var _alarmSaved = SingleLiveEvent<Boolean>()
    val alarmSaved get() = _alarmSaved

    private var _remind = MutableLiveData<Remind>()
    val remind: LiveData<Remind> get() = _remind

    private var _uri = MutableLiveData<String>()
    val uri: LiveData<String> get() = _uri

    fun saveAlarm(title: String, hour: Int, minute: Int) {
        val uri = uri.value!!
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tmpRemind = remind.value
                if (tmpRemind != null) {
                    remindRepository.update(title, hour, minute, uri, true, tmpRemind.id)
                } else {
                    val remind = Remind(title, hour, minute, uri, true)
                    remindRepository.insert(remind)
                }
            }
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
            _uri.value = tmpRemind.uri
        }
    }

    fun setUri(uri: String) {
        _uri.value = uri
    }

    class EditViewModelFactory(private val remindRepository: RemindRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditViewModel(remindRepository) as T
        }
    }
}