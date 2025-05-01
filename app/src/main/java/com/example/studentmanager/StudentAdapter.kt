package com.example.studentmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.studentmanager.R.id.textViewEmail
import com.example.studentmanager.R.id.textViewMSSV
import com.example.studentmanager.R.id.textViewName
import com.example.studentmanager.R.id.textViewNumberPhone

class StudentAdapter(val students: MutableList<StudentModel>) : BaseAdapter(){
    override fun getCount(): Int = students.size

    override fun getItem(position: Int): Any = students[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val itemView: View
        if(convertView == null){
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.student_item,parent,false)
            viewHolder = ViewHolder()
            viewHolder.name = itemView.findViewById<TextView>(textViewName)
            viewHolder.mssv = itemView.findViewById<TextView>(textViewMSSV)
            viewHolder.email = itemView.findViewById<TextView>(textViewEmail)
            viewHolder.phoneNumber = itemView.findViewById<TextView>(textViewNumberPhone)
            itemView.tag = viewHolder
        }
        else{
            viewHolder = convertView.tag as ViewHolder
            itemView = convertView

        }
        viewHolder.name.text = "${position}. ${students[position].name}"
        viewHolder.mssv.text = "MSSV: ${students[position].mssv}"
        viewHolder.phoneNumber.text = "Phone: ${students[position].phoneNumber}"
        viewHolder.email.text = "Email: ${students[position].email}"
        return itemView
    }

    class ViewHolder{
        lateinit var name: TextView
        lateinit var mssv: TextView
        lateinit var email: TextView
        lateinit var phoneNumber: TextView

    }

}