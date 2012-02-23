package de.cs.finca.kredit;

import java.util.Date;

public class Segment {
	private final double monatsrate;
	private final Date start;
	private final int nr;

	public Segment(double monatsrate, Date start, int nr) {
		this.monatsrate = monatsrate;
		this.nr = nr;
		this.start = new Date(start.getTime());
	}

	public Segment copy() {
		return new Segment(monatsrate, new Date(start.getTime()), nr);
	}

	public double getMonatsrate() {
		return monatsrate;
	}

	public int getNr() {
		return nr;
	}

	public Date getStart() {
		return new Date(start.getTime());
	}
}
