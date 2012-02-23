package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class EinfachesteSplitStrategy extends Strategy {

	public EinfachesteSplitStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Diese Strategie ermittelt die beste Verteilung der monatliche Gesamtbelastung R1 + R2 auf beide Kredite. "
				+ System.lineSeparator()
				+ "W�hrend der Laufzeit werden die Monatraten R1 und R2 NICHT mehr ver�ndert. ";
	}

	@Override
	public String getShortDescription() {
		return "Optimale Rate, wenn die Raten nicht angepasst werden.";
	}

	/**
	 * Diese Strategie ermittelt die beste Monatratenaufteilung. Das Startdatum
	 * der Kredite wird NICHT ber�cksichtigt. W�hrend der Laufzeit werden die
	 * Monatraten NICHT mehr ver�ndert. Die Gesamtmonatsrate wird aus der Summe
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

		while (rate1 >= getKreditDaten(1).getMinRate()
				&& rate2 >= getKreditDaten(2).getMinRate()) {
			daten1.setMonatsrate(rate1);
			daten2.setMonatsrate(rate2);
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
