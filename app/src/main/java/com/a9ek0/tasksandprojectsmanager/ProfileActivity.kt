package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)

        val backIcon: ImageView = findViewById(R.id.back_icon)
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val name = user.displayName
            val email = user.email

            profileName.text = name ?: "No Name"
            profileEmail.text = email ?: "No Email"
        }
    }
}