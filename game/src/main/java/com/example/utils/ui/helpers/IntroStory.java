package com.example.utils.ui.helpers;

import java.util.List;

import com.example.utils.input.Menu;
import com.example.utils.ui.Cleaner;
import com.example.utils.ui.Prettier;

/**
 * Displays the initial story before the game starts.
 */
public final class IntroStory {
	private static final List<String> PARTS = List.of(
			"""
			Any 2120 D.C.

			La nau PiaXXII explora l’espai inhòspit en direcció al planeta SUMMEM
			on es creu que hi poden haver les condicions idònies per arrelar una
			nova vida, que ja no és possible dur a terme en el planeta Pia.

			Després d’un llarg període d’hivernació provocada pel llarg viatge al
			planeta SUMMEM, el cap de la tripulació de la PiaXXII es desperta del
			son induït. L’ordinador de la nau iHall ha detectat una anomalia en el
			sistema i necessita de la intervenció del cap de la tripulació per
			resoldre’l.
			""",
			"""
			- Que tal ha dormit capità Bond?. Em sap greu destorbar-lo però he
			  detectat una anomalia a la nau. Sembla ser que se’ns gira feina.

			- Coi de ferralla “intel·ligent”! – En Bond està altament irritat ja
			  que el somni no podia ser d’allò més excitant i realista... - espero
			  que hagi estat indispensable haver-me destorbat el són!. Sinó
			  preparat per que et programi unes quantes sessions de Treball
			  cooperatiu amb un pedagog inspirat....

			- Li garanteixo Capità Bond que no és una falsa alarma, no m’arriscaria
			  a patir un càstig similar...

			- Està bé iHall – murmurà el capità ja més despert i calmat – donem
			  l’informe de la situació i anem per feina!, que vull tornar a agafar
			  el son.
			""",
			"""
			iHall procedeix a explicar detalladament la situació al capità Resulta
			que hem xocat amb un petit aeròlit que ha provocat petits danys al
			sistema de propulsió de la nau que requereixen de la intervenció humana.

			La Nau respon al següent planell.

			Des dels dormitoris cal arribar a la zona de motors i propulsió. Les
			diferents zones estan unides per portes automàtiques que cal obrir amb
			la tarja personal de cada un dels tripulants però en Bond no recorda on
			l’ha deixat.
			""",
			"""
			Sense la tarja, depèn al 100% de la comunicació amb iHall perquè li
			vagi obrin les portes entre zones. Darrerament però iHall està un pel
			transposat i en moltes ocasions no fa cas a en Bond i el posa a prova
			donant-li pistes o indicacions falses.

			Potser pot intentar agafar alguna de les targes dels companys o intentar
			buscar-la per la nau.
			""",
			"""
			Per aconseguir reparar els motors, cal posar-se el vestit d’astronauta
			que està al vestuari. Sense ell no es podria sobreviure a l’exterior de
			la nau.

			Compte però que, revivint la famosa saga d’en Ridley Scott, tenim un
			Alien donant voltes per la nau. Se’l coneix amb el nom de “Malien”, i
			s’ha colat a la Nau per intentar sabotejar la missió.
			""",
			"""
			En “Malien” només té un punt dèbil, li agraden els dònuts!. Per tant,
			si ens trobem amb ell, potser serà l’única manera de poder-lo distreure
			perquè ens deixi tranquils.

			Necessitarem una eina especial per poder reparar els motors. L’eina
			està al taller de la nau.
			""",
			"""
			Un cop arreglem els propulsors del motor, haurem de córrer cap a la
			zona de la sala de comandaments per posar novament en marxa els motors.

			Podrà en Bond amb en “Malien”?. Podrà reparar els motors i aconseguir
			prosseguir la seva missió el Pia XXII cap a SUMMEM?.
			""");

	private final Cleaner cleaner;

	/**
	 * Creates a story displayer with its own console cleaner.
	 */
	public IntroStory() {
		this(new Cleaner());
	}

	/**
	 * Creates a story displayer with the provided console cleaner.
	 *
	 * @param cleaner cleaner used before each story part
	 */
	public IntroStory(Cleaner cleaner) {
		this.cleaner = cleaner;
	}

	/**
	 * Shows the story one part at a time.
	 */
	public void show() {
		for (int i = 0; i < PARTS.size(); i++) {
			cleaner.clear();
			Prettier.printTitle("Història (%d/%d)", i + 1, PARTS.size());
			System.out.println();
			System.out.println(PARTS.get(i));
			Menu.pause();
		}

		cleaner.clear();
	}
}