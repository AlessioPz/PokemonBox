package com.pokemonbox

import android.content.Intent

/**
 * Constants shared by [MainActivity] and [DetailActivity].
 * Groups values used for layout, insets, bottom sheet, intents, and the detail screen’s faux-TCG logic.
 */
object ActivityConstants {

    /**
     * [MainActivity]: padding, margins, infinite scroll, bottom sheet.
     */
    object Main {

        /** Extra space below the list, in dp, in addition to the navigation bar inset. */
        const val PADDING_LIST_BOTTOM_DP: Int = 24

        /** Bottom margin of the progress bar from the safe area, in dp. */
        const val PROGRESS_MARGIN_DP: Int = 16

        /** Base margin for the FAB (bottom/end), in dp, combined with window insets. */
        const val FAB_BASE_MARGIN_DP: Int = 20

        /**
         * Infinite-scroll threshold: when at most this many items remain past the last visible
         * row, the next page is requested.
         */
        const val LOAD_MORE_REMAINING_ITEMS: Int = 4

        /** Dim amount behind the search bottom sheet (0f–1f). */
        const val BOTTOM_SHEET_DIM_AMOUNT: Float = 0.05f
    }

    /**
     * [DetailActivity]: intent extras, fallback copy, numbers for fake damage/retreat, transitions.
     */
    object Detail {

        /** [Intent] extra key: numeric Pokémon id to show. */
        const val EXTRA_POKEMON_ID: String = "extra_pokemon_id"

        /** [Intent] extra key: shared element transition name for the image (optional). */
        const val EXTRA_SHARED_TRANSITION_NAME: String = "extra_shared_transition_name"

        /** Default for reading the [EXTRA_POKEMON_ID] extra on an [Intent] when the id is missing or invalid. */
        const val INVALID_POKEMON_ID: Int = -1

        /**
         * Max character count for the flavor text line on the faux card
         * (English copy from PokeAPI; see repository).
         */
        const val FLAVOR_TEXT_PREVIEW_MAX: Int = 140

        /** Placeholder for missing values (height, weight, types, retreat, etc.). */
        const val PLACEHOLDER_TEXT: String = "--"

        /** Fallback HP for fake-damage math when the data model has no `hp` field. */
        const val FAKE_HP_DEFAULT: Int = 60

        /** Weight of the type count in the first fake-attack damage formula. */
        const val TYPE_WEIGHT_FOR_DAMAGE: Int = 7

        /** Modulo applied with the Pokémon id to vary the second fake-attack damage. */
        const val SECONDARY_DAMAGE_MOD: Int = 23

        /** Offset added after the modulo for the second fake-attack damage. */
        const val SECONDARY_DAMAGE_OFFSET: Int = 18

        /**
         * Divisor of the base stat (HP) in the raw fake-damage step
         * (video-game stats, not printed TCG).
         */
        const val FAKE_DAMAGE_BASE_DIVISOR: Int = 4

        /** Minimum (inclusive) clamp for fake damage before rounding. */
        const val FAKE_DAMAGE_MIN: Int = 30

        /** Maximum (inclusive) clamp for fake damage before rounding. */
        const val FAKE_DAMAGE_MAX: Int = 180

        /** Fake damage rounded to multiples of this integer. */
        const val FAKE_DAMAGE_STEP: Int = 10

        /** Coil crossfade duration when there is no shared element (ms). */
        const val CROSSFADE_MS: Int = 200

        /**
         * Crossfade when a shared element is active: 0 so
         * [androidx.core.app.ActivityCompat.startPostponedEnterTransition] is not delayed.
         */
        const val CROSSFADE_MS_SHARED: Int = 0

        /** Weight threshold (kg) for the first fake-retreat cost band. */
        const val RETREAT_TIER1_MAX_KG: Double = 10.0

        /** Weight threshold (kg) for the second fake-retreat cost band. */
        const val RETREAT_TIER2_MAX_KG: Double = 35.0

        /** Weight threshold (kg) for the third fake-retreat cost band. */
        const val RETREAT_TIER3_MAX_KG: Double = 80.0

        /** Number of retreat symbols when weight is below [RETREAT_TIER1_MAX_KG]. */
        const val RETREAT_COUNT_TIER1: Int = 1

        /** Number of symbols when weight is between band 1 and 2. */
        const val RETREAT_COUNT_TIER2: Int = 2

        /** Number of symbols when weight is between band 2 and 3. */
        const val RETREAT_COUNT_TIER3: Int = 3

        /** Number of symbols when weight is above [RETREAT_TIER3_MAX_KG]. */
        const val RETREAT_COUNT_TIER4: Int = 4

        /**
         * Character used to stand in for colored energy icons (retreat) without vector assets.
         * This does not represent a real printed TCG retreat cost.
         */
        const val RETREAT_SYMBOL: String = "○"

        /** Default name for the first fake attack when there are no abilities or types. */
        const val FAKE_ATTACK_PRIMARY_DEFAULT: String = "Tackle"

        /** Default name for the second fake attack when the second ability/type is missing. */
        const val FAKE_ATTACK_SECONDARY_DEFAULT: String = "Charge"

        /**
         * Shown in the second fake-attack blurb when the ability list is empty.
         */
        const val FAKE_ABILITIES_EMPTY_LABEL: String = "no abilities"
    }
}
