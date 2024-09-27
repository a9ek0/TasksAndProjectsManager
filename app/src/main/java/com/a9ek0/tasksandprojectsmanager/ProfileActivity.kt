// File: java/com/a9ek0/tasksandprojectsmanager/ProfileActivity.kt
package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        setContentView(R.layout.activity_profile)

        val profileIcon: ImageView = findViewById(R.id.back_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}