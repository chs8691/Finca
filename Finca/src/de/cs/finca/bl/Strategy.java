package de.cs.finca.bl;

//TODO *1 und 2 zusammenfassen in eine Struktur.
public abstract class Strategy {

	private Kredit billigster1;
	private Kredit billigster2;
	private final KreditStartDaten daten1;

	private final KreditStartDaten daten2;

	/**
	 * Schrittgröße, um welche die Kredite verändert werden Defaultwert ist 10.0
	 */
	private final double stepSize = 10.0;

	private double gesamtMonatsrate;
	private final double minRate1;

	private final double minRate2;

	protected Strategy(KreditStartDaten daten1, KreditStartDaten daten2)
			throws Exception {
		this.daten1 = daten1;
		this.daten2 = daten2;
		this.billigster1 = new Kredit(daten1);
		this.billigster2 = new Kredit(daten2);
		this.getBilligster1().run();
		this.getBilligster2().run();

		this.gesamtMonatsrate = getDaten1().getMonatsrate()
				+ getDaten2().getMonatsrate();
		this.minRate1 = this.getBilligster1().getStartZinsbetrag()
				+ getStepSize();
		this.minRate2 = this.getBilligster2().getStartZinsbetrag()
				+ getStepSize();

	}

	public Kredit getBilligster1() {
		return billigster1;
	}

	public Kredit getBilligster2() {
		return billigster2;
	}

	public KreditStartDaten getDaten1() {
		return daten1;
	}

	public KreditStartDaten getDaten2() {
		return daten2;
	}

	public abstract String getDescription();

	public double getGesamtkosten() {
		return billigster1.getKosten() + billigster2.getKosten();
	}

	protected double getGesamtMonatsrate() {
		return gesamtMonatsrate;
	}

	public double getMinRate1() {
		return minRate1;
	}

	public double getMinRate2() {
		return minRate2;
	}

	public abstract String getShortDescription();

	protected double getStepSize() {
		return stepSize;
	}

	/**
	 * Runs the strategy to calculate the best result
	 */
	public abstract void run() throws Exception;

	protected void setBilligster1(Kredit billigster1) {
		this.billigster1 = billigster1;
	}

	protected void setBilligster2(Kredit billigster2) {
		this.billigster2 = billigster2;
	}

	public void setGesamtMonatsrate(double gesamtMonatsrate) {
		this.gesamtMonatsrate = gesamtMonatsrate;
	}

}
