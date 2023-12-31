package com.example.neteasecloudmusic.ui.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class UserInfoBean (
    var pk_id: Int,
    var username: String?,
    @PrimaryKey(autoGenerate = false)
    var email: String,
    var portrait: String?,
    var level: Int,
    var introduction: String?,
    var num_follow: Int,
    var follow_list: String?,
    var num_fans: Int,
    var menu_list: String?,
    var friends: String?,
) {
    @Ignore
    constructor(): this(0, null, "", null, 0, null, 0, null, 0, null, null)
}