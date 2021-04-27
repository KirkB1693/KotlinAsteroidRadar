package com.example.asteroidradar.main

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.asteroidradar.R
import com.example.asteroidradar.databinding.FragmentMainBinding
import com.example.asteroidradar.repository.PictureOfTheDayRepository

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter = AsteroidListAdapter(AsteroidListAdapter.OnClickListener {
        viewModel.displayAsteroidDetails(it)
    })

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        observeAsteroids()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun observeAsteroids() {
        viewModel.asteroidsList.observe(viewLifecycleOwner, { asteroids ->
        adapter.submitList(asteroids)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> {
                viewModel.showWeek()
                observeAsteroids()
                return true
            }
            R.id.show_today_menu -> {
                viewModel.showToday()
                observeAsteroids()
                return true
            }
            R.id.show_all_saved_menu -> {
                viewModel.showAllSaved()
                observeAsteroids()
                return true
            }
        }
        return true
    }
}
