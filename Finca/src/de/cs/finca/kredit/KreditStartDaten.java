package de.cs.finca.kredit;

import java.util.Date;

/**
 * Träger für Kreditdaten
 * 
 * @author ChristianSchulzendor
 * 
 */
public class KreditStartDaten {
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
	private final Date start;

	public KreditStartDaten(String name, double volumen, double zinsSatz,
			double monatsrate, Date start) {
		this.name = name;
		this.volumen = volumen;
		this.zinsSatz = zinsSatz;
		this.monatsrate = monatsrate;
		this.start = new Date(start.getTime());

	}

	public KreditStartDaten copy() {
		return new KreditStartDaten(name, volumen, zinsSatz, monatsrate, start);
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
