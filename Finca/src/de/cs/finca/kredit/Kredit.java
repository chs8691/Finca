package de.cs.finca.kredit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Kredit {

	private double kosten;
	private int laufzeit;
	private double rest;
	private final Date endeDate = new Date();
	private final KreditStartDaten startDaten;
	private int letzteSondertilgung;
	private double sondertilgungGesamt;

	private final List<Segment> segmente = new ArrayList<>();

	private int aktSegment;

	/**
	 * Kredit mit initial einem Segment aus den Startdaten
	 * 
	 * @param daten
	 * @throws Exception
	 */
	public Kredit(KreditStartDaten daten) throws Exception {
		this.startDaten = daten.copy();

		if (this.startDaten.getZinsSatz() <= 0
				|| this.startDaten.getZinsSatz() >= 100) {
			throw new Exception("falsche Paramater für Kredit "
					+ daten.getName());
		}

		this.segmente.add(new Segment(this.startDaten.getMonatsrate(),
				this.startDaten.getStart(), this.segmente.size() + 1));

		reset();

	}

	private long addMonate(Date datum, int anzahl) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(datum);
		cal.add(Calendar.MONTH, anzahl);
		return cal.getTimeInMillis();
	}

	/**
	 * Legt das aktuelle Segment anhand akt. Laufzeit innerhalb des Loops fest.
	 */
	private void bestimmeAktSegment() {

		// Fall 1: Letztes Segment bereits erreicht
		if ((aktSegment + 1) == segmente.size()) {
			return;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDaten.getStart());
		cal.add(Calendar.MONTH, laufzeit);
		if (!segmente.get(aktSegment + 1).getStart().after(cal.getTime())) {
			++aktSegment;

		}

	}

	private double ermittleZinsbetrag(double volumen) {
		return ermittleZinsbetrag(startDaten.getZinsSatz(), volumen);
	}

	private double ermittleZinsbetrag(double zinsSatz, double volumen) {
		return volumen * (zinsSatz / 100.0) / 12.0;
	}

	public Date getEnde() {

		return endeDate;
	}

	public double getKosten() {
		return kosten;
	}

	public int getLaufzeit() {
		return laufzeit;
	}

	public List<Segment> getSegmente() {
		List<Segment> segmente = new ArrayList<>();
		for (Segment segment : this.segmente) {
			segmente.add(segment.copy());
		}
		return segmente;
	}

	public double getSondertilgungGesamt() {
		return sondertilgungGesamt;
	}

	public KreditStartDaten getStartDaten() {
		return startDaten;
	}

	/**
	 * Der StartZinsbetrag ist die Mindestmonatsrate. Sie errechnet sich aus dem
	 * Zins + 1 % Tilgung zusammen.
	 * 
	 * @return double
	 */
	public double getStartZinsbetrag() {

		return ermittleZinsbetrag(startDaten.getZinsSatz() + 1,
				startDaten.getVolumen());
	}

	public void neuesSegment(double monatsrate) {
		segmente.add(new Segment(monatsrate, new Date(addMonate(
				startDaten.getStart(), laufzeit)), segmente.size() + 1));
	}

	/**
	 * Durchläuft nächste Rate.
	 * 
	 * @return TRUE, wenn
	 */
	public boolean next() {
		return !zuEnde() ? rate(rest) : false;
	}

	private boolean rate(double start) {
		laufzeit++;
		double zinsBetrag = ermittleZinsbetrag(start);

		bestimmeAktSegment();

		kosten += zinsBetrag;
		double lRest = start
				- (segmente.get(aktSegment).getMonatsrate() - zinsBetrag);
		rest = lRest >= 0 ? lRest : 0;

		endeDate.setTime(addMonate(endeDate, 1));
		letzteSondertilgung++;
		return rest > 0.0;

	}

	private void reset() {
		rest = startDaten.getVolumen();
		laufzeit = 0;
		kosten = 0;
		endeDate.setTime(startDaten.getStart().getTime());
		aktSegment = 0;
		letzteSondertilgung = 0;
		sondertilgungGesamt = 0;

	}

	/**
	 * Durchläuft alle Raten in einem Rutsch.
	 */
	public void run() {
		reset();
		while (rate(rest))
			;

	}

	/**
	 * Führt eine Sondertilung durch, sofern möglich: einmal pro Jahr mit dem
	 * max. Betrag, der durch die StartBedingungen festgelegt wurde. Der Kredit
	 * muss natürlich noch laufen.
	 * 
	 * @param betrag
	 *            der Betrag der sondergetilgt werden soll
	 * @return Restbetrag der nicht verbraucht wurde
	 * @throws IllegalArgumentException
	 *             wenn betrag negativ
	 */
	public double sondertilgung(double betrag) {
		if (betrag < 0)
			throw new IllegalArgumentException();

		if (betrag == 0)
			return betrag;

		// Kredit bereits beendet, keine Sondertilgung mehr möglich/nötig
		if (zuEnde())
			return betrag;

		// Ab dem 2. Jahr darf einmal jährlich getilgt werden
		if (letzteSondertilgung < 12)
			return betrag;

		// Tilgungsbetrag auf Obergrenze begrenzen
		double sondertilgungsBetrag = Math.min(betrag,
				startDaten.getMaxSondertilgung());
		// Bei Kreditende: Max. den Rest tilgen
		sondertilgungsBetrag = Math.min(sondertilgungsBetrag, rest);

		// Tilgen
		rest -= sondertilgungsBetrag;
		sondertilgungGesamt += sondertilgungsBetrag;

		// Sondertilgung in einem Jahr ermöglichen
		letzteSondertilgung = 0;

		// Nicht getilgten Betrag zurückgeben
		return betrag - sondertilgungsBetrag;
	}

	public boolean zuEnde() {
		return !(rest > 0);
	}

}
