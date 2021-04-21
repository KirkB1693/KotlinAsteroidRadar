package com.example.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.asteroidradar.main.AsteroidListAdapter
import com.example.asteroidradar.main.AsteroidApiStatus
import com.squareup.picasso.Picasso
import retrofit2.http.Url
import java.net.URL

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(data)
}

@BindingAdapter("asteroidsApiStatus")
fun bindStatus(statusProgressBar: ProgressBar, status: AsteroidApiStatus?) {
    when (status) {
        AsteroidApiStatus.LOADING -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        AsteroidApiStatus.ERROR -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        AsteroidApiStatus.DONE -> {
            statusProgressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, pictureOfDay: PictureOfDay?) {
    pictureOfDay.let {
        if (pictureOfDay != null) {
            if (pictureOfDay.mediaType == "video") {
                val youtubeId  = extractYouTubeId(pictureOfDay.url)
                if (youtubeId != "") {
                    val url = "http://img.youtube.com/vi/$youtubeId/0.jpg"
                    val imgUri = url.toUri().buildUpon().scheme("http").build()
                    Picasso.with(imgView.context)
                        .load(imgUri)
                        .placeholder(R.drawable.placeholder_picture_of_day)
                        .error(R.drawable.placeholder_picture_of_day)
                        .into(imgView)
                } else {
                    Picasso.with(imgView.context).load(R.drawable.placeholder_picture_of_day).into(imgView)
                }
            } else {
                val imgUri = pictureOfDay.url.toUri().buildUpon().scheme("https").build()
                Picasso.with(imgView.context)
                    .load(imgUri)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.placeholder_picture_of_day)
                    .into(imgView)
            }
        } else {
            Picasso.with(imgView.context).load(R.drawable.placeholder_picture_of_day).into(imgView)
        }
    }
}

fun extractYouTubeId(url: String): String {
    val query = URL(url).query
    val params = query.split("&")
    var id = ""
    for (row in params) {
        val param1 = row.split("=")
        if (param1[0].equals("v")) {
            id = param1[1]
        }
    }
    return id
}


@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
