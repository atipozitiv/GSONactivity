package com.example.gsonactivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.net.HttpURLConnection
import java.net.URL

data class Photo(
    val id: Long,
    val owner: String = "",
    val secret: String = "",
    val server: Int,
    val farm: Int,
    val title: String = "",
    val ispublic: Int,
    val isfrieng: Int,
    val isfamily: Int
)

data class PhotoPage(
    val page:Int = 1,
    val pages: Int = 1,
    val perpage: Int = 1,
    val total: Int = 1,
    val photo: JsonArray
)

data class Wrapper(
    val photos: JsonObject,
    val stat: String = "ok"
)

class Adapter(private val context: Context,
              private val list: ArrayList<String>,
              private val cellClickListener: CellClickListener) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        var imageView: ImageView? = null
        init {
            imageView = itemView.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(list[position], context)
        }
        holder.imageView?.let { Glide.with(context).load(list[position]).into(it) }
    }
}

interface CellClickListener {
    fun onCellClickListener(text: String, context: Context) {
        val intent = Intent(context, PicViewer::class.java)
        intent.putExtra("picLink", text)
        startActivity(context, intent, Bundle.EMPTY)
    }
}

class MainActivity : AppCompatActivity(), CellClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        plant(Timber.DebugTree())
        val links = ArrayList<String>()
        val url = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1")
        Thread {
            val connection = url.openConnection() as HttpURLConnection
            try{
                val data = connection.inputStream.bufferedReader().readText()
                connection.disconnect()
                val wrapped: Wrapper = Gson().fromJson(data,Wrapper::class.java)
                val firstPage:PhotoPage = Gson().fromJson(wrapped.photos, PhotoPage::class.java)
                val photos =  Gson().fromJson(firstPage.photo, Array<Photo>::class.java).toList()
                for (i in photos.indices) {
                    val link: String = "https://farm${photos[i].farm}.staticflickr.com/${photos[i].server}/${photos[i].id}_${photos[i].secret}_z.jpg";
                    links.add(link);
                    if (i%5 == 0) Timber.d(links[i])
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            runOnUiThread()
            {
                val recyclerView: RecyclerView = findViewById(R.id.rView)
                recyclerView.layoutManager = GridLayoutManager(this,2)
                recyclerView.adapter = Adapter(this, links, this)
            }
        }.start()
    }
}