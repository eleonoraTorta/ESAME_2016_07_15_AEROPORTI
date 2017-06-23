package it.polito.tdp.flight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.flight.model.Model;
import it.polito.tdp.flight.model.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtDistanzaInput;

	@FXML
	private TextField txtPasseggeriInput;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCreaGrafo(ActionEvent event) {
		
		txtResult.clear();
		
		String dist = txtDistanzaInput.getText();
		if( dist == null){
			txtResult.appendText("ERRORE: inserire la distanza massima in km\n");
			return;	
		}
		int distanza =0;
		try{
			distanza = Integer.parseInt(dist);
		} catch (NumberFormatException e ){
			txtResult.appendText("ERRORE: inserire un carattere numerico\n");
			return;
		}
		
		model.creaGrafo(distanza);
		
		txtResult.appendText("Dato un aeroporto è possibile raggiungere un qualsiasi altro aeroporto? " + model.tuttiVerticiraggiungibili() + "\n");
		txtResult.appendText("L'aeroporto raggiungibile da Fiumicino e piu lontano da esso è " + model.getAeroportoPiuLontano("Fiumicino") + "\n");
		
		
	}

	@FXML
	void doSimula(ActionEvent event) {
		
		txtResult.clear();
		
		// Acquisisco dati per creare grafo
		String dist = txtDistanzaInput.getText();
		if( dist == null){
			txtResult.appendText("ERRORE: inserire la distanza massima in km\n");
			return;	
		}
		int distanza =0;
		try{
			distanza = Integer.parseInt(dist);
		} catch (NumberFormatException e ){
			txtResult.appendText("ERRORE: inserire un carattere numerico relativo alla distanza massima\n");
			return;
		}
		
		model.creaGrafo(distanza);
		
		// Acquisisco dati per la simulazione
		String numero = txtPasseggeriInput.getText();
		if( numero == null){
			txtResult.appendText("ERRORE: inserire un numero relativo al numero di passeggeri\n");
			return;
		}
		
		int k = 0;
		try{
			k =  Integer.parseInt(numero);
		} catch( NumberFormatException e ){
			txtResult.appendText("ERRORE: inserire un carattere numerico relativo al numero di passeggeri\n");
			return;
		}
		
		List <Statistics> statistica = model.simula(k);
		for( Statistics s : statistica){
			txtResult.appendText(s.getAereoporto() + " " + s.getPersone() + "\n");
		}
		
		
		
	}

	@FXML
	void initialize() {
		assert txtDistanzaInput != null : "fx:id=\"txtDistanzaInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtPasseggeriInput != null : "fx:id=\"txtPasseggeriInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Untitled'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}
}
