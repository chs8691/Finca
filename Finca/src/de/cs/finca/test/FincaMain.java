package de.cs.finca.test;

import java.util.Date;
import java.util.Formatter;
import java.util.List;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;
import de.cs.finca.kredit.Segment;
import de.cs.finca.strategie.EinfachesteSplitStrategy;
import de.cs.finca.strategie.KonstanteRateStrategy;
import de.cs.finca.strategie.Strategy;
import de.cs.finca.strategie.ZweistufigeRateStrategy;

//TODO N�chste Strategie entwickeln
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

			// Einfach mal die Kredite unabh�ngig von einander ausf�hren
			fm.einfacherKredit(daten1);
			System.out.println();
			fm.einfacherKredit(daten2);
			System.out.println();

			// Raten willk�rlich verteilen, summe muss 1004 sein
			daten1.setMonatsrate(502.00);
			daten2.setMonatsrate(502.00);
			// SplitStrategie: Einfachste Form: Keine Ratenanpassung bei
			// Auslaufen von Kredit 1
			fm.runStrategy(new EinfachesteSplitStrategy(daten1, daten2));

			// SplitStrategie: Konstante Monatsbelastung durch Anpassung
			// bei Auslauf vom ersten Kredit
			fm.runStrategy(new KonstanteRateStrategy(daten1, daten2));

			// Nehmen wir nun eine Monatsrate von 1.300 .-
			daten1.setMonatsrate(1004.00);
			daten2.setMonatsrate(296.00);
			fm.runStrategy(new EinfachesteSplitStrategy(daten1, daten2));
			fm.runStrategy(new KonstanteRateStrategy(daten1, daten2));
			fm.runStrategy(new ZweistufigeRateStrategy(daten1, daten2));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void einfacherKredit(KreditStartDaten daten) throws Exception {
		Kredit kredit = new Kredit(daten);
		kredit.run();
		printKredit(kredit);
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

	private void runStrategy(Strategy strategy) throws Exception {
		strategy.run();

		ergebnisreportStrategielauf(strategy);

	}
}
