package de.cs.finca.bl;

public class EinfachesteSplitStrategy extends Strategy {

	public EinfachesteSplitStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Diese Strategie ermittelt die beste Monatratenaufteilung, "
				+ "w�hrend der Laufzeit werden die Monatraten NICHT mehr ver�ndert. "
				+ "Die monatliche Belastung ist aus der Summe der beiden Monatsraten ermittelt.";
	}

	@Override
	public String getShortDescription() {
		return "Beste Monatsaufteilung ohne Ratenanpassung";
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

		double rate1 = getMinRate1();
		double rate2 = getGesamtMonatsrate() - rate1;
		KreditStartDaten daten1 = getDaten1().copy();
		KreditStartDaten daten2 = getDaten2().copy();

		while (rate1 >= getMinRate1() && rate2 >= getMinRate2()) {
			daten1.setMonatsrate(rate1);
			daten2.setMonatsrate(rate2);
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			kredit1.run();
			kredit2.run();

			double aktKosten = kredit1.getKosten() + kredit2.getKosten();
			double billigsteKosten = this.getBilligster1().getKosten()
					+ this.getBilligster2().getKosten();
			if (aktKosten < billigsteKosten) {
				this.setBilligster1(kredit1);
				this.setBilligster2(kredit2);
			}

			rate1 += getStepSize();
			rate2 = getGesamtMonatsrate() - rate1;
		}

	}
}
