package com.example.studentmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface StudentDao {
    @Query("select * from students")
    suspend fun getAllStudents(): List<StudentModel>

    @Query("select * from students where mssv=:keyword")
    suspend fun getByMssv(keyword: String): List<StudentModel>

    @Insert
    suspend fun addStudent(student: StudentModel): Long

    @Update
    suspend fun updateStudent(student: StudentModel): Int

    @Delete
    suspend fun deleteStudent(student: StudentModel): Int

    @Query("delete from students where mssv=:keyword")
    suspend fun deleteByMssv(keyword: String): Int
}