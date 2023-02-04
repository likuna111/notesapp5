package com.example.todonotesapp.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

//data access object
@Dao
interface NotesDao{
    @Query( value = "SELECT * FROM notesData")
    fun getAll():List<Notes>

    @Insert(onConflict = REPLACE)
    fun insert(notes:Notes)

    @Update
    fun updateNotes(notes: Notes)

    @Delete
    fun delete(notes: Notes)

    @Query (value = "DELETE FROM notesData WHERE isTaskCompleted = :status")
    fun deleteNotes(status:Boolean)
}