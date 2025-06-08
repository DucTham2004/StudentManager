package com.example.studentmanager

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var updateStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var students: MutableList<StudentModel>
    lateinit var adapter: StudentAdapter
    lateinit var intentAdd: Intent
    lateinit var intentUpdate: Intent
    lateinit var studentDao: StudentDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentDao = StudentDatabase.getInstance(this).studentDao()
        students = mutableListOf()
        lifecycleScope.launch {
            // Lấy dữ liệu xong mới khởi tạo adapter để tránh empty list
            val list = studentDao.getAllStudents()
            students.addAll(list)
            // Quay về main thread để cập nhật UI
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }



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
                val newStudent = StudentModel(studentName ?: "",studentMSSV ?: "",studentEmail ?: "",studentPhoneNumber ?: "")
                lifecycleScope.launch {
                    studentDao.addStudent(newStudent)
                }
                students.add(newStudent)
                adapter.notifyDataSetChanged()
            }
        }

        updateStudentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_CANCELED){

            }
            else{
                val studentUpdateName = it.data?.getStringExtra("studentUpdateName")
                val studentUpdateEmail = it.data?.getStringExtra("studentUpdateEmail")
                val studentUpdatePhoneNumber = it.data?.getStringExtra("studentUpdatePhoneNumber")
                val position = it.data?.getIntExtra("position",0)

                students[position!!].name = studentUpdateName.toString()
                students[position].email = studentUpdateEmail.toString()
                students[position].phoneNumber = studentUpdatePhoneNumber.toString()
                // update database
                lifecycleScope.launch {
                    studentDao.updateStudent(students[position!!])
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }

                }





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
                lifecycleScope.launch {
                    studentDao.deleteStudent(students[info.position])
                }
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








