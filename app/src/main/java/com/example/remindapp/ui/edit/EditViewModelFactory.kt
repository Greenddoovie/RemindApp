package com.example.remindapp.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.remindapp.model.repository.RemindRepository

class EditViewModelFactory(val remindRepository: RemindRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditViewModel(remindRepository) as T
    }
}