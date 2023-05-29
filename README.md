# NasaApiApp

<h2> Overview </h2>
This Android application connects to the images.nasa.gov API, a REST API hosted by NASA that provides access to the NASA Image and Video Library. This library contains a vast collection of images, videos, and audio files related to NASA’s missions and discoveries. The application allows users to search through this collection using a search bar and retrieve relevant data. Users can also select a search result to view more detailed information, including the title, date of creation, NASA ID, and description of the selected item.

<h2> App architecture </h2>
<h2>App Architecture</h2> The app consists of two primary activities: MainActivity and ItemPageActivity. In MainActivity, users can search for content from the NASA API using a SearchView. The results of the search are displayed in a ListView, which uses a custom ArrayAdapter called ItemAdapter to display each item. Each item in the list displays an image and title for a result from the NASA API. When a user selects an item from the list, ItemPageActivity is launched. This activity displays detailed information about the selected item, including its title, date of creation, NASA ID, and description. 

<h2>Libraries</h2> 
The following libraries were used in this app: 
<ul> 
  <li><b>Glide:</b> 
    An image loading and caching library that efficiently loads images from URLs into ImageViews. In this app, it is used to load images from the NASA API into the ImageViews in the ListView and ItemPageActivity. Glide was chosen for its simplicity and efficiency in loading and displaying images from external sources.
  </li> 
  <li><b>Kotlinx Serialization:</b> 
    A library for serializing and deserializing data. In this app, it is used to parse the JSON response from the NASA API into data classes, allowing for easy access and manipulation of the data. Kotlinx Serialization was chosen for its efficiency and ease of use in handling JSON data.
  </li> 
  <li><b>OkHttp:</b> 
    An efficient and easy-to-use HTTP client library for making network requests. In this app, it is used to fetch data from the NASA API. OkHttp was chosen for its simplicity and reliability in making network requests to external APIs.
  </li> 
</ul>

<h2>How to Build and Run This App</h2>
<p>To build and run this Android app, follow these steps:</p>
<ol>
  <li>Open the project in Android Studio.</li>
  <li>Connect an Android device to your computer via USB or start an emulator from the AVD Manager in Android Studio.</li>
  <li>Click on the "Run" button (represented by a green triangle) in the toolbar to build and install the app on the connected device or emulator.</li>
</ol>
<p>After following these steps, the app should be running on your device or emulator and you can interact with it to test its functionality.</p>


<h2>Information</h2>
This app used the image.nasa.gov API (https://images-api.nasa.gov/). For more information to the API, you can reach the document here: https://images.nasa.gov/docs/images.nasa.gov_api_docs.pdf
<br/>
The GitHub link to this project is here: https://github.com/1-Dont-Know/NasaApiApp

