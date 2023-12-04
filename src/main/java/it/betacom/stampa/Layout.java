package it.betacom.stampa;

import org.apache.commons.lang3.StringUtils;

import it.betacom.gestionedb.OperazioniDatabase;

public class Layout {
	
	private static final int LUNGHEZZA_SEPARATORE_RIGHE = 70,
			NUMERO_CAMPI = 3,
			LUNGHEZZA_NOME = 15,
			LUNGHEZZA_NUMERO = 13,
			LUNGHEZZA_DATA = 22;
	
	public static String intestazione() {
		return
				separatoreRighe()
				+ "\nStatistiche estrazioni\n"
				+ separatoreRighe();
	}
	
	public static String intestazioneStatistiche() {
		return 
				StringUtils.rightPad("Nome", LUNGHEZZA_NOME) + " | "
				+ StringUtils.rightPad("N. estrazioni", LUNGHEZZA_NUMERO) + " | "
				+ StringUtils.rightPad("Data ultima estrazione", LUNGHEZZA_DATA)
				+ "\n";
	}
	
	public static String statistiche() {
		return OperazioniDatabase.getEstrazioniToString(LUNGHEZZA_NOME, LUNGHEZZA_NUMERO, LUNGHEZZA_DATA);
	}
	
	//separatore di default
	public static String separatoreRighe() {
		return StringUtils.rightPad("", LUNGHEZZA_SEPARATORE_RIGHE, "-");
	}
	
	public static String separatoreRighe(int lunghezza) {
		return StringUtils.rightPad("", lunghezza, "-");
	}
	
	public static String separatoreRigheLista() {
		return separatoreRighe(LUNGHEZZA_NOME + LUNGHEZZA_NUMERO + LUNGHEZZA_DATA + (NUMERO_CAMPI - 1) * 3);
	}

}
