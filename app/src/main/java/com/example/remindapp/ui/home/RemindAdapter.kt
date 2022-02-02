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

class RemindAdapter(
    private val remindItemClickListener: RemindItemClickListener,
    private val checkboxListener: CheckBoxClickListener
) : ListAdapter<Remind, RecyclerView.ViewHolder>(RemindItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RemindViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_remind_main_fragment,
                parent,
                false
            ),
            remindItemClickListener,
            checkboxListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RemindViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    interface RemindItemClickListener {
        fun onClick(itemIdx: Int) {}
    }

    interface CheckBoxClickListener {
        fun onClick(view: View, item: Remind) {}
    }

    class RemindViewHolder(
        private val binding: ItemRemindMainFragmentBinding,
        private val clickListener: RemindItemClickListener,
        private val checkboxListener: CheckBoxClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var item: Remind

        init {
            binding.root.setOnClickListener {
                clickListener.onClick(item.id)
            }
            binding.cbActive.setOnClickListener {
                checkboxListener.onClick(binding.cbActive, item)
            }
        }

        fun bind(item: Remind) {
            binding.remind = item
            binding.executePendingBindings()
            this.item = item
        }
    }
}