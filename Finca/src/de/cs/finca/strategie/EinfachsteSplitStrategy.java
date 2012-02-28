package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class EinfachsteSplitStrategy extends Strategy {

	public EinfachsteSplitStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Diese Strategie ermittelt die beste Verteilung der monatliche Gesamtbelastung R1 + R2 auf beide Kredite. "
				+ System.lineSeparator()
				+ "Während der Laufzeit werden die Monatraten R1 und R2 NICHT mehr verändert. ";
	}

	@Override
	public String getShortDescription() {
		return this.getClass().getSimpleName()
				+ ": Optimale Rate, wenn die Raten nicht angepasst werden.";
	}

	/**
	 * Diese Strategie ermittelt die beste Monatratenaufteilung. Das Startdatum
	 * der Kredite wird NICHT berücksichtigt. Während der Laufzeit werden die
	 * Monatraten NICHT mehr verändert. Die Gesamtmonatsrate wird aus der Summe
	 * der beiden Einzelwerte ermittelt.
	 * 
	 * @throws Exception
	 */
	@Override
	public void run() throws Exception {

		double rate1 = getKreditDaten(1).getMinRate();
		double rate2 = getGesamtMonatsrate() - rate1;
		KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
		KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();

		// Da wir die Rate ja max. einmal ändern wollen, muss das von beiden
		// Kredit unterstützt werden
		int maxSegmente = Math.min(getKreditDaten(1).getDaten()
				.getMaxSegmente(), getKreditDaten(2).getDaten()
				.getMaxSegmente());

		boolean end = false;

		while (!end && rate1 >= getKreditDaten(1).getMinRate()
				&& rate2 >= getKreditDaten(2).getMinRate()) {
			if (maxSegmente >= 1) {
				// Ändern wir genau einmal den Kredit
				daten1.setMonatsrate(rate1);
				daten2.setMonatsrate(rate2);
			} else {
				// Wir dürfen den Kredit gar nicht ändern: alles nur einmal
				// durchlaufen
				end = true;
			}
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			kredit1.run();
			kredit2.run();

			double aktKosten = kredit1.getKosten() + kredit2.getKosten();
			double billigsteKosten = getKreditDaten(1).getBilligster()
					.getKosten()
					+ getKreditDaten(2).getBilligster().getKosten();
			if (aktKosten < billigsteKosten) {
				getKreditDaten(1).setBilligster(kredit1);
				getKreditDaten(2).setBilligster(kredit2);
			}

			rate1 += getStepSize();
			rate2 = getGesamtMonatsrate() - rate1;
		}

	}
}
