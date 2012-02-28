package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class KonstanteRateStrategy extends Strategy {

	private final int absenkungAufProzent;

	/**
	 * 
	 * @param daten1
	 * @param daten2
	 * @param absenkungAufProzent
	 *            Monatsrate auf diesen Prozentwert absenken, wenn erster Kredit
	 *            ausgelaufen ist
	 * @throws Exception
	 */
	public KonstanteRateStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2, int absenkungAufProzent) throws Exception {
		super(daten1, daten2);

		if (absenkungAufProzent > 100 || absenkungAufProzent <= 0)
			throw new IllegalArgumentException();
		this.absenkungAufProzent = absenkungAufProzent;

	}

	@Override
	public String getDescription() {
		return "Mit Auslauf von Kredit 1 wird die Rate R2 von Kredit 2 auf "
				+ absenkungAufProzent + " % " + System.lineSeparator()
				+ "der initialen Gesamtrate abgesenkt.";
	}

	@Override
	public String getShortDescription() {
		return this.getClass().getSimpleName()
				+ ": Beste Monatsaufteilung bei einmaliger Ratenanpassung (Prozent) mit Auslauf von Kredit 1 ";
	}

	@Override
	public void run() throws Exception {

		double gesamtMonatsrate = getKreditDaten(1).getDaten().getMonatsrate()
				+ getKreditDaten(2).getDaten().getMonatsrate();
		final double stepSize = 10.0;
		double minRate1 = getKreditDaten(1).getBilligster()
				.getStartZinsbetrag() + stepSize;
		double minRate2 = getKreditDaten(2).getBilligster()
				.getStartZinsbetrag() + stepSize;
		double rate1 = minRate1;
		double rate2 = gesamtMonatsrate - rate1;
		int segmentCount1 = 0;
		int segmentCount2 = 0;
		KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
		KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();

		boolean ende = false;

		while (!ende && rate1 >= minRate1 && rate2 >= minRate2) {

			// Anfangsrate festlegen, falls Kredit 1 überhaupt geändert werden
			// darf. Zwangsläufig ist davon abhängig, ob Kredit 2 auch angepasst
			// werden darf
			if (getKreditDaten(1).getDaten().getMaxSegmente() > segmentCount1 && //
					getKreditDaten(2).getDaten().getMaxSegmente() > segmentCount2) {
				daten1.setMonatsrate(rate1);
				segmentCount1 = 1;
				daten2.setMonatsrate(rate2);
				segmentCount2 = 1;

			} else {
				ende = false;

			}
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			boolean gewechselt = false;
			while (kredit1.next() | kredit2.next()) {

				// Monatsrate einmalig nach dem Ende eines Kredits verändern
				// falls Kredit 2 änderbar
				if (!gewechselt) {
					if (kredit1.zuEnde() && !kredit2.zuEnde()) {
						if (getKreditDaten(2).getDaten().getMaxSegmente() > segmentCount1) {
							kredit2.neuesSegment(gesamtMonatsrate
									* (absenkungAufProzent / 100.0));
							segmentCount2++;
							gewechselt = true;
						}
					}
					if (kredit2.zuEnde() && !kredit1.zuEnde()) {
						if (getKreditDaten(1).getDaten().getMaxSegmente() > segmentCount1) {
							kredit1.neuesSegment(gesamtMonatsrate
									* (absenkungAufProzent / 100.0));
							segmentCount1++;
							gewechselt = true;
						}
					}
				}
			}

			gegenBilligstenKreditVergleiche(kredit1, kredit2);

			rate1 += stepSize;
			rate2 = gesamtMonatsrate - rate1;
		}

	}
}
