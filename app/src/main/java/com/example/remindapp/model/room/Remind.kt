package com.example.remindapp.model.room

data class Remind(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    val active: Boolean
)

// 고려사항: time 저장 방법
