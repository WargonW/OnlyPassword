package com.wargon.onlypassword.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

const val NAME_MAX_LENGTH = 50
const val USERNAME_MAX_LENGTH = 50
const val PASSWORD_MAX_LENGTH = 50
const val NOTE_MAX_LENGTH = 100

@Serializable
@Parcelize
@Entity(tableName = "passwords")
data class Password (

    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,

    val name: String,

    @ColumnInfo(name = "user_name")
    val username: String,

    val password: String,

    val note: String?,
) : Parcelable