package com.example.remindapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.remindapp.R
import com.example.remindapp.databinding.ItemRemindMainFragmentBinding
import com.example.remindapp.model.room.Remind
import com.example.remindapp.ui.home.model.RemindItem

class RemindAdapter : ListAdapter<RemindItem, RecyclerView.ViewHolder>(RemindItem.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RemindViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_remind_main_fragment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RemindViewHolder -> {
                holder.binding.remindItem = getItem(position)
                holder.binding.executePendingBindings()
            }
        }
    }

    class RemindViewHolder(
        val binding: ItemRemindMainFragmentBinding
    ) : RecyclerView.ViewHolder(binding.root)

}