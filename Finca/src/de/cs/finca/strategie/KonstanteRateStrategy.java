package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class KonstanteRateStrategy extends Strategy {

	public KonstanteRateStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Mit Auslauf von Kredit 1 wird die Rate R2 von Kredit 2 erh�ht, so dass die Monatsbelastung R1 + R2 "
				+ System.lineSeparator()
				+ "�ber den gesamten Zeitraum gleich bleibt.";
	}

	@Override
	public String getShortDescription() {
		return "Beste Monatsaufteilung bei gleichbleibender monatlicher Belastung.";
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
		KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
		KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();

		while (rate1 >= minRate1 && rate2 >= minRate2) {
			daten1.setMonatsrate(rate1);
			daten2.setMonatsrate(rate2);
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			boolean gewechselt = false;
			while (kredit1.next() | kredit2.next()) {

				// Monatsrate einmalig nach dem Ende eines Kredits ver�ndern
				if (!gewechselt) {
					if (kredit1.zuEnde() && !kredit2.zuEnde()) {
						kredit2.neuesSegment(gesamtMonatsrate);
						gewechselt = true;
					}
					if (kredit2.zuEnde() && !kredit1.zuEnde()) {
						kredit1.neuesSegment(gesamtMonatsrate);
						gewechselt = true;
					}
				}
			}

			gegenBilligstenKreditVergleiche(kredit1, kredit2);

			rate1 += stepSize;
			rate2 = gesamtMonatsrate - rate1;
		}

	}
}
