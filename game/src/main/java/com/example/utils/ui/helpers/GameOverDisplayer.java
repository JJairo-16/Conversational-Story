package com.example.utils.ui.helpers;

import com.example.models.game.GameFlag;
import com.example.models.game.GameState;
import com.example.utils.input.Menu;
import com.example.utils.ui.Cleaner;
import com.example.utils.ui.Prettier;

/**
 * Displays the final screen once a game session has ended.
 */
public final class GameOverDisplayer {
    /**
     * Prevents instantiation.
     */
    private GameOverDisplayer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Shows the ending that matches the final game state.
     *
     * @param cleaner console cleaner used before printing the ending
     * @param state   final game state
     */
    public static void show(Cleaner cleaner, GameState state) {
        cleaner.clear();

        FinalCase finalCase = getCase(state);
        Prettier.printTitle(finalCase.title());

        for (String message : state.consumeEventMessages()) {
            Prettier.warn(message);
        }

        System.out.println(finalCase.message());
        System.out.println();
        Menu.pause();
    }

    /**
     * Resolves the ending from victory and death flags.
     *
     * @param state final game state
     * @return matching final case
     */
    private static FinalCase getCase(GameState state) {
        if (state.isGameWon()) {
            return state.hasFlag(GameFlag.CREW_MEMBER_DIES)
                    ? FinalCase.WIN_CREW_DEAD
                    : FinalCase.WIN_ALL_ALIVE;
        }

        boolean playerIsAlive = !state.hasFlag(GameFlag.PLAYER_DIE);
        boolean crewMemberIsAlive = !state.hasFlag(GameFlag.CREW_MEMBER_DIES);

        if (playerIsAlive && crewMemberIsAlive)
            return FinalCase.UNRESOLVED;
        if (playerIsAlive)
            return FinalCase.ONLY_CREW_MEMBER_DIES;
        if (crewMemberIsAlive)
            return FinalCase.ONLY_PLAYER_DIES;
        return FinalCase.ALL_DIES;
    }

    /**
     * Possible endings based on mission completion and victims.
     */
    private enum FinalCase {
        /** The mission is completed and everyone survives. */
        WIN_ALL_ALIVE(
                "Missió completada: rumb a SUMMEM",
                """
                        Els motors responen amb un tremolor profund que recorre tota la PiaXXII.
                        Durant uns segons, els llums parpellegen, i després la nau recupera el
                        seu rumb com si mai hagués dubtat.

                        En Bond torna a la sala de comandament amb el tripulant encara viu i amb
                        iHall, per una vegada, sense cap comentari sarcàstic immediat. En Malien
                        queda enrere, perdut entre passadissos tancats i alarmes apagades.

                        La missió continua cap a SUMMEM.
                        """),

        /** The mission is completed, but the crew member has died. */
        WIN_CREW_DEAD(
                "Victòria amarga: motors en marxa",
                """
                        Els propulsors tornen a encendre's i la PiaXXII surt de la deriva. La
                        reparació ha funcionat, però el silenci que queda a la nau pesa més que
                        qualsevol avaria.

                        En Bond arriba a comandament sabent que el tripulant no ha sobreviscut a
                        en Malien. iHall confirma el rumb cap a SUMMEM amb una veu més baixa del
                        que és habitual.

                        La missió continua, però ja no ho fa intacta.
                        """),

        /** The player dies while the crew member survives. */
        ONLY_PLAYER_DIES(
                "Final tràgic: contacte amb Malien",
                """
                        En Malien apareix massa a prop i aquesta vegada no hi ha cap dònut, cap
                        porta, ni cap segon extra per improvisar. En Bond cau abans de poder
                        completar la reparació.

                        El tripulant encara respira en algun punt de la nau, però sense el capità
                        i sense els motors operatius, la PiaXXII queda atrapada en una ruta morta.
                        iHall registra el fracàs i talla les alarmes una a una.

                        La missió acaba en silenci.
                        """),

        /** The crew member dies, but the player has not completed the mission. */
        ONLY_CREW_MEMBER_DIES(
                "Tripulació perduda: una baixa confirmada",
                """
                        El crit del tripulant ressona pels passadissos abans que la nau torni al
                        seu brunzit mecànic habitual. En Malien desapareix tan ràpid com havia
                        arribat, deixant enrere una absència impossible d'ignorar.

                        En Bond continua viu, però la situació ha canviat. Ara no només ha de
                        reparar la nau: també ha de carregar amb el cost de no haver arribat a
                        temps.

                        La PiaXXII segueix en perill.
                        """),

        /** Both the player and crew member die. */
        ALL_DIES(
                "Missió fallida: nau a la deriva",
                """
                        Primer cau el tripulant. Després, en Bond. Els passadissos de la PiaXXII
                        queden buits, il·luminats només per avisos vermells i pantalles que ningú
                        no tornarà a consultar.

                        iHall intenta reiniciar protocols d'emergència, però no queda ningú per
                        respondre. En Malien es mou lliurement per una nau sense tripulació, mentre
                        el planeta SUMMEM queda cada cop més lluny.

                        La missió s'ha perdut definitivament.
                        """),

        /** The session ended without a victory or death condition. */
        UNRESOLVED(
                "Partida finalitzada: protocol interromput",
                """
                        La simulació s'atura abans que la PiaXXII arribi a un desenllaç clar. Els
                        motors continuen pendents de reparació, i els registres de la nau no
                        indiquen cap víctima confirmada.

                        En Bond queda en un punt incert de la missió, amb iHall encara esperant una
                        decisió útil i en Malien en algun lloc del mapa.

                        El final queda obert.
                        """);

        private final String title;
        private final String message;

        FinalCase(String title, String message) {
            this.title = title;
            this.message = message;
        }

        /**
         * Returns the final screen title.
         *
         * @return title
         */
        private String title() {
            return title;
        }

        /**
         * Returns the final narrative message.
         *
         * @return ending message
         */
        private String message() {
            return message;
        }
    }
}
