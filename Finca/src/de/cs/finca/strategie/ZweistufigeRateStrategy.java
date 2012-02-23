package de.cs.finca.strategie;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;

public class ZweistufigeRateStrategy extends Strategy {

	public ZweistufigeRateStrategy(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		super(daten1, daten2);
	}

	@Override
	public String getDescription() {
		return "Die Gesamtbelastung R1 + R2 soll nur für die Dauer des ersten Kredits hoch sein. "
				+ System.lineSeparator()
				+ "Mit Auslaufen des ersten Kredits soll der zweite Kredit auf die die Rate max(R1, R2) angepasst werden.";
	}

	@Override
	public String getShortDescription() {
		return "Feste Gesamtratenabsekung mit erstem Kreditende auf höhere Rate beider Kredite";
	}

	@Override
	public void run() throws Exception {
		KreditStartDaten daten1 = getKreditDaten(1).getDaten().copy();
		KreditStartDaten daten2 = getKreditDaten(2).getDaten().copy();
		Kredit kredit1 = new Kredit(daten1);
		Kredit kredit2 = new Kredit(daten2);

		boolean gewechselt = false;
		while (kredit1.next() | kredit2.next()) {

			// Einmalig wechseln
			if (!gewechselt) {
				if (kredit1.zuEnde() && !kredit2.zuEnde()) {
					if (daten1.getMonatsrate() > daten2.getMonatsrate())
						kredit2.neuesSegment(daten1.getMonatsrate());
					gewechselt = true;
				}
				if (kredit2.zuEnde() && !kredit1.zuEnde()) {
					if (daten2.getMonatsrate() > daten1.getMonatsrate())
						kredit1.neuesSegment(daten2.getMonatsrate());
					gewechselt = true;
				}

			}
		}

		// Da wir nicht verschiedene Kombinationen durchrechnen, ist der
		// benutzte Kredit auch der billigste
		getKreditDaten(1).setBilligster(kredit1);
		getKreditDaten(2).setBilligster(kredit2);

	}
}
