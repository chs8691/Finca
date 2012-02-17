package de.cs.finca.bl;

public class KonstanteRateStrategy extends Strategy {

	public KonstanteRateStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Diese Strategie ermittelt die beste Monatratenaufteilung. "
				+ "Mit Auslauf von Kredit1 wird die Rate von Kredit2 erhöht, so dass die Monatsbelastung bleibt. "
				+ "Die monatliche Belastung ist aus der Summe der beiden Einzelwerte ermittelt.";
	}

	@Override
	public String getShortDescription() {
		return "Beste Monatsaufteilung bei gleichbleibender monatlicher Belastung.";
	}

	@Override
	public void run() throws Exception {

		double gesamtMonatsrate = getDaten1().getMonatsrate()
				+ getDaten2().getMonatsrate();
		final double stepSize = 10.0;
		double minRate1 = this.getBilligster1().getStartZinsbetrag() + stepSize;
		double minRate2 = this.getBilligster2().getStartZinsbetrag() + stepSize;
		double rate1 = minRate1;
		double rate2 = gesamtMonatsrate - rate1;
		KreditStartDaten daten1 = this.getDaten1().copy();
		KreditStartDaten daten2 = this.getDaten2().copy();

		while (rate1 >= minRate1 && rate2 >= minRate2) {
			daten1.setMonatsrate(rate1);
			daten2.setMonatsrate(rate2);
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			boolean gewechselt = false;
			while (kredit1.next() | kredit2.next()) {

				// Monatsrate einmalig nach dem Ende eines Kredits verändern
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

			double aktKosten = kredit1.getKosten() + kredit2.getKosten();
			double billigsteKosten = this.getBilligster1().getKosten()
					+ this.getBilligster2().getKosten();
			if (aktKosten < billigsteKosten) {
				this.setBilligster1(kredit1);
				this.setBilligster2(kredit2);
			}

			rate1 += stepSize;
			rate2 = gesamtMonatsrate - rate1;
		}

	}
}
