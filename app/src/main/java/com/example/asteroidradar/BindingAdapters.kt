package com.example.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.asteroidradar.main.AsteroidListAdapter
import com.example.asteroidradar.main.AsteroidApiStatus
import com.squareup.picasso.Picasso
import java.io.File

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

@BindingAdapter("imagePath")
fun bindImage(imgView: ImageView, pictureOfDayDrawablePath: String?) {
    pictureOfDayDrawablePath.let {
        if (pictureOfDayDrawablePath != null) {
                Picasso.with(imgView.context)
                    .load(File(pictureOfDayDrawablePath))
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.placeholder_picture_of_day)
                    .into(imgView)
        } else {
            Picasso.with(imgView.context).load(R.drawable.placeholder_picture_of_day).into(imgView)
        }
    }
}


@BindingAdapter("imageContentDescription")
fun bindImageContentDescription(imgView: ImageView, pictureOfDay: PictureOfDay?) {
    val context = imgView.context
    pictureOfDay.let {
        if (pictureOfDay != null) {
            if (pictureOfDay.title.isEmpty()) {
                imgView.contentDescription =
                    context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
            } else {
                imgView.contentDescription =
                    context.getString(R.string.nasa_picture_of_day_content_description_format)
                        .format(pictureOfDay.title)
            }
        } else {
            imgView.contentDescription =
                context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
        }
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription = context.getString(R.string.icon_hazardous_content_description)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription = context.getString(R.string.icon_safe_content_description)
    }
}


@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
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
