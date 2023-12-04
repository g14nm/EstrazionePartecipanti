package it.betacom.stampa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import it.betacom.gestionedb.OperazioniDatabase;

public class EsportazioneStatistiche {
	
	private static final String FONT_PATH = "./src/main/resources/22815_LCOUR.ttf";
	private static final String FILE_PATH = "./statistiche.pdf";
	
	private static final Logger logger = LogManager.getLogger(EsportazioneStatistiche.class.getName());
	private static final String ERRORE = "Errore! Controllare file di log";
	private static final String DB_VUOTO = "Database vuoto!";
	
	public static void creaPdfStatistiche() {
		if(!OperazioniDatabase.esistonoTabelle())
			System.out.println(DB_VUOTO);
		else {
			try {
				//Inizializzazione documento PDF
				PdfDocument pdf = new PdfDocument(new PdfWriter(FILE_PATH));

				//Inizializzazione PDF con font specifico
				Document document = new Document(pdf);
				PdfFont courier_new = PdfFontFactory.createFont(FONT_PATH);
				document.setFont(courier_new);

				//Definizione contenuti

				//intestazione principale
				Paragraph intestazione = new Paragraph(Layout.intestazione());

				//intestazione statistiche ed elenco statistiche
				Paragraph statistiche = new Paragraph()
						.add(new Text(Layout.intestazioneStatistiche()).setBold())
						.add(Layout.statistiche());

				//statistiche e totale
				Paragraph fine = new Paragraph()
						.add(Layout.separatoreRighe());

				//Aggiunta contenuti al documento
				document.add(intestazione);
				document.add(statistiche);
				document.add(fine);

				//Chiusura documento
				document.close();

				logger.debug("generato pdf delle statistiche delle estrazioni");
				System.out.println("PDF generato correttamente");
			} catch (Exception e) {
				logger.error(e.getMessage());
				System.out.println(ERRORE);
			}
		}
	}
	
	public static void stampaStatisticheSuConsole() {
		if(!OperazioniDatabase.esistonoTabelle())
			System.out.println(DB_VUOTO);
		else {
			System.out.println(
					Layout.intestazione()
					+ "\n"
					+ Layout.intestazioneStatistiche()
					+ Layout.statistiche()
					+ Layout.separatoreRighe()
					);
		}
	}
	
}
