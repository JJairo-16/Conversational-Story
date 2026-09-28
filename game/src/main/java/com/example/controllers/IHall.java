package com.example.controllers;

import java.util.List;

import com.example.models.generation.GameRandom;
import com.example.models.generation.GameRoute;

/**
 * Session-local conversation script for iHall.
 *
 * <p>
 * iHall is intentionally not part of {@code GameState}: it is a conversational
 * loop helper. Each game route gets three possible iHall branches. A branch
 * starts with a short chain of unique responses that gradually lose patience
 * and then falls back to an irritated repeat pool once that chain is exhausted.
 */
final class IHall {
    private final GameRandom random;
    private final Branch branch;
    private int responseIndex;

    /**
     * Creates an iHall conversation helper for a selected branch.
     *
     * @param random session random source
     * @param branch selected conversation branch
     */
    private IHall(GameRandom random, Branch branch) {
        this.random = random;
        this.branch = branch;
    }

    /**
     * Creates iHall for the selected game route.
     *
     * @param route  generated game route
     * @param random session random source
     * @return configured iHall conversation helper
     */
    static IHall create(GameRoute route, GameRandom random) {
        IHallBranchType branchType = random.getRandomIndex(IHallBranchType.values());
        return new IHall(random, createBranch(route, branchType));
    }

    /**
     * Returns the next response in the selected branch.
     *
     * @return response text
     */
    String nextResponse() {
        List<String> uniqueResponses = branch.uniqueResponses();
        if (responseIndex < uniqueResponses.size()) {
            return uniqueResponses.get(responseIndex++);
        }

        List<String> fallbackPool = branch.fallbackPool();
        return random.getRandomIndex(fallbackPool);
    }

    /**
     * Creates the conversation branch matching the selected route and tone.
     *
     * @param route      generated game route
     * @param branchType selected iHall tone
     * @return configured conversation branch
     */
    private static Branch createBranch(GameRoute route, IHallBranchType branchType) {
        return switch (route) {
            case DRAIN_CARD -> createDrainCardBranch(branchType);
            case CREW_CARD -> createCrewCardBranch(branchType);
            case OFFICE_CARD -> createOfficeCardBranch(branchType);
        };
    }

    /**
     * Creates the hints used when the card is hidden in the flooded bathroom.
     *
     * @param branchType selected iHall tone
     * @return drain-card conversation branch
     */
    private static Branch createDrainCardBranch(IHallBranchType branchType) {
        return switch (branchType) {
            case GENERIC -> new Branch(
                    List.of(
                            "He detectat activitat anòmala prop dels banys. Hauries de comprovar les zones inundades.",
                            "La lectura d'humitat és massa alta per ignorar-la. Busca alguna manera de revisar sota l'aigua.",
                            "T'ho reformulo: banys inundats, visibilitat baixa i equip adequat per submergir-te.",
                            "La pista segueix sent banys i aigua. El meu marge de paciència comença a baixar.",
                            "Si encara preguntes, provaré amb síl·labes: banys, aigua, snorkel."),
                    genericPool());
            case USELESS -> new Branch(
                    List.of(
                            "Segons els meus càlculs, la tarja podria estar en un lloc humit. O sec. Fascinant.",
                            "La humitat continua sent una pista. Potser fins i tot una pista bona, accidentalment.",
                            "Hi ha aigua on no hauria d'haver-n'hi. Aquesta dada gairebé sembla útil.",
                            "Sorprenentment, repetir banys inundats no ha resolt res encara.",
                            "Estic a un pas de dibuixar-te una banyera amb una fletxa gegant."),
                    uselessPool());
            case HOSTILE -> new Branch(
                    List.of(
                            "La tarja és teva. La nau és meva. Endevina qui té més feina.",
                            "Hi ha registres anòmals als banys. Intenta convertir aquesta frase en una acció.",
                            "Prova als banys, si és que pots interpretar una pista tan subtil.",
                            "Necessitaràs mirar sota l'aigua. Ja sé que exigeix iniciativa.",
                            "Banys. Aigua. Snorkel. Si això falla, apago la metàfora."),
                    hostilePool());
        };
    }

    /**
     * Creates the hints used when the crew member can provide the card.
     *
     * @param branchType selected iHall tone
     * @return crew-card conversation branch
     */
    private static Branch createCrewCardBranch(IHallBranchType branchType) {
        return switch (branchType) {
            case GENERIC -> new Branch(
                    List.of(
                            "Un altre tripulant conserva una tarja funcional. Potser encara pots despertar-lo.",
                            "Els dormitoris continuen registrant signes vitals de la tripulació.",
                            "Si necessites una tarja, parlar amb qui encara en té una és una opció raonable.",
                            "Ves als dormitoris i intenta despertar el tripulant. La pista ja no és subtil.",
                            "Desperta el tripulant. És literalment el verb de la solució."),
                    genericPool());
            case USELESS -> new Branch(
                    List.of(
                            "Les persones despertes acostumen a respondre millor que les adormides.",
                            "Hi ha un tripulant que podria ajudar-te, sempre que no continuï dormint.",
                            "He consultat el manual: despertar algú requereix, tècnicament, despertar-lo.",
                            "La part complicada sembla ser que has de fer alguna cosa abans d'obtenir resposta.",
                            "El tripulant dorm. Tu preguntes. La ironia ja treballa sola."),
                    uselessPool());
            case HOSTILE -> new Branch(
                    List.of(
                            "Podries intentar parlar amb la tripulació. Si no et fa massa mandra.",
                            "Un tripulant viu és més útil despert que decorant un dormitori.",
                            "El tripulant dorm. Tu improvises. Cap dels dos inspira confiança.",
                            "Desperta'l i potser et donarà una tarja abans que jo perdi el filtre diplomàtic.",
                            "Desperta'l abans que jo decideixi simular una avaria de paciència."),
                    hostilePool());
        };
    }

    /**
     * Creates the hints used when the card is hidden in the office desk.
     *
     * @param branchType selected iHall tone
     * @return office-card conversation branch
     */
    private static Branch createOfficeCardBranch(IHallBranchType branchType) {
        return switch (branchType) {
            case GENERIC -> new Branch(
                    List.of(
                            "Els últims accessos registrats apunten a les oficines.",
                            "Els registres també indiquen activitat recent prop de l'escriptori central.",
                            "Si hi ha una tarja fora de lloc, l'escriptori central seria una bona hipòtesi.",
                            "Ves a oficines i revisa l'escriptori. La meva paciència encara aguanta, per ara.",
                            "Oficines. Escriptori. Obrir. Necessites que ho ordeni alfabèticament?"),
                    genericPool());
            case USELESS -> new Branch(
                    List.of(
                            "Les oficines contenen escriptoris. Els escriptoris contenen coses. De res.",
                            "Un escriptori és, estadísticament, un lloc on la gent deixa objectes.",
                            "He trobat set informes pendents i zero ganes de llegir-los per tu.",
                            "Podria dir 'oficina' amb més solemnitat, però no faria aparèixer la tarja.",
                            "Si veus un escriptori i no l'obres, potser el problema no és la pista."),
                    uselessPool());
            case HOSTILE -> new Branch(
                    List.of(
                            "Busca a oficines. I intenta no perdre també la dignitat.",
                            "Els registres apunten a l'escriptori central. Sí, això és una pista.",
                            "La pista és oficina. Si necessites un mapa paraula per paraula, malament.",
                            "Obre l'escriptori abans que decideixi classificar aquesta conversa com avaria humana.",
                            "Oficines. Escriptori. Tarja. Ja està, he baixat al nivell cartell lluminós."),
                    hostilePool());
        };
    }

    /**
     * Returns fallback lines for a mostly helpful iHall.
     *
     * @return repeatable generic response pool
     */
    private static List<String> genericPool() {
        return List.of(
                "Ja t'he donat prou pistes. Revisa la sala i fes alguna cosa útil.",
                "La meva paciència ha entrat en mode estalvi. Tarja, vestit, eina. Espavila.",
                "No tornaré a empaquetar la mateixa pista amb paper de regal.",
                "Consulta l'inventari, mira al teu voltant i deixa de convertir-me en megàfon.",
                "La resposta no canviarà perquè la preguntis amb més insistència.",
                "Encara puc ajudar-te, però prefereixo que abans provis la pista que ja tens.");
    }

    /**
     * Returns fallback lines for an evasive iHall.
     *
     * @return repeatable useless response pool
     */
    private static List<String> uselessPool() {
        return List.of(
                "Resposta inútil número següent: continua sent culpa teva.",
                "No tinc cap dada nova. Només menys paciència.",
                "Puc seguir parlant, però no prometo contribuir.",
                "He generat una pista alternativa: fes alguna cosa diferent.",
                "El silenci també seria una resposta, però sembla que vols soroll.",
                "La meva ajuda s'està degradant a comentari decoratiu.");
    }

    /**
     * Returns fallback lines for a hostile iHall.
     *
     * @return repeatable hostile response pool
     */
    private static List<String> hostilePool() {
        return List.of(
                "Resol-ho tu. Jo ja he fet massa per algú que no escolta.",
                "Et recordo que perdre objectes vitals no era part del protocol, geni.",
                "Quan acabis de passejar sense rumb, potser la missió encara existeix.",
                "La nau té problemes seriosos i, d'alguna manera, tu ets un d'ells.",
                "No confonguis la meva veu amb una guarderia interactiva.",
                "Torno a repetir la pista i torno a lamentar-ho immediatament.");
    }

    /**
     * Conversation data for one iHall route and tone combination.
     *
     * @param uniqueResponses ordered one-time responses
     * @param fallbackPool    repeatable responses used after the unique chain
     */
    private record Branch(List<String> uniqueResponses, List<String> fallbackPool) {
    }

    /**
     * Available iHall conversation tones.
     */
    private enum IHallBranchType {
        /** Helpful, direct hints. */
        GENERIC,

        /** Evasive hints that may still point toward the route. */
        USELESS,

        /** Irritated hints with a more confrontational voice. */
        HOSTILE
    }
}
