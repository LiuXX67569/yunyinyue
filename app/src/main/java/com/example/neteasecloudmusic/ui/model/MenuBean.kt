package com.example.neteasecloudmusic.ui.model

import java.io.Serializable

data class MenuBean (
    val pk_id: Int = 0,
    var cover_image_path: String? = null,
    var menu_name: String? = null,
    var uid: Int = 0,
    var username: String? = null,
    var menu_intro: String? = null,
    var musicIdList: String? = null,
    var musicNum: Int = 0
): Serializable