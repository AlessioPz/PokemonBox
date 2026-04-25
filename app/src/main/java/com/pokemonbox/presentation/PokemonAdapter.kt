package com.pokemonbox.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pokemonbox.databinding.ItemPokemonBinding
import com.pokemonbox.ui.SharedTransition
import com.pokemonbox.domain.model.Pokemon

class PokemonAdapter(
    private val onFavoriteClick: (Pokemon) -> Unit,
    private val onPokemonClick: (Pokemon, View) -> Unit
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding, onFavoriteClick, onPokemonClick)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PokemonViewHolder(
        private val binding: ItemPokemonBinding,
        private val onFavoriteClick: (Pokemon) -> Unit,
        private val onPokemonClick: (Pokemon, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pokemon) {
            ViewCompat.setTransitionName(
                binding.ivPokemon,
                SharedTransition.PokemonImage.transitionNameFor(item.id)
            )
            binding.ivPokemon.load(item.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_gallery)
            }

            binding.tvName.text = item.name
            binding.tvDescription.text = item.description.ifBlank { "-" }

            binding.tvType1.text = item.types.firstOrNull().orEmpty()
            val secondType = item.types.getOrNull(1)
            binding.tvType2.text = secondType.orEmpty()
            binding.tvType2.visibility = if (secondType.isNullOrBlank()) View.GONE else View.VISIBLE

            binding.btnFavorite.setFavoriteState(item.isFavorite)
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(item)
            }

            binding.root.setOnClickListener {
                onPokemonClick(item, binding.ivPokemon)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem == newItem
        }
    }
}

private fun ImageButton.setFavoriteState(isFavorite: Boolean) {
    val drawable = if (isFavorite) {
        android.R.drawable.btn_star_big_on
    } else {
        android.R.drawable.btn_star_big_off
    }
    setImageResource(drawable)
}
