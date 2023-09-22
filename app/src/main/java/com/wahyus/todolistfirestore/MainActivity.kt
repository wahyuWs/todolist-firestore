package com.wahyus.todolistfirestore

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wahyus.todolistfirestore.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), TodolistAdapter.ItemCallback, TodolistAdapter.ItemRemove {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todolistAdapter: TodolistAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: Dialog
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        todolistAdapter = TodolistAdapter(this, this)
        retrieveData()
        initDialog()

        binding.fabInsert.setOnClickListener {
            dialog.show()
            insertData()
        }

        binding.rvToDoList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todolistAdapter
        }
    }

    private fun retrieveData() {
        val notes = mutableListOf<Note>()
        db.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val item = data.toObject<Note>()
                    if(item != null) {
                        val note = Note(
                            data.id,
                            item.title,
                            item.description,
                            item.datetime
                        )
                        notes.add(note)
                    }
                }
                todolistAdapter.setData(notes)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun initDialog() {
        dialog = Dialog(this)
        view = layoutInflater.inflate(R.layout.insert_layout, null)
        dialog.apply {
            setContentView(view)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun showNote(data: Note) {
        val editextTitle = view.findViewById<EditText>(R.id.edt_title)
        val editextDescription = view.findViewById<EditText>(R.id.edt_description)
        val btnClose = view.findViewById<Button>(R.id.btn_close)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        btnSave.visibility = View.INVISIBLE
        btnClose.setOnClickListener {
            dialog.dismiss()
            editextTitle.text.clear()
            editextDescription.text.clear()
        }

        editextTitle.setText(data.title)
        editextDescription.setText(data.description)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertData() {
        val editextTitle = view.findViewById<EditText>(R.id.edt_title)
        val editextDescription = view.findViewById<EditText>(R.id.edt_description)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnClose = view.findViewById<Button>(R.id.btn_close)

        btnSave.visibility = View.VISIBLE
        btnSave.setOnClickListener {
            val title = editextTitle.text.toString()
            val description = editextDescription.text.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val current = LocalDateTime.now().format(formatter)

            val note = hashMapOf(
                "title" to title,
                "description" to description,
                "datetime" to current
            )

            db.collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully added notes id: ${it.id}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erorr ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun deleteNote(id: String) {
        db.collection("notes").document(id)
            .delete()
            .addOnSuccessListener { Toast.makeText(this, "deleted successfully", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
    }

    override fun onClick(note: Note) {
        showNote(note)
        dialog.show()
    }

    override fun onRemove(id: String) {
        deleteNote(id)
    }
}