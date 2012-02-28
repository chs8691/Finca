package de.cs.finca.test;

import java.util.Date;
import java.util.Formatter;
import java.util.List;

import de.cs.finca.kredit.Kredit;
import de.cs.finca.kredit.KreditStartDaten;
import de.cs.finca.kredit.Segment;
import de.cs.finca.strategie.EinfachsteSplitStrategy;
import de.cs.finca.strategie.FinanzierteSondertilgung;
import de.cs.finca.strategie.KonstanteRateStrategy;
import de.cs.finca.strategie.Strategy;

//TODO Nächste Strategie entwickeln
public class FincaMain {
	private static final double zinsSatz2 = 3.0;
	private static final double zinsSatz1 = 5.18;
	private static final double kreditsumme1 = 49795.30;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FincaMain fm = new FincaMain();

		try {

			// fm.runStrategien1(1004, 35000.0);
			fm.runStrategien2(1004, 296, 35000.0);
			fm.runStrategienMitFixemKredit1(1300.0, 35000.0);

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
			line.append("-");
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

	private void printHeader(String text) {
		System.out.println();
		System.out
				.println("==================================================================================");
		System.out.println(text);
		System.out
				.println("==================================================================================");
	}

	private void printKredit(Kredit kredit) {
		System.out.println("Kredit " + kredit.getStartDaten().getName());
		System.out.println("-------------------------");
		System.out.printf("Volumen       : %,10.2f %n", kredit.getStartDaten()
				.getVolumen());
		System.out.printf("Zinssatz      : %10.2f %% %n", kredit
				.getStartDaten().getZinsSatz());
		System.out.printf("Kosten        : %10.2f %n", kredit.getKosten());
		System.out.printf("Sondertilgung : %10.2f %n",
				kredit.getSondertilgungGesamt());
		System.out.printf("Max. Segmente : %10d %n", kredit.getStartDaten()
				.getMaxSegmente());
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

	private void runKredit(double monatsbelastung, double kreditsumme2)
			throws Exception {
		KreditStartDaten daten1 = KreditStartDaten.create("Nr. 1",
				kreditsumme1, zinsSatz1, monatsbelastung, 9, new Date());
		KreditStartDaten daten2 = KreditStartDaten.create("Nr. 2",
				kreditsumme2, zinsSatz2, monatsbelastung, 9, new Date());

		printHeader("Kreditvergleich: Gesamtmonatsbelastung = "
				+ monatsbelastung + ", Kreditsumme2 = " + kreditsumme2);
		// Einfach mal die Kredite unabhängig von einander ausführen
		einfacherKredit(daten1);
		System.out.println();
		einfacherKredit(daten2);
		System.out.println();

	}

	private void runStrategien1(double monatsbelastung, double kreditsumme2)
			throws Exception {

		KreditStartDaten daten1 = KreditStartDaten.create("Nr. 1",
				kreditsumme1, zinsSatz1, monatsbelastung, 9, new Date());
		KreditStartDaten daten2 = KreditStartDaten.create("Nr. 2",
				kreditsumme2, zinsSatz2, monatsbelastung, 9, new Date());

		printHeader("Gesamtmonatsbelastung = " + monatsbelastung
				+ ", Kreditsumme2 = " + kreditsumme2);

		// Raten willkürlich verteilen, summe muss 1004 sein
		daten1.setMonatsrate(monatsbelastung / 2.0);
		daten2.setMonatsrate(monatsbelastung / 2.0);
		// SplitStrategie: Einfachste Form: Keine Ratenanpassung bei
		// Auslaufen von Kredit 1
		EinfachsteSplitStrategy einfachsteSplitStrategy = new EinfachsteSplitStrategy(
				daten1, daten2);
		runStrategy(einfachsteSplitStrategy);

		// Jetzt mit dem besten Ergebnis die Sondertilgung hinzufügen
		daten1.setMonatsrate(einfachsteSplitStrategy.getKreditDaten(1)
				.getBilligster().getSegmente().get(0).getMonatsrate());
		daten2.setMonatsrate(einfachsteSplitStrategy.getKreditDaten(2)
				.getBilligster().getSegmente().get(0).getMonatsrate());
		runStrategy(new FinanzierteSondertilgung(daten1, daten2));

		// SplitStrategie: Konstante Monatsbelastung durch Anpassung
		// bei Auslauf vom ersten Kredit
		runStrategy(new KonstanteRateStrategy(daten1, daten2, 100));

	}

	private void runStrategien2(double rate1, double rate2, double kreditsumme2)
			throws Exception {
		KreditStartDaten daten1 = KreditStartDaten.create("Nr. 1",
				kreditsumme1, zinsSatz1, rate1, 9, new Date());
		KreditStartDaten daten2 = KreditStartDaten.create("Nr. 2",
				kreditsumme2, zinsSatz2, rate2, 9, new Date());

		printHeader("Rate1 = " + rate1 + ", Rate2 = " + rate2
				+ ", Kreditsumme2 = " + kreditsumme2);

		runStrategy(new EinfachsteSplitStrategy(daten1, daten2));
		runStrategy(new KonstanteRateStrategy(daten1, daten2, 100));
		runStrategy(new KonstanteRateStrategy(daten1, daten2, 66));

	}

	private void runStrategienMitFixemKredit1(double monatsbelastung,
			double kreditsumme2) throws Exception {
		double monatsrate1 = 1004.0;
		KreditStartDaten daten1 = KreditStartDaten.create("Nr. 1",
				kreditsumme1, zinsSatz1, monatsrate1, 0, new Date());
		KreditStartDaten daten2 = KreditStartDaten.create("Nr. 2",
				kreditsumme2, zinsSatz2, monatsbelastung - monatsrate1, 2,
				new Date());

		printHeader("Gesamtmonatsbelastung=" + monatsbelastung
				+ ", Kreditsumme2=" + kreditsumme2 + ", Rate1 fix="
				+ monatsrate1);

		runStrategy(new EinfachsteSplitStrategy(daten1, daten2));
		runStrategy(new KonstanteRateStrategy(daten1, daten2, 100));
		runStrategy(new KonstanteRateStrategy(daten1, daten2, 66));

	}

	private void runStrategy(Strategy strategy) throws Exception {
		strategy.run();

		ergebnisreportStrategielauf(strategy);

	}
}
