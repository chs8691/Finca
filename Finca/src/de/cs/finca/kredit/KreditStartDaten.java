package de.cs.finca.kredit;

import java.util.Date;

/**
 * Tr�ger f�r Kreditdaten
 * 
 * @author ChristianSchulzendor
 * 
 */
public class KreditStartDaten {
	public static KreditStartDaten create(String name, double volumen,
			double zinsSatz, double monatsrate, int maxSegmente, Date start) {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate,
				maxSegmente, start, 0);
	}

	/**
	 * Kredit soll Sondertilgung k�nnen und machen
	 * 
	 * @param name
	 * @param volumen
	 * @param zinsSatz
	 * @param monatsrate
	 * @param start
	 * @param maxSondertilgung
	 *            Sondertilung pro Jahr (max)
	 * @return KreditStartDaten
	 */
	public static KreditStartDaten createMitSondertilgung(String name,
			double volumen, double zinsSatz, double monatsrate,
			int maxSegmente, Date start, double maxSondertilgung) {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate,
				maxSegmente, start, maxSondertilgung);
	}

	/**
	 * Unver�nerliches Segment (zeitlicher Abschnitt) eines Kredits
	 * 
	 * @author ChristianSchulzendor
	 * 
	 */

	private String name;
	private double volumen;
	private double zinsSatz;
	private double monatsrate;
	private double maxSondertilgung;

	private final Date start;
	private int maxSegmente;

	/**
	 * 
	 * @param name
	 * @param volumen
	 * @param zinsSatz
	 * @param monatsrate
	 * @param start
	 * @param gesamtSondertilgung
	 * @param maxSegmente
	 *            Wie oft die Monatsrate angepasst werden kann
	 * @throws IllegalArgumentException
	 *             wenn ein Betrag negativ ist
	 */
	private KreditStartDaten(String name, double volumen, double zinsSatz,
			double monatsrate, int maxSegmente, Date start,
			double maxSondertilgung) {
		this.name = name;
		this.volumen = volumen;
		this.zinsSatz = zinsSatz;
		this.monatsrate = monatsrate;
		this.maxSondertilgung = maxSondertilgung;
		this.maxSegmente = maxSegmente;
		this.start = new Date(start.getTime());

		if (this.volumen < 0 || this.zinsSatz < 0 || this.monatsrate < 0
				|| this.maxSondertilgung < 0)
			throw new IllegalArgumentException();

	}

	public KreditStartDaten copy() {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate,
				maxSegmente, new Date(start.getTime()), maxSondertilgung);
	}

	public int getMaxSegmente() {
		return maxSegmente;
	}

	public double getMaxSondertilgung() {
		return maxSondertilgung;
	}

	public double getMonatsrate() {
		return monatsrate;
	}

	public String getName() {
		return name;
	}

	public Date getStart() {
		return new Date(start.getTime());
	}

	public double getVolumen() {
		return volumen;
	}

	public double getZinsSatz() {
		return zinsSatz;
	}

	public void setMaxSegmente(int maxSegmente) {
		this.maxSegmente = maxSegmente;
	}

	/**
	 * Setzt den maximalen Betrag f�r eine j�hrliche Sondertilgung fest
	 * 
	 * @param maxSondertilgung
	 */
	public void setMaxSondertilgung(double maxSondertilgung) {
		this.maxSondertilgung = maxSondertilgung;
	}

	public void setMonatsrate(double monatsrate) {
		this.monatsrate = monatsrate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVolumen(double volumen) {
		this.volumen = volumen;
	}

	public void setZinsSatz(double zinsSatz) {
		this.zinsSatz = zinsSatz;
	}
}
