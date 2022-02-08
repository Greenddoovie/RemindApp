package com.example.remindapp.ui.home.model

import androidx.recyclerview.widget.DiffUtil
import com.example.remindapp.model.room.Remind

data class RemindItem(
    val id: Int,
    val title: String,
    val hour: Int,
    val minute: Int,
    val uri: String,
    val active: Boolean,
    val onClick: (RemindItem) -> Unit,
    val onCheckBoxClick: (RemindItem) -> Unit
) {

    fun convertTime() =
        "${if (hour < 10) "0$hour" else "$hour"}:${if (minute < 10) "0$minute" else "$minute"} ${if (hour >= 12) "PM" else "AM"}"

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RemindItem>() {
            override fun areItemsTheSame(oldItem: RemindItem, newItem: RemindItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RemindItem, newItem: RemindItem): Boolean {
                return oldItem == newItem
            }
        }

        fun from(
            remind: Remind,
            onClick: (RemindItem) -> Unit,
            onCheckBoxClick: (RemindItem) -> Unit
        ): RemindItem {
            return RemindItem(
                id = remind.id,
                title = remind.title,
                hour = remind.hour,
                minute = remind.minute,
                uri = remind.uri,
                active = remind.active,
                onClick = onClick,
                onCheckBoxClick = onCheckBoxClick
            )
        }

        fun to(remindItem: RemindItem): Remind {
            return Remind(
                title = remindItem.title,
                hour = remindItem.hour,
                minute = remindItem.minute,
                uri = remindItem.uri,
                active = remindItem.active
            ).apply {
                id = remindItem.id
            }
        }
    }
}
