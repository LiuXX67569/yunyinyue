package com.example.neteasecloudmusic.ui.model

import java.io.Serializable

data class SingerBean (
    val pk_id:Int = 0,
    val singer_name: String? = null,
    val photo_path: String? = null,
    val level: Int = 0,
    val num_follow: Int = 0,
    val num_fans: Int = 0,
    val menu_list: String? = null,
    val introduction: String? = null
): Serializable