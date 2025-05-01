package com.example.studentmanager

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_student)
        supportActionBar?.title = "Add Student"
        val inputName = findViewById<EditText>(R.id.editTextName)
        val inputMSSV = findViewById<EditText>(R.id.editTextMSSV)
        val inputPhoneNumber = findViewById<EditText>(R.id.editTextPhone)
        val inputEmail = findViewById<EditText>(R.id.editTextEmailAddress)
        val btnCreate = findViewById<Button>(R.id.buttonCreate)
        val btnCancel = findViewById<Button>(R.id.buttonCancel)

        btnCreate.setOnClickListener{
            intent.putExtra("studentName",inputName.text.toString())
            intent.putExtra("studentMSSV",inputMSSV.text.toString())
            intent.putExtra("studentEmail",inputEmail.text.toString())
            intent.putExtra("studentPhoneNumber",inputPhoneNumber.text.toString())
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        btnCancel.setOnClickListener{
            finish()
        }




    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu,menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return true
//    }


}