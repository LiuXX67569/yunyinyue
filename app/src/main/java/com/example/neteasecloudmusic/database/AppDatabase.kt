package com.example.neteasecloudmusic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.neteasecloudmusic.database.dao.UserDao
import com.example.neteasecloudmusic.ui.model.UserInfoBean

@Database(version = 1, entities = [UserInfoBean::class], exportSchema=false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database")
                .build()
                .apply { instance = this }
        }
    }
}