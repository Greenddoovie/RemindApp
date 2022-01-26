package com.example.remindapp.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.room.Remind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(private val remindRepository: IRemindRepo) : ViewModel() {

    private var _alarmSaved = SingleLiveEvent<Boolean>()
    val alarmSaved get() = _alarmSaved

    fun saveAlarm(title: String, uri: String, hour: Int, minute: Int) {
        val remind = Remind(title, hour, minute, uri, true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                remindRepository.insert(remind)
            }
            _alarmSaved.value = true
        }
    }

    //ToDo: 저장에 실패한 경우 처리 어떻게 할지 고민

}