package com.joxs.knowledge.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.joxs.knowledge.data.local.entity.MovieEntity
import com.joxs.knowledge.data.network.Resource
import com.joxs.knowledge.data.remote.ApiConstants
import com.joxs.knowledge.databinding.FragmentMoviesBinding
import com.joxs.knowledge.presentation.MoviesViewModel
import com.joxs.knowledge.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoviesFragment : Fragment(), MoviesAdapter.OnMovieClickListener {

    private var _binding: FragmentMoviesBinding? = null
    private lateinit var moviesAdapter: MoviesAdapter
    private val viewModel: MoviesViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupUI()
        getPopularMovies()
        return root
    }

    private fun setupUI() {
        moviesAdapter = MoviesAdapter(this)
        binding.apply {
            rvPopularMovies.apply {
                adapter = moviesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    private fun getPopularMovies() {
        viewModel.popularMovies.observe(viewLifecycleOwner, { result ->
            moviesAdapter.submitList(result.data)

            binding.progressBar.isVisible = result is Resource.Loading && result.data.isNullOrEmpty()
            if(result is Resource.Error && result.data.isNullOrEmpty()){
                showToast(result.error!!.localizedMessage!!)
            }
            if(result is Resource.Success && !result.data.isNullOrEmpty()){
                binding.bpMovie.ivMovie.load(ApiConstants.IMAGE_API_PREFIX+result.data[10].posterPath)
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMovieClick(movie: MovieEntity) {
        val action = MoviesFragmentDirections.actionNavigationMoviesToMovieDetailFragment(movie)
        findNavController().navigate(action)
    }

}