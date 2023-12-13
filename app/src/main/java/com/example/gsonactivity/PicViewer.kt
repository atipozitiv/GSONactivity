package com.example.gsonactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class PicViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pic_viewer)

        val imView: ImageView = findViewById(R.id.imageView2)
        Glide.with(this).load(intent.getStringExtra("picLink")).into(imView)

        findViewById<Toolbar>(R.id.toolbar).apply {
            setSupportActionBar(this)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, "Добавленно в Избранное", LENGTH_LONG).show()
        return true
    }

}