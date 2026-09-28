package com.example.models.generation;

import java.util.List;
import java.util.Random;

/**
 * Central random generator for reproducible games.
 *
 * <p>
 * Game code should depend on this wrapper instead of creating {@link Random}
 * directly.
 */
public final class GameRandom {
    private final long seed;
    private final Random random;

    /**
     * Creates a random generator from a seed.
     *
     * @param seed seed used to reproduce the game
     */
    public GameRandom(long seed) {
        this.seed = seed;
        random = new Random(seed);
    }

    /**
     * Returns a random integer in {@code [0, max)}.
     *
     * @param max exclusive upper bound
     * @return random integer
     */
    public int nextInt(int max) {
        return random.nextInt(max);
    }

    /**
     * Returns whether a percentage chance succeeds.
     *
     * @param percent chance from {@code 0} to {@code 100}
     * @return {@code true} when the chance succeeds
     */
    public boolean chance(int percent) {
        return random.nextInt(100) < percent;
    }

    /**
     * Returns a random element from an array.
     *
     * @param <T> element type
     * @param arr source array; must not be empty
     * @return randomly selected element
     * @throws IllegalArgumentException if {@code arr} is empty
     */
    public <T> T getRandomIndex(T[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Cannot select a random element from an empty array.");
        }

        if (arr.length == 1) {
            return arr[0];
        }

        return arr[random.nextInt(arr.length)];
    }

    /**
     * Returns a random element from a list.
     *
     * @param <T> element type
     * @param list source list; must not be empty
     * @return randomly selected element
     * @throws IllegalArgumentException if {@code list} is empty
     */
    public <T> T getRandomIndex(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot select a random element from an empty list.");
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns the generator seed.
     *
     * @return seed
     */
    public long getSeed() {
        return seed;
    }
}
