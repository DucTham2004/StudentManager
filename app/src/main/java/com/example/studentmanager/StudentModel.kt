package com.example.studentmanager

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "students")
data class StudentModel(var name: String,
                        @PrimaryKey
                        var mssv: String,
                        var email: String,
                        var phoneNumber: String
)
