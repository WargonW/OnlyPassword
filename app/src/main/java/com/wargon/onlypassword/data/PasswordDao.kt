package com.wargon.onlypassword.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(password: Password): Long

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)

    @Query("SELECT * from passwords WHERE id = :id")
    fun getPassword(id: Long): Flow<Password>

    @Query("SELECT * from passwords ORDER BY name ASC")
    fun getAllPasswords(): Flow<List<Password>>

    @Query("SELECT * from passwords where name = :name")
    fun getPasswordByName(name: String): Flow<Password>
}