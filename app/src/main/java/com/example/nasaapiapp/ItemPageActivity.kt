package com.example.nasaapiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nasaapiapp.databinding.ActivityItemPageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ItemPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the data from the intent extras
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val id = intent.getStringExtra("nasa_id")
        val date = intent.getStringExtra("date_created")
        val imageUrl = intent.getStringExtra("imageUrl")

        // Format the date and time
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        val formattedDate = outputFormat.format(parsedDate)

        // Formatting the description
        val descID = "ID: $id\n\n$description"

        // Adding the bindings and adding the data to it
        binding.tvImageTitle.text = title
        binding.tvItemDescription.text = descID
        binding.tvItemDate.text = formattedDate

        // Set onClickListener for exit button
        binding.ivX.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        // Load image using Glide library
        Glide.with(this).load(imageUrl).into(binding.ivItemImageAip)
    }
}

