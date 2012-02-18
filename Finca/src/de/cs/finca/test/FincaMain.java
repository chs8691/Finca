package de.cs.finca.test;

import java.util.Date;
import java.util.Formatter;
import java.util.List;

import de.cs.finca.bl.EinfachesteSplitStrategy;
import de.cs.finca.bl.KonstanteRateStrategy;
import de.cs.finca.bl.Kredit;
import de.cs.finca.bl.KreditStartDaten;
import de.cs.finca.bl.Segment;
import de.cs.finca.bl.Strategy;

public class FincaMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FincaMain fm = new FincaMain();
		KreditStartDaten daten1 = new KreditStartDaten("Nr. 1", 49795.30, 5.18,
				1004, new Date());
		KreditStartDaten daten2 = new KreditStartDaten("Nr. 2", 45000.00, 3.0,
				1004, new Date());

		try {

			// Einfach mal die Kredite unabhängig von einander ausführen
			fm.einfacherKredit(daten1);
			fm.einfacherKredit(daten2);

			// Raten willkürlich verteilen, summe muss 1004 sein
			daten1.setMonatsrate(502.00);
			daten2.setMonatsrate(502.00);
			// SplitStrategie: Einfachste Form: Keine Ratenanpassung bei
			// Auslaufen von Kredit 1
			fm.einfachsterSplit(daten1, daten2);

			// SplitStrategie: Konstante Monatsbelastung durch Anpassung
			// bei Auslauf vom ersten Kredit
			fm.konstanteRate(daten1, daten2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void einfacherKredit(KreditStartDaten daten) throws Exception {
		Kredit kredit = new Kredit(daten);
		kredit.run();
		printKredit(kredit);
	}

	private void einfachsterSplit(KreditStartDaten daten1,
			KreditStartDaten daten2) throws Exception {
		Strategy strategy = new EinfachesteSplitStrategy(daten1, daten2);
		strategy.run();

		ergebnisreportStrategielauf(strategy);

	}

	private void ergebnisreportStrategielauf(Strategy strategy) {
		Formatter f = new Formatter();
		f.format(strategy.getShortDescription() + ": Gesamtkosten = %,1.2f %n",
				strategy.getGesamtkosten());
		StringBuffer line = new StringBuffer();

		for (int i = 0; i < f.toString().length() - 3; i++)
			line.append("=");
		System.out.print(f.toString());
		System.out.println(line);
		System.out.println(strategy.getDescription() + System.lineSeparator());
		printKredit(strategy.getKreditDaten(1).getBilligster());
		System.out.printf("Minimale Rate : %,10.2f %n%n", strategy
				.getKreditDaten(1).getMinRate());
		printKredit(strategy.getKreditDaten(2).getBilligster());
		System.out.printf("Minimale Rate : %,10.2f %n%n", strategy
				.getKreditDaten(2).getMinRate());
	}

	private void konstanteRate(KreditStartDaten daten1, KreditStartDaten daten2)
			throws Exception {
		Strategy strategy = new KonstanteRateStrategy(daten1, daten2);
		strategy.run();

		ergebnisreportStrategielauf(strategy);

	}

	private void printKredit(Kredit kredit) {
		System.out.println("Kredit " + kredit.getStartDaten().getName());
		System.out.println("-------------------------");
		System.out.printf("Volumen       : %,10.2f %n", kredit.getStartDaten()
				.getVolumen());
		System.out.printf("Zinssatz      : %10.2f %% %n", kredit
				.getStartDaten().getZinsSatz());
		System.out.printf("Kosten        : %10.2f %n", kredit.getKosten());
		List<Segment> segmente = kredit.getSegmente();
		for (Segment segment : segmente) {
			System.out.printf(
					"Segment %1$d Rate: %3$,10.2f - ab %2$tm.%2$tY %n",
					segment.getNr(), segment.getStart(),
					segment.getMonatsrate());
		}
		System.out.printf(
				"Laufzeit      : %1$d Monate (%2$tm.%2$tY - %3$tm.%3$tY) %n",
				kredit.getLaufzeit(), kredit.getStartDaten().getStart(),
				kredit.getEnde());
	}
}
