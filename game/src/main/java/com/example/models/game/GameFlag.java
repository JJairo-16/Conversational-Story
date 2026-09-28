package com.example.models.game;

/**
 * Progress markers for puzzle state that is not already represented by route,
 * inventory or entity state.
 */
public enum GameFlag {
    /** The flashlight has revealed the donut in the dark kitchen. */
    KITCHEN_DONUT_REVEALED,

    /** The flooded bathroom has been inspected with the snorkel. */
    FLOODED_DRAIN_REVEALED,

    /** Engines have been inspected. */
    ENGINES_INSPECTED,

    /** Engines have been repaired. */
    ENGINES_REPAIRED
}
