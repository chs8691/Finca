package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

//TODO maxSegmente einführen
public class FinanzierteSondertilgung extends Strategy {

	/**
	 * @param sondertilgungGesamt
	 *            Betrag, wieviel für Sondertilgung insgesamt zur Verfügung
	 *            steht
	 * @throws IllegalArgumentException
	 *             wenn Sondertilgung negativ ist
	 */
	public FinanzierteSondertilgung(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);

	}

	@Override
	public String getDescription() {
		return "Findet die Höhe von Kredit 2, um die wirtschaftlichste Sondertilgungsrate von Kredit 1 zu ermitteln."
				+ " Die Gesamtsondetilgungssumme wird auf die ursprüngliche Höhe von Kredit 2 aufgeschlagen";
	}

	@Override
	public String getShortDescription() {
		return this.getClass().getSimpleName()
				+ ": Billigere Kredit 2 finanziert Sondetilgung von Kredit 1 bei festgelegten Raten (wie einfachsteSplitStrategy)";
	}

	@Override
	public void run() throws Exception {

		boolean ende = false;
		double stepSize = 10.0;
		double sondertilgungsBetrag = stepSize;
		double volumen2 = getKreditDaten(2).getDaten().getVolumen();

		while (!ende) {

			KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
			KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();
			daten1.setMaxSondertilgung(4000.0);
			daten2.setVolumen(volumen2 + sondertilgungsBetrag);
			Kredit kredit1 = new Kredit(daten1);
			Kredit kredit2 = new Kredit(daten2);

			double restSondertilgung = sondertilgungsBetrag;
			while (kredit1.next() | kredit2.next()) {
				if (!kredit1.zuEnde())
					restSondertilgung = kredit1
							.sondertilgung(restSondertilgung);
			}

			gegenBilligstenKreditVergleiche(kredit1, kredit2);

			// Abbruch, wenn Sondertilgungsbetrag nicht mehr komplett
			// abgearbeitet wird
			if (restSondertilgung > 0 || sondertilgungsBetrag >= 100000)
				ende = true;

			// nächster Durchlauf mit nächst höherer Sondertilgung
			sondertilgungsBetrag += stepSize;
		}

	}

}
