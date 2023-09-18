package com.wahyus.todolistfirestore

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wahyus.todolistfirestore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: Dialog
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        initDialog()
        binding.fabInsert.setOnClickListener {
            dialog.show()
            insertData()
        }
    }

    private fun initDialog() {
        dialog = Dialog(this)
        view = layoutInflater.inflate(R.layout.insert_layout, null)
        dialog.apply {
            setContentView(view)
            setCancelable(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun insertData() {
        val editextTitle = view.findViewById<EditText>(R.id.edt_title)
        val editextDescription = view.findViewById<EditText>(R.id.edt_description)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        btnSave.setOnClickListener {
            val title = editextTitle.text.toString()
            val description = editextDescription.text.toString()
            val note = hashMapOf(
                "title" to title,
                "description" to description
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
    }
}