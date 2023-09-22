package com.wahyus.todolistfirestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wahyus.todolistfirestore.databinding.ItemToDoListBinding

class TodolistAdapter(private val itemCallback: ItemCallback, private val itemRemove: ItemRemove): RecyclerView.Adapter<TodolistAdapter.TodolistViewHolder>() {

    private val notes = ArrayList<Note>()

    fun setData(note: List<Note>) {
        notes.addAll(note)
        notifyDataSetChanged()
    }
    inner class TodolistViewHolder(private val itemBinding: ItemToDoListBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(note: Note) {
            itemBinding.apply {
                tvTitle.text = note.title
                tvDescripstion.text = note.description
                date.text = note.datetime
            }

            itemBinding.ibDelete.setOnClickListener {
                itemRemove.onRemove(note.id)
            }

            itemView.setOnClickListener {
                itemCallback.onClick(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistViewHolder {
        val itemTodoListBinding = ItemToDoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodolistViewHolder(itemTodoListBinding)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: TodolistViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    interface ItemCallback {
        fun onClick(note: Note)
    }

    interface ItemRemove {
        fun onRemove(id: String)
    }
}