package it.polito.tdp.flight.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	
	private FlightDAO dao;
	private SimpleDirectedWeightedGraph <Airport, DefaultWeightedEdge> grafo;
	private List<Airport> aeroporti ;
	private Map <Integer, Airport> mappa;
	private List <DefaultWeightedEdge> shortestPath;
	
	
	public Model (){
		this.dao = new FlightDAO();
		this.mappa = new TreeMap <Integer, Airport>();
		
	}

	public List<Airport> getAeroporti(){
		if( aeroporti == null){
			aeroporti = dao.getAllAirports();
			
			for( Airport a : aeroporti){
				mappa.put(a.getAirportId(), a);
			}
		}
		return aeroporti;
	}
	
	
	public void creaGrafo (int distanza){
		
		this.grafo = new SimpleDirectedWeightedGraph <>(DefaultWeightedEdge.class);
		
		// Chiedo al dao tutte le coppie di aeroporti collegati
		this.getAeroporti();
		List <AirportPair> coppie = dao.getAirportsConnected(mappa);
		
		for( AirportPair a : coppie){
		
			// Aggiungo i vertici solo se i due aeroporti sono a distanza < di quella specificata dall'utente
			
			double dist = this.calcolaDistanza(a.getPartenza(), a.getDestinazione());
			if( dist < distanza &&  !a.getPartenza().equals(a.getDestinazione())){
				
				// calcolo il peso dell'arco (ovvero la durata del volo)
				   double durata =  dist / 800.0 ;
				
				// Aggiungo arco pesato  con relativ vertici (non li duplica)
				Graphs.addEdgeWithVertices(this.grafo, a.getPartenza(), a.getDestinazione(), durata);
			}	
		}
		
		for( Airport v : this.grafo.vertexSet()){
			System.out.println(v + "\n");	
		}
		for( DefaultWeightedEdge arco : this.grafo.edgeSet()){
			System.out.println(arco + "\n");
		}
	}
	
	public double calcolaDistanza( Airport a1, Airport a2){
		LatLng partenza = new LatLng (a1.getLatitude(), a1.getLongitude());
		LatLng arrivo = new LatLng (a2.getLatitude(), a2.getLongitude());
		
		double distanza = LatLngTool.distance(partenza, arrivo, LengthUnit.KILOMETER);
		return distanza;
	}
	
	private boolean esisteVoloDaA(Airport a1, Airport a2) {
		if( dao.checkFlightFromTo(a1, a2) > 0){
			return true;
		}
		return false;
	}
	
	//INTERPRETAZIONE 1: valuto se il grafo è connesso
	public boolean tuttiVerticiraggiungibili(){
		ConnectivityInspector <Airport, DefaultWeightedEdge> inspector = new ConnectivityInspector <Airport, DefaultWeightedEdge>(this.grafo);
		if ( inspector.isGraphConnected() == true){
			return true;
		}
		return false;
	
	}
	
	// INTERPRETAZIONE 2 : valuto se esistono dei cammini che collegano tutte le coppie di aeropporti ( con scalo)
	public boolean tuttiVerticiraggiungibili2(){
		
		DijkstraShortestPath <Airport, DefaultWeightedEdge> dsp;
		
		for( Airport a : this.grafo.vertexSet()){
			for( Airport b : this.grafo.vertexSet()){
				if(! a.equals(b)){
					
					dsp = new DijkstraShortestPath <Airport, DefaultWeightedEdge>(this.grafo, a,b);
					 shortestPath = dsp.getPathEdgeList();
					
					 if( shortestPath == null){
						 return false;
					 }
				}
			
			}
		}
		return true;	
	}
	
	
	
	// INTEREPRETAZIONE 1: trovo aeroporto collegato direttamente a Fiumicino e piu lontano da esso
	public Airport getAeroportoPiuLontano2(String nome){
		Airport partenza = mappa.get(this.getAirportIdFromName(nome));
		
		double max = Double.MIN_VALUE;
		Airport piuLontano = null;
		
		for( DefaultWeightedEdge arco : this.grafo.outgoingEdgesOf(partenza)){
			if( this.grafo.getEdgeWeight(arco) > max){
				max = this.grafo.getEdgeWeight(arco);
				piuLontano = this.grafo.getEdgeTarget(arco);
			}
		}
		return piuLontano;
	}
	
	// INTERPRETAZIONE 2: trovo aeroporto collegato CON SCALO piu lontano da Fiumicino
	public Airport getAeroportoPiuLontano(String nome){
		
		Airport partenza = mappa.get(this.getAirportIdFromName(nome));
		DijkstraShortestPath <Airport, DefaultWeightedEdge> dsp; 
		
		double max = Double.MIN_VALUE;
		Airport piuLontano = null;
		
		for (Airport a : this.grafo.vertexSet()){
			if(!a.equals(partenza)){
				dsp = new DijkstraShortestPath <Airport, DefaultWeightedEdge>(this.grafo, partenza,a);
				if (dsp.getPathLength() > max){
					max = dsp.getPathLength();   // restituisce la lunghezza PESATA del cammino
					piuLontano = a;
				}
			}
		}
		return piuLontano;
	}
	
	public int getAirportIdFromName(String nome){
		return dao.getIdFromName(nome);
	}
	
	
	public List <Statistics> simula(int k) {
		 
		 Simulatore sim = new Simulatore( this.grafo, this.grafo.vertexSet(),  k );
		 // La simulazione inizia alle ore 6:00
		 LocalTime timeIniziale = LocalTime.of(06, 00);
		 sim.caricaEventiIniziali(timeIniziale);
		 sim.run();
		 return sim.getStatistics();
		
	}
	
	public Airport getAeroportoFromId( int id){
		return mappa.get(id);
	}
	
	
	
	
	public void creaGrafo2(int distanza) {
		this.grafo = new SimpleDirectedWeightedGraph <>(DefaultWeightedEdge.class);
		
		// Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.getAeroporti());
		
		// Aggiungo gli archi
		for( Airport a1 : this.grafo.vertexSet()){
			for( Airport a2 : this.grafo.vertexSet()){
				
				// controllo che siano a distanza inferiore di quella specificata dall'utente
				if( this.calcolaDistanza(a1, a2) < distanza){
					// controllo che esista almeno una compagnia
					if( this.esisteVoloDaA(a1,a2)){
						// calcolo il peso dell'arco (ovvero la durata del volo)
						   double peso =  this.calcolaDistanza(a1, a2) / 800.0 ;
						// Aggiungo arco pesato
						Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso);
					}
				}
			}
		}
		System.out.println(this.grafo);
		
	}

	


}
