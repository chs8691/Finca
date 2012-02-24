package de.cs.finca.kredit;

import java.util.Date;

/**
 * Träger für Kreditdaten
 * 
 * @author ChristianSchulzendor
 * 
 */
public class KreditStartDaten {
	public static KreditStartDaten create(String name, double volumen,
			double zinsSatz, double monatsrate, Date start) {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate, start,
				0);
	}

	/**
	 * Kredit soll Sondertilgung können und machen
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
			double volumen, double zinsSatz, double monatsrate, Date start,
			double maxSondertilgung) {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate, start,
				maxSondertilgung);
	}

	/**
	 * Unveränerliches Segment (zeitlicher Abschnitt) eines Kredits
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

	/**
	 * 
	 * @param name
	 * @param volumen
	 * @param zinsSatz
	 * @param monatsrate
	 * @param start
	 * @param gesamtSondertilgung
	 * @throws IllegalArgumentException
	 *             wenn ein Betrag negativ ist
	 */
	private KreditStartDaten(String name, double volumen, double zinsSatz,
			double monatsrate, Date start, double maxSondertilgung) {
		this.name = name;
		this.volumen = volumen;
		this.zinsSatz = zinsSatz;
		this.monatsrate = monatsrate;
		this.maxSondertilgung = maxSondertilgung;
		this.start = new Date(start.getTime());

		if (this.volumen < 0 || this.zinsSatz < 0 || this.monatsrate < 0
				|| this.maxSondertilgung < 0)
			throw new IllegalArgumentException();

	}

	public KreditStartDaten copy() {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate, start,
				maxSondertilgung);
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

	/**
	 * Setzt den maximalen Betrag für eine jährliche Sondertilgung fest
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
