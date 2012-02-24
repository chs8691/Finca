package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

abstract public class Strategy {

	public class KreditDaten {
		private Kredit billigster;
		private final KreditStartDaten daten;
		private final double minRate;

		private KreditDaten(KreditStartDaten daten, double stepSize)
				throws Exception {
			this.daten = daten.copy();
			this.setBilligster(new Kredit(daten));
			this.minRate = this.getBilligster().getStartZinsbetrag() + stepSize;

		}

		public Kredit getBilligster() {
			// TODO Copy?
			return billigster;
		}

		public KreditStartDaten getDaten() {
			return daten.copy();
		}

		public double getMinRate() {
			return minRate;
		}

		public void setBilligster(Kredit billigster) {
			this.billigster = billigster;
		}

	}

	private final KreditDaten kreditDaten1;
	private final KreditDaten kreditDaten2;

	/**
	 * Schrittgröße, um welche die Kredite verändert werden Defaultwert ist 10.0
	 */
	private final double stepSize = 10.0;

	private double gesamtMonatsrate;

	public Strategy(KreditStartDaten daten1, KreditStartDaten daten2)
			throws Exception {
		this.kreditDaten1 = new KreditDaten(daten1, stepSize);
		this.kreditDaten2 = new KreditDaten(daten2, stepSize);
		this.kreditDaten1.getBilligster().run();
		this.kreditDaten2.getBilligster().run();

		this.gesamtMonatsrate = kreditDaten1.getDaten().getMonatsrate()
				+ kreditDaten2.getDaten().getMonatsrate();

	}

	/**
	 * Prüft, ob die Kreditkombination billiger ist, als der bisher billigste.
	 * Wenn ja, wird dieser als neue billigste Kreditkombination gesetzt.
	 * 
	 * @param kredit1
	 * @param kredit2
	 */
	public void gegenBilligstenKreditVergleiche(Kredit kredit1, Kredit kredit2) {
		double aktKosten = kredit1.getKosten() + kredit2.getKosten();
		double billigsteKosten = getKreditDaten(1).getBilligster().getKosten()
				+ getKreditDaten(2).getBilligster().getKosten();
		if (aktKosten < billigsteKosten) {
			getKreditDaten(1).setBilligster(kredit1);
			getKreditDaten(2).setBilligster(kredit2);
		}

	}

	public abstract String getDescription();

	public double getGesamtkosten() {
		return kreditDaten1.getBilligster().getKosten()
				+ kreditDaten2.getBilligster().getKosten();
	}

	protected double getGesamtMonatsrate() {
		return gesamtMonatsrate;
	}

	public KreditDaten getKreditDaten(int nr) {
		switch (nr) {
		case 1:
			return kreditDaten1;

		case 2:
			return kreditDaten2;
		default:
			throw new IllegalArgumentException("Kredit 1 oder 2 erwartet");

		}
	}

	public abstract String getShortDescription();

	protected double getStepSize() {
		return stepSize;
	}

	/**
	 * Führt die Strategie aus. In der Methode muss am Ende
	 * <code>getKreditdaten(nr).setBilligster()</code> für beide Kredite gesetzt
	 * werden (sofern sich dieser vom Startzeitpunkt unterscheidet).
	 */
	public abstract void run() throws Exception;

	public void setGesamtMonatsrate(double gesamtMonatsrate) {
		this.gesamtMonatsrate = gesamtMonatsrate;
	}

}
