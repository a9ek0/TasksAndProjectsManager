package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val passwordEditText: EditText = findViewById(R.id.password)
        val nicknameEditText: EditText = findViewById(R.id.nickname)
        val addressEditText: EditText = findViewById(R.id.address)
        val registerButton: Button = findViewById(R.id.register_button)

        registerButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            val nickname = nicknameEditText.text.toString()
            val address = addressEditText.text.toString()

            if (password.isNotEmpty() && nickname.isNotEmpty() && address.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(address, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(nickname)
                                .build()

                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        val userData = hashMapOf(
                                            "nickname" to nickname,
                                            "address" to address,
                                            "email" to address
                                        )
                                        db.collection("users").document(user.uid).set(userData)
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Profile update failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}