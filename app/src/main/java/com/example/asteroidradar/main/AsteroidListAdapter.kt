package com.example.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.asteroidradar.Asteroid
import com.example.asteroidradar.R
import com.example.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidListAdapter(val onClickListener: OnClickListener) : ListAdapter<Asteroid, AsteroidListAdapter.AsteroidViewHolder>(DiffCallback){


    class AsteroidViewHolder(private var binding: AsteroidListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroidItemDateText.text = asteroid.closeApproachDate
            binding.asteroidItemNameText.text = asteroid.codename
            if (asteroid.isPotentiallyHazardous) {
                binding.asteroidStatusImage.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                binding.asteroidStatusImage.setImageResource(R.drawable.ic_status_normal)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(AsteroidListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }
}