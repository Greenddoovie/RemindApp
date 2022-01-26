package com.example.remindapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.remindapp.R
import com.example.remindapp.databinding.ItemRemindMainFragmentBinding
import com.example.remindapp.model.room.Remind

class RemindAdapter : ListAdapter<Remind, RecyclerView.ViewHolder>(RemindItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RemindViewHolder(
            ItemRemindMainFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RemindViewHolder -> { holder.bind(getItem(position)) }
        }
    }

    class RemindViewHolder(val binding: ItemRemindMainFragmentBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(item: Remind) {
            with(binding) {
                tvTime.text =
                    itemView.context.getString(R.string.main_fragment_item_time, item.hour, item.minute)
                tvTitle.text = item.title
                cbActive.isChecked = item.active
            }
        }
    }
}