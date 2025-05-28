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
import androidx.recyclerview.widget.RecyclerView.Adapter

class MainActivity : AppCompatActivity() {
    lateinit var addStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var updateStudentLauncher: ActivityResultLauncher<Intent>
    lateinit var students: MutableList<StudentModel>
    lateinit var adapter: StudentAdapter
    lateinit var intentAdd: Intent
    lateinit var intentUpdate: Intent
    lateinit var db: SQLiteDatabase
    val tableName = "tbStudent"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        students = mutableListOf<StudentModel>()
        db = SQLiteDatabase.openDatabase(filesDir.path +"/studentdb",null,SQLiteDatabase.CREATE_IF_NECESSARY)
        if(!isTableExists(db, tableName)) // neu chua ton tai bang
        {
            // tao bang
            createTable(tableName, db)
        }
        val cursor = db.rawQuery(
            "select *" +
                    " from $tableName ", null
        )

        while (cursor.moveToNext()){
            if(cursor.getInt(4) != 0)  // =0 la da xoa
            {
                students.add(StudentModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)))
            }
        }

        cursor.close()

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
                addDataNewStudent(studentName,studentMSSV,studentEmail,studentPhoneNumber)
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
                updateDataStudent(position,studentUpdateName,studentUpdateMSSV,studentUpdateEmail,studentUpdatePhoneNumber  )
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
                deleteDataStudent(students[info.position].mssv)
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


    fun createTable(table: String, db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("create table $table(" +
                    "name text ," +
                    "mssv text," +
                    "email text," +
                    "phone text," +
                    "status integer)")
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    fun addDataNewStudent(studentName: String?, studentMSSV: String?, studentEmail: String?, studentPhoneNumber: String?){
        db.beginTransaction()
        try {
            db.execSQL("insert into $tableName values(?,?,?,?,1)", arrayOf(studentName ?:"",studentMSSV ?: "",studentEmail ?:"",studentPhoneNumber ?: ""))
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteDataStudent(mssv: String?){
        db.beginTransaction()
        try {
            db.execSQL("update $tableName set status = 0 where mssv = ?", arrayOf(mssv))
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    fun updateDataStudent(position: Int?, name: String?, mssv: String?, email: String?, phone: String?){
        db.beginTransaction()
        try {
            db.execSQL("update $tableName set name = ?, mssv = ?, email = ?, phone = ? where mssv = ?", arrayOf(name,mssv,email,phone, students[position!!].mssv))
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    fun isTableExists(db: SQLiteDatabase?, tableName: String?): Boolean {
        if (db == null || !db.isOpen || tableName.isNullOrBlank()) return false
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
            arrayOf("table", tableName)
        )
        cursor.use {
            // Luôn có một hàng COUNT(*) => moveToFirst() sẽ trả về true
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        // Trong trường hợp lạ (cursor không có hàng) thì trả về false
        return false
    }


}