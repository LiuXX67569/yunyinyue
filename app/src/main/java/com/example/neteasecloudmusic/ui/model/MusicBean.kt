package com.example.neteasecloudmusic.ui.model

import java.io.Serializable

data class MusicBean (
    val pk_id: Int = 0,
    val music_name: String? = null,
    val singer_id: Int = 0,
    val singer: String? = null,
    val album_id: Int = 0,
    val album: String? = null,
    val mp3_file_path: String? = null,
    val cover_image_path: String? = null,
    val lyrics_path: String? = null,
    val introduction: String? = null
): Serializable


