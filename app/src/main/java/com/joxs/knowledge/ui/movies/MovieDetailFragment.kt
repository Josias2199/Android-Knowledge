package com.joxs.knowledge.ui.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.joxs.knowledge.R
import com.joxs.knowledge.data.remote.ApiConstants
import com.joxs.knowledge.databinding.FragmentMovieDetailBinding


class MovieDetailFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private val args: MovieDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(layoutInflater)

        setupUI()

        return binding.root
    }

    private fun setupUI(){
        with(binding){
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivBackdropPath.load(ApiConstants.IMAGE_API_PREFIX+args.movie.backdropPath)
            ivMovie.load(ApiConstants.IMAGE_API_PREFIX+args.movie.posterPath)
            txtTitle.text = args.movie.title
            txtDescription.text = args.movie.overview
        }
    }

}