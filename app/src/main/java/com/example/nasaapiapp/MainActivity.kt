package com.example.nasaapiapp

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
import java.lang.Exception
import kotlin.math.min

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
data class Data(val title: String, val description: String, val date_created: String, val nasa_id: String)

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

    private lateinit var binding: ActivityMainBinding

    private lateinit var data: NasaResponse

    private var currentPage = 1
    private var currentQuery = ""
    //only 15 results will show per page with a max of 100 pages
    private val resultsPerPage = 15
    private val maxPages = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch data with an initial query
        fetchData("a")

        // Set onClickListeners for prevButton and nextButton to navigate between pages of results
        binding.prevButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage -= 1
                updatePage()
            }
        }

        binding.nextButton.setOnClickListener {
            if (currentPage < maxPages) {
                currentPage += 1
                updatePage()
            }
        }
    }

    // Fetch data from NASA API using OkHttp library and update UI with results on success or print error message on failure.
    private fun fetchData(query: String) {
        try {
            currentQuery = query

            // Build URL for NASA API request using query parameter.
            val url = "https://images-api.nasa.gov/search?q=$query"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()
                        val json = Json { ignoreUnknownKeys = true }
                        data = json.decodeFromString(body!!)
                        runOnUiThread {
                            // Update the data used by the ArrayAdapter on the UI thread after fetching data from API.
                            updatePage()

                            // Set up SearchView to allow user to search for new query or filter current results by title.
                            setupSearchBar()
                        }
                    } catch (e: Exception) {
                        println("Error: " + e.message)
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


    // Update UI with current page of results and enable/disable prevButton and nextButton as needed.
    private fun updatePage() {
        val start = (currentPage - 1) * resultsPerPage
        // This makes sure that the end index does not pass the last item in the list of items returned by the NASA API.
        val end = min(start + resultsPerPage, data.collection.items.size)
        if (start <= end) {
            // Create a new list with the updated data for current page of results.
            val newItems = data.collection.items.subList(start, end)
            // Pass the new list to a new instance of the ItemAdapter and set it as adapter for userList ListView.
            val adapter = ItemAdapter(this, newItems)
            binding.userList.adapter = adapter

            // Set text for pageText TextView to display current page number.
            binding.pageText.text = "Page $currentPage"

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
        }

        // Enable/disable prevButton and nextButton based on current page number and availability of more results from API.
        binding.prevButton.isEnabled = currentPage > 1
        binding.nextButton.isEnabled = currentPage < maxPages && end < data.collection.items.size
    }

    // Filter current page of results by title using user's query and update UI with filtered results.
    private fun filterItems(query: String) {
        val start = (currentPage - 1) * resultsPerPage
        val end = min(start + resultsPerPage, data.collection.items.size)
        val filteredItems =
            data.collection.items.subList(start, end).filter { item -> item.data[0].title.contains(query, ignoreCase = true) }
        val adapter = ItemAdapter(this@MainActivity, filteredItems)
        binding.userList.adapter = adapter

        // Disable nextButton if there are no more filtered results available on current page.
        binding.nextButton.isEnabled =
            currentPage < maxPages && end < data.collection.items.size && filteredItems.size >= resultsPerPage

    }

    // Set up SearchView to allow user to search for new query or filter current results by title.
    private fun setupSearchBar() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()

                // Fetch data with the user's query and reset the page number when user submits a new query.
                currentPage = 1
                fetchData(query ?: "")
                updatePage()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Fetch data with the user's query and reset the page number when user changes a new query.
                currentPage = 1

                // Fetch data with the user's query when user types any characters in SearchView.
                fetchData(newText ?: "")

                // Filter items and update adapter after data has been fetched.
                filterItems(newText ?: "")
                return false
            }

        })
    }
}

