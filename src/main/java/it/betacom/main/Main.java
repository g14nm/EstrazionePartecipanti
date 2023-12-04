package it.betacom.main;

import java.util.Scanner;

import it.betacom.gestionedb.OperazioniDatabase;
import it.betacom.stampa.EsportazioneStatistiche;

public class Main {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		scegliOpzione(scanner);
		scanner.close();
		
	}
	
	private static void scegliOpzione(Scanner scanner) {
		boolean uscita = false;
		while(!uscita) {
			System.out.println("--------------------------------------------");
			System.out.println("Scegli opzione:"
					+ "\n1 -> inizializza database"
					+ "\n2 -> estrazione casuale"
					+ "\n3 -> visualizza statistiche estrazioni"
					+ "\n4 -> genera pdf statisiche estrazioni"
					+ "\n5 -> svuota database"
					+ "\n6 -> esci");
			switch(scanner.nextLine()) {
				case "1" : 
					OperazioniDatabase.inizializza();
					System.out.println("--------------------------------------------");
					break;
				case "2" : 
					OperazioniDatabase.estraiPartecipante();
					System.out.println("--------------------------------------------");
					break;
				case "3" : 
					EsportazioneStatistiche.stampaStatisticheSuConsole();
					break;
				case "4" : 
					EsportazioneStatistiche.creaPdfStatistiche();
					System.out.println("--------------------------------------------");
					break;
				case "5" :
					OperazioniDatabase.dropTabelle();
					System.out.println("--------------------------------------------");
					break;
				case "6" : 
					uscita = true;
					break;
				default :
					System.out.println("Opzione non valida!");
					System.out.println("--------------------------------------------");
			}
		}
	}
	
}
