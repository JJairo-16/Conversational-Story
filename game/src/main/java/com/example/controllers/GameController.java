package com.example.controllers;

import com.example.utils.input.Getters;
import com.example.utils.ui.IntroStory;

/**
 * Coordinates the main game flow.
 */
public class GameController {
    private GameController() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final Getters getters = new Getters();

    /**
     * Starts games repeatedly until the user chooses not to play again.
     */
    public static void startGame() {
        boolean loop = true;

        while (loop) {
            initializeGame();
            loop = playAgain();
        }
    }

    private static void initializeGame() {
        new IntroStory().show();
        GameLoop gameLoop = new GameLoop();
        gameLoop.run();
    }

    private static boolean playAgain() {
        return getters.getBoolean("Vols jugar de nou? ", false, "Sí", "No");
    }
}
