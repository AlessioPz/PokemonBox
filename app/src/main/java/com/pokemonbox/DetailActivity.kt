package com.pokemonbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.pokemonbox.databinding.ActivityDetailBinding
import com.pokemonbox.presentation.detail.DetailViewModel
import kotlin.math.max
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private var hasEndedEnterPostponed = false
    private var usesSharedElementTransition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRootWindowInsets()

        val transitionName = intent.getStringExtra(EXTRA_SHARED_TRANSITION_NAME)
        usesSharedElementTransition = !transitionName.isNullOrEmpty()
        if (usesSharedElementTransition) {
            ViewCompat.setTransitionName(binding.ivPokemon, transitionName)
            ActivityCompat.postponeEnterTransition(this)
        }

        binding.btnBack.setOnClickListener {
            ActivityCompat.finishAfterTransition(this)
        }
        observeState()

        val pokemonId = intent.getIntExtra(EXTRA_POKEMON_ID, -1)
        if (pokemonId > 0) {
            viewModel.load(pokemonId)
        } else {
            binding.tvError.isVisible = true
            endEnterTransitionIfPostponed()
        }
    }

    private fun setupRootWindowInsets() {
        val contentPadding = resources.getDimensionPixelSize(R.dimen.activity_detail_content_padding)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            v.setPadding(
                max(system.left, cutout.left) + contentPadding,
                max(system.top, cutout.top) + contentPadding,
                max(system.right, cutout.right) + contentPadding,
                max(system.bottom, cutout.bottom) + contentPadding
            )
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.tvError.isVisible = state.errorMessage != null
                val showContent = state.pokemon != null
                binding.contentGroup.isVisible = showContent
                if (state.errorMessage != null) {
                    endEnterTransitionIfPostponed()
                }

                state.pokemon?.let { pokemon ->
                    binding.tvName.text = pokemon.name
                    binding.tvId.text = getString(R.string.pokemon_id_label, pokemon.id)
                    binding.tvTypes.text = pokemon.types.joinToString(" • ")
                    binding.tvDescription.text = pokemon.description
                    val shared = usesSharedElementTransition
                    binding.ivPokemon.load(pokemon.imageUrl) {
                        crossfade(durationMillis = if (shared) 0 else 200)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_gallery)
                        if (shared) {
                            listener(
                                onError = { _, _ ->
                                    endEnterTransitionIfPostponed()
                                },
                                onSuccess = { _, _ ->
                                    endEnterTransitionIfPostponed()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun endEnterTransitionIfPostponed() {
        if (!usesSharedElementTransition || hasEndedEnterPostponed) return
        hasEndedEnterPostponed = true
        ActivityCompat.startPostponedEnterTransition(this)
    }

    companion object {
        private const val EXTRA_POKEMON_ID = "extra_pokemon_id"
        private const val EXTRA_SHARED_TRANSITION_NAME = "extra_shared_transition_name"

        fun newIntent(
            context: Context,
            pokemonId: Int,
            sharedElementTransitionName: String? = null
        ): Intent {
            return Intent(context, DetailActivity::class.java)
                .putExtra(EXTRA_POKEMON_ID, pokemonId)
                .apply {
                    if (!sharedElementTransitionName.isNullOrEmpty()) {
                        putExtra(EXTRA_SHARED_TRANSITION_NAME, sharedElementTransitionName)
                    }
                }
        }
    }
}
