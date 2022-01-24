package com.example.remindapp.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.example.remindapp.model.Remind

class RemindItemCallback : DiffUtil.ItemCallback<Remind>() {
    override fun areItemsTheSame(oldItem: Remind, newItem: Remind): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Remind, newItem: Remind): Boolean {
        return oldItem == newItem
    }
}