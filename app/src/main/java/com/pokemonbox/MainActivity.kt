package com.pokemonbox

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.activity.enableEdgeToEdge
import com.pokemonbox.databinding.ActivityMainBinding
import com.pokemonbox.databinding.BottomSheetSearchBinding
import com.pokemonbox.ui.SharedTransition
import com.pokemonbox.presentation.MainViewModel
import com.pokemonbox.presentation.PokemonAdapter
import com.pokemonbox.presentation.PokemonTab
import kotlinx.coroutines.launch
import kotlin.math.max
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PADDING_LIST_BOTTOM_DP = 24
        private const val PROGRESS_MARGIN_DP = 16
        private const val FAB_BASE_MARGIN_DP = 20
        private const val LOAD_MORE_REMAINING_ITEMS = 4
        private const val BOTTOM_SHEET_DIM_AMOUNT = 0.05f
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val pokemonAdapter = PokemonAdapter(
        onFavoriteClick = { pokemon ->
            viewModel.onFavoriteClicked(pokemon.id)
        },
        onPokemonClick = { pokemon, sharedView ->
            val name = SharedTransition.PokemonImage.transitionNameFor(pokemon.id)
            val intent = DetailActivity.newIntent(
                this,
                pokemonId = pokemon.id,
                sharedElementTransitionName = name
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedView,
                name
            )
            startActivity(intent, options.toBundle())
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSystemBarInsets()
        setupRecycler()
        setupListeners()
        observeState()
    }

    private fun setupSystemBarInsets() {
        val d = resources.displayMetrics.density
        val paddingBottomListDp = (PADDING_LIST_BOTTOM_DP * d).toInt()
        val marginProgressDp = (PROGRESS_MARGIN_DP * d).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val topSafe = max(system.top, cutout.top)
            val fabMarginBase = (FAB_BASE_MARGIN_DP * d).toInt()

            binding.tvTitle.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = topSafe
            }

            binding.rvPokemon.updatePadding(
                bottom = system.bottom + paddingBottomListDp
            )

            binding.progressBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = system.bottom + marginProgressDp
            }

            binding.fabSearch.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = system.bottom + fabMarginBase
                marginEnd = fabMarginBase + system.right
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setupRecycler() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPokemon.apply {
            layoutManager = linearLayoutManager
            adapter = pokemonAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy <= 0) return

                    val totalItemCount = linearLayoutManager.itemCount
                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (lastVisibleItem >= totalItemCount - LOAD_MORE_REMAINING_ITEMS) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupListeners() {
        binding.fabSearch.setOnClickListener {
            openSearchBottomSheet()
        }

        binding.tvError.setOnClickListener {
            viewModel.retry()
        }

        binding.btnAll.setOnClickListener {
            viewModel.onTabSelected(PokemonTab.ALL)
        }
        binding.btnFavorites.setOnClickListener {
            viewModel.onTabSelected(PokemonTab.FAVORITES)
        }
    }

    private fun openSearchBottomSheet() {
        val sheetBinding = BottomSheetSearchBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.etSearch.setText(viewModel.uiState.value.query)
        sheetBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            }
        })

        dialog.setOnShowListener {
            dialog.window?.setDimAmount(BOTTOM_SHEET_DIM_AMOUNT)
            sheetBinding.etSearch.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(sheetBinding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }
        dialog.show()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                pokemonAdapter.submitList(state.pokemons)
                binding.progressBar.isVisible = state.isLoading
                binding.tvError.isVisible = state.errorMessage != null
                if (state.errorMessage != null) {
                    binding.tvError.text = getString(R.string.error_loading_pokemon)
                }

                val colorEmphasis = ContextCompat.getColor(this@MainActivity, R.color.text_primary)
                val colorMuted = ContextCompat.getColor(this@MainActivity, R.color.text_secondary)
                val bgSelected = ContextCompat.getColor(this@MainActivity, R.color.tab_selected_background)
                val bgUnselected = ContextCompat.getColor(this@MainActivity, R.color.white)
                if (state.selectedTab == PokemonTab.ALL) {
                    binding.btnAll.setTextColor(colorEmphasis)
                    binding.btnFavorites.setTextColor(colorMuted)
                    binding.btnAll.setTypeface(null, Typeface.BOLD)
                    binding.btnFavorites.setTypeface(null, Typeface.NORMAL)
                    binding.btnAll.backgroundTintList = ColorStateList.valueOf(bgSelected)
                    binding.btnFavorites.backgroundTintList = ColorStateList.valueOf(bgUnselected)
                } else {
                    binding.btnAll.setTextColor(colorMuted)
                    binding.btnFavorites.setTextColor(colorEmphasis)
                    binding.btnAll.setTypeface(null, Typeface.NORMAL)
                    binding.btnFavorites.setTypeface(null, Typeface.BOLD)
                    binding.btnAll.backgroundTintList = ColorStateList.valueOf(bgUnselected)
                    binding.btnFavorites.backgroundTintList = ColorStateList.valueOf(bgSelected)
                }

                val isEmpty = state.pokemons.isEmpty() && !state.isInitialLoading
                binding.rvPokemon.visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE
            }
        }
    }
}
