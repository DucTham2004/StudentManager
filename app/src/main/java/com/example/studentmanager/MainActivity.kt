package com.example.studentmanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView.Adapter

class MainActivity : AppCompatActivity() {
    lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var updateStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var students: MutableList<StudentModel>
    lateinit var adapter: StudentAdapter
    lateinit var intentAdd: Intent
    lateinit var intentUpdate: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        students = mutableListOf<StudentModel>()
        students.add(StudentModel("Nguyen Duc Tham", "20225395", "ductham2004@gmail.com", "0393036465"))
        students.add(StudentModel("Nguyen Van A", "20225495", "abc@gmail.com", "0393436465"))
        students.add(StudentModel("Nguyen Thi B", "20225355", "xvd@gmail.com", "0334325465"))

        val listViewStudent = findViewById<ListView>(R.id.list_student)
        adapter = StudentAdapter(students)
        listViewStudent.adapter = adapter
        registerForContextMenu(listViewStudent)

        addStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_CANCELED){

            }
            else{
                val studentName = it.data?.getStringExtra("studentName")
                val studentMSSV = it.data?.getStringExtra("studentMSSV")
                val studentEmail = it.data?.getStringExtra("studentEmail")
                val studentPhoneNumber = it.data?.getStringExtra("studentPhoneNumber")
                students.add(StudentModel(studentName.toString(),studentMSSV.toString(),studentEmail.toString(),studentPhoneNumber.toString()))
                adapter.notifyDataSetChanged()
            }
        }

        updateStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_CANCELED){

            }
            else{
                val studentUpdateName = it.data?.getStringExtra("studentUpdateName")
                val studentUpdateMSSV = it.data?.getStringExtra("studentUpdateMSSV")
                val studentUpdateEmail = it.data?.getStringExtra("studentUpdateEmail")
                val studentUpdatePhoneNumber = it.data?.getStringExtra("studentUpdatePhoneNumber")
                val position = it.data?.getIntExtra("position",0)
                students[position!!].name = studentUpdateName.toString()
                students[position].mssv = studentUpdateMSSV.toString()
                students[position].email = studentUpdateEmail.toString()
                students[position].phoneNumber = studentUpdatePhoneNumber.toString()
                adapter.notifyDataSetChanged()
            }
        }
        intentAdd = Intent(this, CreateStudentActivity::class.java)
        intentUpdate = Intent(this,UpdateActivity::class.java)





    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_item -> {
                addStudentLauncher.launch(intentAdd)
            }
        }
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu,menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when(item.itemId){
            R.id.itemUpdate ->{
                intentUpdate.putExtra("oldName",students[info.position].name)
                intentUpdate.putExtra("oldMSSV",students[info.position].mssv)
                intentUpdate.putExtra("oldEmail",students[info.position].email)
                intentUpdate.putExtra("oldPhoneNumber",students[info.position].phoneNumber)
                intentUpdate.putExtra("position",info.position)
                updateStudentLauncher.launch(intentUpdate)
            }
            R.id.itemDelete -> {
                students.removeAt(info.position)
                adapter.notifyDataSetChanged()

            }
            R.id.itemEmail -> {
                val intentEmail = Intent(Intent.ACTION_SEND)
                intentEmail.type = "text/plain"
                intentEmail.putExtra(Intent.EXTRA_EMAIL, students[info.position].email)
                startActivity(intentEmail)
            }

            R.id.itemCall -> {
               val intentCall = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${students[info.position].phoneNumber}"))
                startActivity(intentCall)
            }

        }

        return true
    }
}