package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val nicknameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerTextView: TextView = findViewById(R.id.register_text)

        loginButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(nickname, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val username = user?.displayName
                            Log.d("LoginActivity", "Login successful, username: $username")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("LoginActivity", "Login failed: ${task.exception?.message}")
                            Toast.makeText(this, "Invalid nickname or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}