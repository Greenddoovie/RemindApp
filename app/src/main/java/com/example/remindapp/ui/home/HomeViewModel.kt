package com.example.remindapp.ui.home

import androidx.lifecycle.*
import com.example.remindapp.model.repository.IRemindRepo
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.Remind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val remindRepository: IRemindRepo) : ViewModel() {

    private val _reminds = MutableLiveData<List<Remind>>()
    val reminds: LiveData<List<Remind>> get() = _reminds

    private lateinit var job: Job

    fun fetchReminds() {
        job = viewModelScope.launch {
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
                    title = item.title,
                    hour = item.hour,
                    minute = item.minute,
                    uri = item.uri,
                    active = checked,
                    id = item.id
                )
            }
            fetchReminds()
        }
    }

    suspend fun getRemind(idx: Int): Remind? {
        return withContext(Dispatchers.IO) {
            job.join()
            reminds.value?.let { remindList ->
                remindList.find { it.id == idx }
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