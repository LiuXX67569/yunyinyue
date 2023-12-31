package com.example.neteasecloudmusic.utils

import android.content.Context
import com.example.neteasecloudmusic.database.AppDatabase
import com.example.neteasecloudmusic.database.dao.UserDao
import com.example.neteasecloudmusic.ui.model.UserInfoBean

class DatabaseUtil(context: Context) {
    private var userDao: UserDao
    init {
        userDao = AppDatabase.getDatabase(context).userDao()
    }

    fun getUserInfo(email: String): UserInfoBean {
        return userDao.getUserInfo(email)
    }

    fun add(user: UserInfoBean) {
        userDao.add(user)
    }

    fun update(user: UserInfoBean): Int {
        return userDao.update(user)
    }
}