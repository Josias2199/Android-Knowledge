package com.joxs.knowledge.ui.movies

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joxs.knowledge.data.local.entity.MovieEntity
import com.joxs.knowledge.data.remote.ApiConstants
import com.joxs.knowledge.databinding.ItemMovieBinding

class MoviesAdapter(
    private val itemClickListener: OnMovieClickListener
) :

    ListAdapter<MovieEntity, MoviesAdapter.MovieViewHolder>(MovieComparator()) {

    interface OnMovieClickListener {
        fun onMovieClick(movie: MovieEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        //val holder = MovieViewHolder(itemBinding)
        /*itemBinding.root.setOnClickListener {
            Log.d("Fire", "click")
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener

            itemClickListener.onMovieClick(this.getItem(position))
        }*/

        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
            holder.itemView.setOnClickListener { itemClickListener.onMovieClick(currentItem) }
        }
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(movieEntity: MovieEntity) {
                binding.imgMovie.load(ApiConstants.IMAGE_API_PREFIX+movieEntity.posterPath)
                binding.txtMovieTitle.text = movieEntity.title
            }
    }

    class MovieComparator : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity) =
            oldItem == newItem
    }
}