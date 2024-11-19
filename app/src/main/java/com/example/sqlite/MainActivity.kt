package com.example.sqlite

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.adapter.UserAdapter
import com.example.sqlite.database.DatabaseHelper

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var saveButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            // Initialize DatabaseHelper
            dbHelper = DatabaseHelper(this)

            // Initialize views
            initializeViews()

            // Setup RecyclerView
            setupRecyclerView()

            // Setup save button click listener
            setupSaveButton()

            // Load initial data
            loadUsers()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeViews() {
        try {
            nameInput = findViewById(R.id.nameInput)
            emailInput = findViewById(R.id.emailInput)
            saveButton = findViewById(R.id.saveButton)
            recyclerView = findViewById(R.id.recyclerView)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views: ${e.message}", e)
            throw e
        }
    }

    private fun setupRecyclerView() {
        try {
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = UserAdapter(ArrayList()) // Initialize with empty list
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
            throw e
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            try {
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val id = dbHelper.insertUser(name, email)
                if (id > 0) {
                    Toast.makeText(this, "User saved successfully!", Toast.LENGTH_SHORT).show()
                    clearInputs()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Error saving user", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user: ${e.message}", e)
                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUsers() {
        try {
            val users = dbHelper.getAllUsers()
            adapter.updateUsers(users)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading users: ${e.message}", e)
            Toast.makeText(this, "Error loading users: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        nameInput.text.clear()
        emailInput.text.clear()
    }

    override fun onDestroy() {
        try {
            dbHelper.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing database: ${e.message}", e)
        }
        super.onDestroy()
    }
}