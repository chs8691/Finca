package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class FinanzierteSondertilgung extends Strategy {

	private final double sondertilgungGesamt;

	/**
	 * @param sondertilgungGesamt
	 *            Betrag, wieviel für Sondertilgung insgesamt zur Verfügung
	 *            steht
	 * @throws IllegalArgumentException
	 *             wenn Sondertilgung negativ ist
	 */
	public FinanzierteSondertilgung(KreditStartDaten daten1,
			KreditStartDaten daten2, double sondertilgungGesamt)
			throws Exception {
		super(daten1, daten2);
		this.sondertilgungGesamt = sondertilgungGesamt;
		if (this.sondertilgungGesamt < 0)
			throw new IllegalArgumentException();

	}

	@Override
	public String getDescription() {
		return "Findet die Höhe von Kredit 2, um die wirtschaftlichste Sondertilgungsrate von Kredit 1 zu ermitteln. Die Gesmatsondetilgungssumme wird auf die ursprüngliche Höhe von Kredit 2 aufeschlagen";
	}

	@Override
	public String getShortDescription() {
		return "Billigere Kredit 2 finanziert Sondetilgung von Kredit 1";
	}

	@Override
	public void run() throws Exception {

		boolean ende = false;
		double sondertilgungsBetrag = 1;
		KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
		KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();
		daten1.setMaxSondertilgung(4000.0);

		while (!ende) {

			daten2.setVolumen(daten2.getVolumen() + sondertilgungsBetrag);
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			double restSondertilgung = sondertilgungsBetrag;
			while (kredit1.next() && kredit2.next()) {
				restSondertilgung = kredit1.sondertilgung(restSondertilgung);
			}

			// Abbruch, wenn Sondertilgungsbetrag nicht mehr komplett
			// abgearbeitet wird
			if (restSondertilgung > 0)
				ende = true;

			gegenBilligstenKreditVergleiche(kredit1, kredit2);
		}

	}

}
