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
import com.pokemonbox.ActivityConstants.Detail
import com.pokemonbox.databinding.ActivityDetailBinding
import com.pokemonbox.domain.model.Pokemon
import com.pokemonbox.presentation.detail.DetailUiState
import com.pokemonbox.presentation.detail.DetailViewModel
import kotlin.math.max
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

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

        val transitionName = intent.getStringExtra(Detail.EXTRA_SHARED_TRANSITION_NAME)
        usesSharedElementTransition = !transitionName.isNullOrEmpty()
        if (usesSharedElementTransition) {
            ViewCompat.setTransitionName(binding.ivPokemon, transitionName)
            ActivityCompat.postponeEnterTransition(this)
        }

        binding.btnBack.setOnClickListener {
            ActivityCompat.finishAfterTransition(this)
        }
        observeState()

        val pokemonId = intent.getIntExtra(Detail.EXTRA_POKEMON_ID, Detail.INVALID_POKEMON_ID)
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
                applyHeaderState(state)
                state.pokemon?.let { bindPokemonDetail(it) }
            }
        }
    }

    private fun applyHeaderState(state: DetailUiState) {
        binding.progressBar.isVisible = state.isLoading
        binding.tvError.isVisible = state.errorMessage != null
        binding.contentGroup.isVisible = state.pokemon != null
        if (state.errorMessage != null) {
            endEnterTransitionIfPostponed()
        }
    }

    private fun bindPokemonDetail(pokemon: Pokemon) {
        binding.tvName.text = pokemon.name
        binding.tvHp.text = getString(R.string.pokemon_hp_label, pokemon.hp ?: 0)
        binding.tvPokemonMeta.text = getString(
            R.string.pokemon_meta_label,
            pokemon.id,
            pokemon.types.joinToString(" • ")
        )
        binding.tvHeightWeight.text = getString(
            R.string.pokemon_height_weight_label,
            pokemon.heightMeters?.let { formatDecimal(it) } ?: Detail.PLACEHOLDER_TEXT,
            pokemon.weightKg?.let { formatDecimal(it) } ?: Detail.PLACEHOLDER_TEXT
        )
        binding.tvDescription.text = pokemon.description
            .replace("\n", " ")
            .trim()
            .take(Detail.FLAVOR_TEXT_PREVIEW_MAX)
        bindFakeTcgAttacks(pokemon)
        binding.tvWeakness.text =
            pokemon.weaknesses.firstOrNull()?.let { "$it x2" } ?: Detail.PLACEHOLDER_TEXT
        binding.tvResistance.text =
            pokemon.resistances.firstOrNull()?.let { "$it -30" } ?: Detail.PLACEHOLDER_TEXT
        binding.tvRetreat.text = getString(
            R.string.pokemon_retreat_label,
            toRetreatSymbols(pokemon.weightKg)
        )
        val shared = usesSharedElementTransition
        binding.ivPokemon.load(pokemon.imageUrl) {
            crossfade(
                durationMillis = if (shared) Detail.CROSSFADE_MS_SHARED else Detail.CROSSFADE_MS
            )
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_gallery)
            if (shared) {
                listener(
                    onError = { _, _ -> endEnterTransitionIfPostponed() },
                    onSuccess = { _, _ -> endEnterTransitionIfPostponed() }
                )
            }
        }
    }

    private fun bindFakeTcgAttacks(pokemon: Pokemon) {
        val primaryAttackName = pokemon.abilities.firstOrNull()
            ?: pokemon.types.firstOrNull()
            ?: Detail.FAKE_ATTACK_PRIMARY_DEFAULT
        val secondaryAttackName = pokemon.abilities.getOrNull(1)
            ?: pokemon.types.getOrNull(1)
            ?: Detail.FAKE_ATTACK_SECONDARY_DEFAULT
        val baseHp = pokemon.hp ?: Detail.FAKE_HP_DEFAULT
        val primaryDamage = computeFakeDamage(
            base = baseHp,
            modifier = pokemon.types.size * Detail.TYPE_WEIGHT_FOR_DAMAGE
        )
        val secondaryDamage = computeFakeDamage(
            base = baseHp,
            modifier = pokemon.id % Detail.SECONDARY_DAMAGE_MOD + Detail.SECONDARY_DAMAGE_OFFSET
        )
        val attacks = binding.includeTcgAttacks
        attacks.tvAttackPrimaryName.text = primaryAttackName
        attacks.tvAttackPrimaryDamage.text = primaryDamage.toString()
        attacks.tvAttackPrimaryText.text = getString(
            R.string.pokemon_fake_attack_primary_text,
            pokemon.types.joinToString(" / ")
        )
        attacks.tvAttackSecondaryName.text = secondaryAttackName
        attacks.tvAttackSecondaryDamage.text = secondaryDamage.toString()
        attacks.tvAttackSecondaryText.text = getString(
            R.string.pokemon_fake_attack_secondary_text,
            pokemon.abilities.takeIf { it.isNotEmpty() }?.joinToString(", ")
                ?: Detail.FAKE_ABILITIES_EMPTY_LABEL
        )
    }

    private fun endEnterTransitionIfPostponed() {
        if (!usesSharedElementTransition || hasEndedEnterPostponed) return
        hasEndedEnterPostponed = true
        ActivityCompat.startPostponedEnterTransition(this)
    }

    private fun formatDecimal(value: Double): String {
        return String.format(Locale.US, "%.1f", value)
    }

    private fun computeFakeDamage(base: Int, modifier: Int): Int {
        val raw = (base / Detail.FAKE_DAMAGE_BASE_DIVISOR) + modifier
        return (raw.coerceIn(Detail.FAKE_DAMAGE_MIN, Detail.FAKE_DAMAGE_MAX) / Detail.FAKE_DAMAGE_STEP) *
            Detail.FAKE_DAMAGE_STEP
    }

    private fun toRetreatSymbols(weightKg: Double?): String {
        if (weightKg == null) {
            return Detail.PLACEHOLDER_TEXT
        }
        val count = when {
            weightKg < Detail.RETREAT_TIER1_MAX_KG -> Detail.RETREAT_COUNT_TIER1
            weightKg < Detail.RETREAT_TIER2_MAX_KG -> Detail.RETREAT_COUNT_TIER2
            weightKg < Detail.RETREAT_TIER3_MAX_KG -> Detail.RETREAT_COUNT_TIER3
            else -> Detail.RETREAT_COUNT_TIER4
        }
        return buildString {
            repeat(count) { append(Detail.RETREAT_SYMBOL) }
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            pokemonId: Int,
            sharedElementTransitionName: String? = null
        ): Intent {
            return Intent(context, DetailActivity::class.java)
                .putExtra(Detail.EXTRA_POKEMON_ID, pokemonId)
                .apply {
                    if (!sharedElementTransitionName.isNullOrEmpty()) {
                        putExtra(Detail.EXTRA_SHARED_TRANSITION_NAME, sharedElementTransitionName)
                    }
                }
        }
    }
}
