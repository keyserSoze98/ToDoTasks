package com.keysersoze.todotasks

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskFragment : Fragment(), TaskAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var db: SQLiteDatabase
    private lateinit var taskAdapter: TaskAdapter

    companion object {
        const val TABLE_NAME = "tasks"
        const val COLUMN_TASK = "task"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        recyclerView = view.findViewById(R.id.taskRecyclerView)
        addButton = view.findViewById(R.id.addButton)

        // Initialize database and RecyclerView
        val dbHelper = DatabaseHelper(requireContext())
        db = dbHelper.writableDatabase
        taskAdapter = TaskAdapter(loadTasks(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        addButton.setOnClickListener {
            showAddTaskDialog()
        }

        return view
    }

    @SuppressLint("Range")
    private fun loadTasks(): List<String> {
        val tasks = mutableListOf<String>()
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TASK),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK))
            tasks.add(task)
        }
        cursor.close()
        return tasks
    }

    private fun addTask(task: String) {
        val values = ContentValues()
        values.put(COLUMN_TASK, task)
        db.insert(TABLE_NAME, null, values)
    }

    private fun deleteTask(task: String) {
        val whereClause = "$COLUMN_TASK = ?"
        val whereArgs = arrayOf(task)
        db.delete(TABLE_NAME, whereClause, whereArgs)
    }

    override fun onItemClick(position: Int) {
        // Handle item click
        val task = taskAdapter.tasks[position]
        Toast.makeText(requireContext(), "Clicked: $task", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(position: Int) {
        // Handle edit click
        val task = taskAdapter.tasks[position]
        showEditDialog(task, position)
    }

    override fun onDeleteClick(position: Int) {
        // Handle delete click
        val task = taskAdapter.tasks[position]
        showDeleteConfirmationDialog(task, position)
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Task")
        val editText = EditText(requireContext())
        builder.setView(editText)
        builder.setPositiveButton("Add") { _, _ ->
            val task = editText.text.toString()
            addTask(task)
            taskAdapter.updateTasks(loadTasks())
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showEditDialog(task: String, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Task")
        val editText = EditText(requireContext())
        editText.setText(task)
        builder.setView(editText)
        builder.setPositiveButton("Save") { _, _ ->
            val newTask = editText.text.toString()
            updateTask(task, newTask)
            taskAdapter.updateTasks(loadTasks())
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showDeleteConfirmationDialog(task: String, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Task")
        builder.setMessage("Are you done with this task?")
        builder.setPositiveButton("Yes") { _, _ ->
            deleteTask(task)
            taskAdapter.updateTasks(loadTasks())
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun updateTask(oldTask: String, newTask: String) {
        val contentValues = ContentValues().apply {
            put(COLUMN_TASK, newTask)
        }
        val whereClause = "$COLUMN_TASK = ?"
        val whereArgs = arrayOf(oldTask)
        db.update(TABLE_NAME, contentValues, whereClause, whereArgs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        db.close()
    }
}


