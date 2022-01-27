package com.example.remindapp.ui.home

import androidx.lifecycle.*
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val remindRepository: IRemindRepo) : ViewModel() {

    private var _reminds = MutableLiveData<List<Remind>>()
    val reminds: LiveData<List<Remind>> get() = _reminds

    fun fetchReminds() {
        viewModelScope.launch {
            val remindList = withContext(Dispatchers.IO) {
                remindRepository.getAll()
            }
            _reminds.value = remindList.sortedWith(compareBy({ it.hour }, { it.minute }))
        }
    }

    fun update(item: Remind, checked: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                remindRepository.update(
                    item.title,
                    item.hour,
                    item.minute,
                    item.uri,
                    checked,
                    item.id
                )
            }
        }
    }

    class HomeViewModelFactory(private val remindRepository: RemindRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(remindRepository) as T
        }
    }

}