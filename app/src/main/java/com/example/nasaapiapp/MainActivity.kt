package com.example.nasaapiapp

import java.lang.Exception
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nasaapiapp.databinding.ActivityMainBinding
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

// Define data classes for JSON response from NASA API

// NasaResponse represents the top-level structure of the JSON response from the NASA API.
@Serializable
data class NasaResponse(val collection: Collection)

// Collection represents the "collection" object in the JSON response, which contains a list of items.
@Serializable
data class Collection(val items: List<Item>)

// Item represents an individual item in the "items" array of the JSON response. Each item contains a list of data objects and an optional list of links.
@Serializable
data class Item(val data: List<Data>, val links: List<Link>? = null)

// Data represents a "data" object within an item. It contains information about the item such as its title, description, date created, and NASA ID.
@Serializable
data class Data(
    val title: String,
    val description: String? = null,
    val date_created: String,
    val nasa_id: String
)

// Link represents a "link" object within an item. It contains a URL to a resource associated with the item, such as an image or video.
@Serializable
data class Link(val href: String)

// Define custom ArrayAdapter for displaying items in a ListView
class ItemAdapter(
    context: Context,
    private val items: List<Item>
) : ArrayAdapter<Item>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the item_layout.xml layout to create a view for this item
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)

        // Get the item at the specified position in the list of items
        val item = items[position]

        // Find the ImageView and TextView in the item view
        val imageView = view.findViewById<ImageView>(R.id.item_image)
        val titleView = view.findViewById<TextView>(R.id.item_title)

        // Set the title text of the TextView to display the title of the item
        titleView.text = item.data[0].title

        // Load image from URL using an image loading library such as Glide or Picasso
        // If the item has a link to an image, load the image into the ImageView using Glide
        item.links?.firstOrNull()?.href?.let { url ->
            Glide.with(context).load(url).into(imageView)
        }

        // Return the created view for this item
        return view
    }
}

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding

    private lateinit var data: NasaResponse

    internal var currentQuery = ""
    internal var displayedPage = 1
    private val maxPages = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch data with an initial query and page number.
        fetchData("a")

        binding.prevButton.setOnClickListener {
            if (displayedPage > 1) {
                // disabled until data from API is retrieved
                binding.prevButton.isEnabled = false
                binding.nextButton.isEnabled = false
                displayedPage -= 1
                fetchData(currentQuery)
            }
        }


        binding.nextButton.setOnClickListener {
            if (currentQuery.isNotBlank() && displayedPage < maxPages) {
                binding.prevButton.isEnabled = false
                binding.nextButton.isEnabled = false
                displayedPage += 1
                fetchData(currentQuery)
            }
        }

    }
    internal fun fetchData(query: String) { //for testing
//    private fun fetchData(query: String) {
        try {
            currentQuery = query

            // Build URL for NASA API request using query and displayedPage parameters.
            val url = "https://images-api.nasa.gov/search?q=$query&page=$displayedPage"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()
                        val json = Json { ignoreUnknownKeys = true }
                        data = json.decodeFromString(body!!)
                    } catch (e: Exception) {
                        println("Error: " + e.message)
                    } finally {
                        runOnUiThread {
                            // Update the data used by the ArrayAdapter on the UI thread after fetching data from API.
                            updatePage()

                            // Set up SearchView to allow user to search for new query or filter current results by title.
                            setupSearchBar()
                        }
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    println("API execute failed")
                }
            })
        } catch (e: Exception) {
            println("Error: " + e.message)
        }
    }

    internal fun updatePage() { // for testing
//    private fun updatePage() {
        // Pass all items returned by the NASA API to a new instance of the ItemAdapter and set it as adapter for userList ListView.
        val adapter = ItemAdapter(this, data.collection.items)
        binding.userList.adapter = adapter

        // Set text for pageText TextView to display current page number.
        binding.pageText.text = "Page $displayedPage"

        // Set onClickListener for userList items to start ItemPageActivity when an item is clicked.
        binding.userList.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as Item

            // Create Intent to start ItemPageActivity.
            val intent = Intent(this, ItemPageActivity::class.java)

            // Pass data from the selected item to the new Activity using intent extras.
            intent.putExtra("title", selectedItem.data[0].title)
            intent.putExtra("description", selectedItem.data[0].description)
            intent.putExtra("date_created", selectedItem.data[0].date_created)
            intent.putExtra("nasa_id", selectedItem.data[0].nasa_id)

            selectedItem.links?.firstOrNull()?.href?.let { url ->
                intent.putExtra("imageUrl", url)
            }
            startActivity(intent)
        }

        // Enable/disable prevButton and nextButton based on current page number and availability of more results from API.
        binding.prevButton.isEnabled = displayedPage > 1
        binding.nextButton.isEnabled =
            displayedPage < maxPages
    }

    // Set up SearchView to allow user to search for new query or filter current results by title.
    private fun setupSearchBar() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                updateData(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateData(newText ?: "")
                return false
            }
        })
    }

    private fun updateData(query: String) {
        displayedPage = 1
        fetchData(query)
        updatePage()
    }
}

