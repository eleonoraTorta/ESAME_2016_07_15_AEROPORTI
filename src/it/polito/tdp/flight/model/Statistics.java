package it.polito.tdp.flight.model;

public class Statistics implements Comparable <Statistics> {
	
	private Airport aereoporto;
	private int persone;
	
	public Statistics(Airport aereoporto, int persone) {
		super();
		this.aereoporto = aereoporto;
		this.persone = persone;
	}

	public Airport getAereoporto() {
		return aereoporto;
	}

	public void setAereoporto(Airport aereoporto) {
		this.aereoporto = aereoporto;
	}

	public int getPersone() {
		return persone;
	}

	public void setPersone(int persone) {
		this.persone = persone;
	}

	@Override
	public String toString() {
		return  aereoporto + " " + persone ;
	}

	@Override
	public int compareTo(Statistics altro) {
		return -( this.persone - altro.persone);
	}
	
	

}
