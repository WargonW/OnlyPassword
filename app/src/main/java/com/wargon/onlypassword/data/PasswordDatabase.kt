package com.wargon.onlypassword.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Password::class], version = 2, exportSchema = false)
abstract class PasswordDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

    companion object {

        @Volatile
        private var Instance : PasswordDatabase ?= null

        fun getDatabase(context: Context): PasswordDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PasswordDatabase::class.java, "password_database")
                    //todo: 完成后移除此项
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }

}