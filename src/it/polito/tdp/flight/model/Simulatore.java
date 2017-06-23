package it.polito.tdp.flight.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class Simulatore {
	
	private SimpleDirectedWeightedGraph <Airport, DefaultWeightedEdge> grafo;
	private Model model = new Model();
	
	//parametri di simulazione
	private int k;
	private int day;
	private LocalTime timeIniziale;
	
	// variabili di misurazione
	private Set <Airport> listaAeroporti;
//	private Map <Integer, Integer> mappa;
	
	//Lista degli eventi
	private PriorityQueue <Evento> queue;
	
	public Simulatore(SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo, Set <Airport> aeroporti, int k) {
		super();
		this.grafo = grafo;
		this.k = k;
		
		this.queue = new PriorityQueue <Evento>();
		this.listaAeroporti = aeroporti;
//		this.mappa = new TreeMap <Integer, Integer>();
		this.day = 1;
		
		// Inizializzo la struttura dati che tiene traccia delle variabili di interesse:
		// E una mappa con chiave l'id dell'aeroporto e come valore il numero di persone presenti in quel determinato aeroporto.
		// Inizializzo tutti gli aeroporti a zero
		
		for( Airport a : listaAeroporti){
//			mappa.put(a.getAirportId(), 0);
			a.setPersone();
		}
		
	}

	public void caricaEventiIniziali(LocalTime timeIniziale) {
		this.timeIniziale = timeIniziale;
		this.day = 1;
		
		System.out.println("Time iniziale: " + timeIniziale);
		
		for (  int i = 0 ; i < k ; i++) {
			
			// Scelgo l' aeroporto di partenza
			Airport partenza = this.randomInitialAirport();
			System.out.println("Partenza: "+partenza);
			// Aggiungo agli aeroporti di partenza il numero di persone presenti 
//			mappa.put(partenza.getAirportId(), mappa.get(partenza.getAirportId()) + 1);
			partenza.addPersone();
			
			// Scelgo la destinazione
			Airport destinazione = this.randomDestination(partenza);
			System.out.println("Destinazione: " +destinazione);
			// Se esiste la destinazione
			if( destinazione != null){
				
				double distanza = model.calcolaDistanza(partenza, destinazione);
				double durata = (distanza / 800.0) * 60;  // Durata in minuti
				LocalTime timePartenza = timeIniziale.plusHours(1); // I primi voli partono alle 7.00
				LocalTime timeArrivo = timePartenza.plusHours((long) durata) ; // serve il +1?
			
				// Valuto se l'ora di arrivo è inferiore alla fine della simulazione ( 6*60 + 48* 60 = 3240)
				LocalTime fineSimulazione = timeIniziale.plusHours(48);
				System.out.println("Fine simulazione : ore " + fineSimulazione + "del giorno due");
				System.out.println("TimeArrivo : " + timeArrivo);
				if( timeArrivo.isBefore(fineSimulazione)){
					// Posso schedulare l'evento
					Evento e = new Evento (timePartenza, partenza, timeArrivo , destinazione );
					System.out.println(e + "\n");
					queue.add(e);
				}	
			}
		}
		
	}


	public void run(){
		
		while(! queue.isEmpty()){
			Evento e = queue.poll();
			
			//Diminuisco le persone all'aeroporto di partenza
			Airport partenza = e.getPartenza();
//			mappa.put(partenza.getAirportId(), mappa.get(partenza.getAirportId()) - 1);
			partenza.removePersone();
			// Aggiungo le persone che si trovano all'aeroporto di destinazione
			Airport destinazione = e.getDestinazione();
//			mappa.put(destinazione.getAirportId(), mappa.get(destinazione.getAirportId()) + 1);
			destinazione.addPersone();
			
			// Valuto se puo essere schedulata una nuova partenza , dato l'orario di arrivo nella destinazione
			if( this.nuovaPartenzaPossibile(e.getTimeArrivo()) != null){
				LocalTime timeNuovaPartenza = this.nuovaPartenzaPossibile(e.getTimeArrivo());
				// Valuto se esiste una destinazione raggiungibile
				Airport destinazioneNuova = this.randomDestination(e.getDestinazione());
				// Se esiste la destinazione
				if( destinazioneNuova != null){
					// valuto l'orario di arrivo nella destinazione
					double distanza = model.calcolaDistanza(e.getDestinazione(), destinazioneNuova);
					double durata = (distanza / 800.0) * 60;  // Durata in minuti
					LocalTime timeNuovoArrivo = timeNuovaPartenza.plusHours((long) durata);  

					// Valuto se l'ora di arrivo è inferiore alla fine della simulazione ( 6*60 + 48* 60 = 3240)
					LocalTime fineSimulazione = timeIniziale.plusHours(48);
					if( timeNuovoArrivo.isBefore(fineSimulazione)){
						// Posso schedulare l'evento
						Evento nuovo = new Evento (timeNuovaPartenza, e.getDestinazione(), timeNuovoArrivo , destinazioneNuova);
						queue.add(e);
					}	
					
				}
					
			}
		}
		
	}
	
	
	public Airport randomInitialAirport(){
		int dim = listaAeroporti.size();
		Random random = new Random();
		int numero = random.nextInt(dim);
		
		List <Airport> lista = new ArrayList <Airport>(listaAeroporti);
		Airport partenza = lista.get(numero);
		return partenza;
	} 
	
	private Airport randomDestination(Airport partenza) {
		
		// Valuto se esistono destinazioni raggiungibili
		int numRaggiungibili = this.grafo.outDegreeOf(partenza);
		if( numRaggiungibili < 1){
			return null;
		}
		
		// Se esitono aeroporti raggiungibili
		Random random = new Random();
		List <Airport> raggiungibili = new ArrayList <Airport>();
		for( DefaultWeightedEdge arco : this.grafo.outgoingEdgesOf(partenza)){
			raggiungibili.add( this.grafo.getEdgeTarget(arco));
		}
		int dim = raggiungibili.size();
		
		int numeroRandom = random.nextInt(dim);
		Airport destinazione =  raggiungibili.get(numeroRandom);
		return destinazione;
	}
	
	public LocalTime nuovaPartenzaPossibile( LocalTime time){
		
		LocalTime partenza = null;
		
		// se l'aereo atterra durante di "giorno" ( 7:00 - 23:00) delle prime 24 h
		if( time.isAfter(LocalTime.of(07,00)) && time.isBefore(LocalTime.of(23, 00))){
			// Valuto se esite una nuova partenza entro le 23
			for( LocalTime i = LocalTime.of(9, 00); i.isBefore(LocalTime.of(23,00)) ; i = i.plusHours(2)){
				if( time.isBefore(i)){
					partenza = i;
				}
			}
		}
		
		// se atterra durante la notte 
		else if(time.isAfter(LocalTime.of(23, 00)) && time.isBefore(timeIniziale.plusHours(25)) ){
	
		// il prossimo volo è alle 7:00 della mattina successiva 
			partenza = timeIniziale.plusHours(25);
		
		}
		
		
		// se l'aereo atterra durante di "giorno" ( 7:00 - 23:00) delle seconde 24 h
		else if( time.isAfter(timeIniziale.plusHours(25)) && time.isBefore(timeIniziale.plusHours(41))){
			// Valuto se esite una nuova partenza entro le 23
			for( LocalTime i = timeIniziale.plusHours(25) ; i.isBefore(timeIniziale.plusHours(41)) ; i = i.plusHours(2)){
				if( time.isBefore(i)){
					partenza = i;
				}
			}
		}
		
		// se atterra durante la notte delle seconde 24h 
		else if(time.isAfter(timeIniziale.plusHours(41)) ){
			// Non possono piu essere schedulati voli 
			partenza = null;
		}
		return partenza;
	}

	
	public List < Statistics> getStatistics(){
		List <Statistics> statistica = new ArrayList <Statistics>();
//		for ( Integer i : mappa.values()){
////			Statistics s = new Statistics (model.getAeroportoFromId(i), mappa.get(i));
//			Statistics s = new Statistics (model.getAeroportoFromId(i), mappa.get(i));
//			statistica.add(s);
//		}
		
		for( Airport a : listaAeroporti){
			if( a.getPersone()>0){
				Statistics s = new Statistics (a, a.getPersone());
				statistica.add(s);
			}
		}
		
		return statistica;
	}
	
	
	
			
	
	
	
//	//		switch(e.getTipo()){
//			
//	//		case PARTENZA:
//				Airport partenza = e.get
//				Airport destinazione = this.randomDestination(e.getAeroporto());
//				double distanza = model.calcolaDistanza(e.getAeroporto(), destinazione);
//				double durata = distanza / 800.0;
//				int timeArrivo = (int) (e.getTime() + durata) +1;
//				int timeNuovaPartenza = this.getTimeNuovaPartenza(timeArrivo);
//				
//				Evento arrivo = new Evento (timeNuovaPartenza, destinazione); 
//				queue.add(arrivo);
//	//			break;
//			
//	//		case ARRIVO:
//				int timeAtterraggio = e.getTime();
//				int partenza = this.getTimeNextPartenza(timeAtterraggio);
//				
//				Evento partenza = new Evento (partenza,tipo.PARTENZA, e.getAeroporto());
//				queue.add(partenza);
//				
//				
//	//			break;
			
		
		
	
	

	

}
