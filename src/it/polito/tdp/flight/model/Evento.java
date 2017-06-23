package it.polito.tdp.flight.model;

import java.time.LocalTime;

public class Evento implements Comparable <Evento>{
	
//	public enum tipo { PARTENZA, ARRIVO};
	
	private LocalTime timePartenza;
//	private tipo tipo;
	private Airport partenza;
	private LocalTime timeArrivo;
	private Airport destinazione;
//	private int day;
	
	public Evento(LocalTime timePartenza, Airport partenza, LocalTime timeArrivo, Airport destinazione) {
		super();
		this.timePartenza = timePartenza;
//		this.tipo = tipo;
		this.partenza = partenza;
		this.timeArrivo = timeArrivo;
		this.destinazione = destinazione;
//		this.day = day;
	}

	public LocalTime getTimePartenza() {
		return timePartenza;
	}

	
//	public tipo getTipo() {
//		return tipo;
//	}
//
//	public void setTipo(tipo tipo) {
//		this.tipo = tipo;
//	}

	public Airport getPartenza() {
		return partenza;
	}

	public void setPartenza(Airport partenza) {
		this.partenza = partenza;
	}
	
	
	
	public LocalTime getTimeArrivo() {
		return timeArrivo;
	}

	public void setTimeArrivo(LocalTime timeArrivo) {
		this.timeArrivo = timeArrivo;
	}

	public Airport getDestinazione() {
		return destinazione;
	}

	public void setDestinazione(Airport destinazione) {
		this.destinazione = destinazione;
	}

	
//	public int getDay() {
//		return day;
//	}
//
//	public void setDay(int day) {
//		this.day = day;
//	}

	public void setTimePartenza(LocalTime timePartenza) {
		this.timePartenza = timePartenza;
	}

	@Override
	public int compareTo(Evento other) {
		return this.timePartenza.compareTo(other.timePartenza);
	}

	@Override
	public String toString() {
		return "Evento [timePartenza=" + timePartenza + ", partenza=" + partenza + ", timeArrivo=" + timeArrivo
				+ ", destinazione=" + destinazione + "]";
	}
	

}
