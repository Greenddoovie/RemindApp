package com.example.remindapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.remindapp.R
import com.example.remindapp.databinding.ItemRemindMainFragmentBinding
import com.example.remindapp.model.room.Remind

class RemindAdapter(private val remindItemClickListener: RemindItemClickListener) : ListAdapter<Remind, RecyclerView.ViewHolder>(RemindItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RemindViewHolder(
            ItemRemindMainFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            remindItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RemindViewHolder -> { holder.bind(getItem(position)) }
        }
    }

    interface RemindItemClickListener {
        fun onClick(itemIdx: Int) {}
    }

    class RemindViewHolder(
        private val binding: ItemRemindMainFragmentBinding,
        private val clickListener: RemindItemClickListener
        ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var item: Remind
        init {
            binding.root.setOnClickListener{
                clickListener.onClick(item.id)
            }
        }

        fun bind(item: Remind) {
            this.item = item
            with(binding) {
                tvTime.text =
                    itemView.context.getString(R.string.main_fragment_item_time, item.hour, item.minute)
                tvTitle.text = item.title
                cbActive.isChecked = item.active
            }
        }
    }
}