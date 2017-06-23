package it.polito.tdp.flight.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.flight.model.Airline;
import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.AirportPair;
import it.polito.tdp.flight.model.Model;
import it.polito.tdp.flight.model.Route;

public class FlightDAO {

	public List<Airline> getAllAirlines() {
		String sql = "SELECT * FROM airline";
		List<Airline> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Airline(res.getInt("Airline_ID"), res.getString("Name"), res.getString("Alias"),
						res.getString("IATA"), res.getString("ICAO"), res.getString("Callsign"),
						res.getString("Country"), res.getString("Active")));
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public List<Route> getAllRoutes() {
		String sql = "SELECT * FROM route";
		List<Route> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Route(res.getString("Airline"), res.getInt("Airline_ID"), res.getString("Source_airport"),
						res.getInt("Source_airport_ID"), res.getString("Destination_airport"),
						res.getInt("Destination_airport_ID"), res.getString("Codeshare"), res.getInt("Stops"),
						res.getString("Equipment")));
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public List<Airport> getAllAirports() {
		String sql = "SELECT * FROM airport";
		List<Airport> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Airport(res.getInt("Airport_ID"), res.getString("name"), res.getString("city"),
						res.getString("country"), res.getString("IATA_FAA"), res.getString("ICAO"),
						res.getDouble("Latitude"), res.getDouble("Longitude"), res.getFloat("timezone"),
						res.getString("dst"), res.getString("tz")));
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	
	//METODO NON USATO
	public int checkFlightFromTo(Airport a , Airport b) {
		String sql = "SELECT COUNT(Airline_ID) as counter " +
						"FROM route " +
						"WHERE Source_Airport_ID = ? AND Destination_Airport_ID = ?";
		
		int counter = 0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a.getAirportId());
			st.setInt(2, b.getAirportId());
			ResultSet res = st.executeQuery();

			res.next();
			counter = res.getInt("counter");
			
			conn.close();
			return counter;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	
	public List <AirportPair> getAirportsConnected(Map <Integer, Airport> aeroporti){
		String sql = "SELECT Source_Airport_ID, Destination_Airport_ID " +
					"FROM route";
		
		List<AirportPair> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				
				Airport partenza = aeroporti.get(res.getInt("Source_Airport_ID"));
				Airport destinazione = aeroporti.get(res.getInt("Destination_Airport_ID"));
				
				AirportPair a = new AirportPair(partenza,destinazione);
				
				list.add(a);
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	
	public int getIdFromName(String nomeAeroporto) {
		String sql = "SELECT Airport_ID " + 
					"FROM airport " + 
					"WHERE Name = ?";
		
		int id = 0 ;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nomeAeroporto);
			
			ResultSet res = st.executeQuery();

			if( res.next() ){
				id = res.getInt("Airport_ID");
			}
			
			conn.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public static void main(String args[]) {
		FlightDAO dao = new FlightDAO();
		Model model = new Model();

//		List<Airline> airlines = dao.getAllAirlines();
//		System.out.println(airlines);
//
//		List<Airport> airports = dao.getAllAirports();
//		System.out.println(airports);
//
//		List<Route> routes = dao.getAllRoutes();
//		System.out.println(routes);
		
	
		Map <Integer, Airport> mappa = new TreeMap <Integer, Airport>();
		for( Airport a : dao.getAllAirports())
			mappa.put(a.getAirportId(), a);
		Airport a = mappa.get(7242);
		Airport b = mappa.get (3832);
	
		System.out.println(dao.checkFlightFromTo(a, b));
	}
	


}
