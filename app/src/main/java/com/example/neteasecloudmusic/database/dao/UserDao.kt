package com.example.neteasecloudmusic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.neteasecloudmusic.ui.model.UserInfoBean

@Dao
interface UserDao {
    @Insert
    fun add(user: UserInfoBean): Long

    @Update
    fun update(user: UserInfoBean): Int

    @Query("select * from UserInfoBean where email=:email")
    fun getUserInfo(email: String): UserInfoBean
}