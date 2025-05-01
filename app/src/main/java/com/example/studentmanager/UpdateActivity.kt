package com.example.studentmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val oldName = intent.getStringExtra("oldName")
        val oldMSSV = intent.getStringExtra("oldMSSV")
        val oldEmail = intent.getStringExtra("oldEmail")
        val oldPhoneNumber = intent.getStringExtra("oldPhoneNumber")
        val position = intent.getIntExtra("position",0)

        val editName = findViewById<EditText>(R.id.editTextUpdateName)
        val editMSSV = findViewById<EditText>(R.id.editTextUpdateMSSV)
        val editEmail = findViewById<EditText>(R.id.editTextUpdateEmailAddress)
        val editPhoneNumber = findViewById<EditText>(R.id.editTextUpdatePhone)

        editName.setText(oldName)
        editMSSV.setText(oldMSSV)
        editEmail.setText(oldEmail)
        editPhoneNumber.setText(oldPhoneNumber)

        val btnUpdate = findViewById<Button>(R.id.buttonUpdate)
        val btnCancel2 = findViewById<Button>(R.id.buttonCancel2)

        btnUpdate.setOnClickListener{
            val resultIntent = Intent().apply {
                putExtra("position", position)
                putExtra("studentUpdateName", editName.text.toString())
                putExtra("studentUpdateMSSV", editMSSV.text.toString())
                putExtra("studentUpdateEmail", editEmail.text.toString())
                putExtra("studentUpdatePhoneNumber", editPhoneNumber.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        btnCancel2.setOnClickListener{
            finish()
        }


    }
}

