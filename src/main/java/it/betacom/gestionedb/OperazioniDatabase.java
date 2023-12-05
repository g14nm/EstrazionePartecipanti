package it.betacom.gestionedb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OperazioniDatabase {
	
	private static final String FILE_PARTECIPANTI = "esercizioPartecipanti.CSV";
	private static final Logger logger = LogManager.getLogger(OperazioniDatabase.class.getName());
	private static final String ERRORE = "Errore! Controllare file di log";
	private static final String DB_VUOTO = "Database vuoto!";
	
	//crea tabelle "partecipanti" e "estrazioni" e inizializza "partecipanti" da file csv
	public static void inizializza() {
		if(esistonoTabelle())
			System.out.println("Inizializzazione già effettuata!");
		//se le tabelle non esistono vengono create e viene inizializzata la tabella "partecipanti"
		else {
			Statement statement = null;
			ResultSet resultSet = null;
			Connection connection = null;
			try {
				connection = DBHandler.getInstance().apriAndGetConnessione();
				connection.setAutoCommit(false);
				statement = connection.createStatement();
				statement.execute("CREATE TABLE partecipanti ("
						+ "id INT AUTO_INCREMENT NOT NULL,"
						+ "nome VARCHAR(255) NOT NULL,"
						+ "sede VARCHAR(255) NOT NULL,"
						+ "PRIMARY KEY (id))");
				statement.execute("CREATE TABLE estrazioni ("
						+ "id INT AUTO_INCREMENT NOT NULL,"
						+ "nome_estratto VARCHAR(255) NOT NULL,"
						+ "data_estrazione TIMESTAMP NOT NULL,"
						+ "PRIMARY KEY (id))");
				logger.debug("create tabelle \"partecipanti\" e \"estrazioni\"");
				inserisciInTabellaPartecipantiDaCSV();
				connection.commit();
				System.out.println("Inizializzazione avvenuta correttamente");
			} catch (SQLException e) {
				logger.error(e.getMessage());
				System.out.println(ERRORE);
				try {connection.rollback();} catch (SQLException e1) {logger.error(e1.getMessage()); System.out.println(ERRORE);}
			}
			finally {
				if(resultSet != null) try {resultSet.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
				if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
				if(connection != null) try {connection.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			}
		}
	}
	
	//controllo esistenza tabelle nel db
	public static boolean esistonoTabelle() {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = DBHandler.getInstance().apriAndGetConnessione().createStatement();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'partecipanti'");
			resultSet.next();
			if(resultSet.getInt(1) == 0) return false;
			else return true;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.out.println(ERRORE);
			return false;
		}
		finally {
			if(resultSet != null) try {resultSet.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			DBHandler.getInstance().chiudiConnessione();
		}
	}
	
	//inserisce nella tabella "partecipanti" i dati letti da file CSV 
	private static void inserisciInTabellaPartecipantiDaCSV() throws SQLException{
		BufferedReader br = new BufferedReader(new InputStreamReader(OperazioniDatabase.class.getClassLoader().getResourceAsStream(FILE_PARTECIPANTI)));
		PreparedStatement statement = null;
		try {
			String riga = br.readLine();
			while(riga != null) {
				String[] campi = riga.split(";");
				statement = DBHandler.getInstance().getConnessione().prepareStatement(
						"INSERT INTO partecipanti (nome, sede) VALUES (?, ?)");
				statement.setString(1, campi[0]);
				statement.setString(2, campi[1]);
				statement.executeUpdate();
				logger.debug("inserito nella tabella \"partecipanti\" il record: " + campi[0] + ", " + campi[1]);
				riga = br.readLine();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.out.println(ERRORE);
		}
		finally {
			if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
		}
	}
	
	//drop delle tabelle "partecipanti" e "estrazioni"
	public static void dropTabelle() {
		if(!esistonoTabelle()) 
			System.out.println(DB_VUOTO);
		else {
			Connection connection = null;
			Statement statement = null;
			try {
				connection = DBHandler.getInstance().apriAndGetConnessione();
				connection.setAutoCommit(false);
				statement = connection.createStatement();
				statement.execute("DROP TABLE IF EXISTS partecipanti");
				statement.execute("DROP TABLE IF EXISTS estrazioni");
				connection.commit();
				logger.debug("rimosse le tabelle \"partecipanti\" e \"estrazioni\"");
				System.out.println("Rimozione di dati e tabelle effettuata correttamente");
			} catch (SQLException e) {
				logger.error(e.getMessage());
				System.out.println(ERRORE);
				try {connection.rollback();} catch (SQLException e1) {logger.error(e1.getMessage()); System.out.println(ERRORE);}
			}
			finally {
				if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
				if(connection != null) try {connection.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			}
		}
	}
	
	//estrae un partecipante e lo inserisce nella tabella "estrazioni"
	public static void estraiPartecipante() {
		if(!esistonoTabelle()) 
			System.out.println(DB_VUOTO);
		else {
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				statement = DBHandler.getInstance().apriAndGetConnessione().createStatement();
				//conta i partecipanti
				resultSet = statement.executeQuery("SELECT COUNT(*) FROM partecipanti");
				resultSet.next();
				int numeroPartecipanti = resultSet.getInt(1);
				try {resultSet.close();} catch(SQLException e) {logger.error(e.getMessage());}
				//estrae un id random tra 1 e il numero dei partecipanti
				int idEstratto = new Random().nextInt(numeroPartecipanti) + 1;
				//trova il nome associato all'id estratto e lo aggiunge alla tabella "estrazioni"
				resultSet = statement.executeQuery("SELECT nome FROM partecipanti WHERE id = " + idEstratto);
				resultSet.next();
				String nome = resultSet.getString("nome");
				logger.debug("partecipante estratto : " + nome);
				System.out.println("Partecipante estratto : " + nome);
				statement.execute("INSERT INTO estrazioni (nome_estratto, data_estrazione) VALUES ('" + nome + "', NOW())");
				logger.debug("inserito/a " + nome + " nella tabella \"estrazioni\"");
			} catch (SQLException e) {
				logger.error(e.getMessage());
				System.out.println(ERRORE);
			}
			finally {
				if(resultSet != null) try {resultSet.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
				if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
				DBHandler.getInstance().chiudiConnessione();
			}
		}
	}
	
	public static String getEstrazioniToString(int lunghezza_nome, int lunghezza_numero, int lunghezza_data) {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = DBHandler.getInstance().apriAndGetConnessione().createStatement();
			resultSet = statement.executeQuery(
					"SELECT nome_estratto, COUNT(*) AS numero_estrazioni, MAX(data_estrazione) AS data_ultima_estrazione "
							+ "FROM estrazioni "
							+ "GROUP BY nome_estratto "
							+ "ORDER BY COUNT(*) DESC");
			logger.debug("estratti record da tabella \"estrazioni\"");
			StringBuilder listaStatistiche = new StringBuilder();
			while(resultSet.next()) {
				listaStatistiche
				.append(StringUtils.rightPad(resultSet.getString("nome_estratto"), lunghezza_nome)).append(" | ")
				.append(StringUtils.leftPad(resultSet.getString("numero_estrazioni"), lunghezza_numero)).append(" | ")
				.append(StringUtils.leftPad((LocalDateTime.parse(resultSet.getTimestamp("data_ultima_estrazione").toString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))), lunghezza_data))
				.append("\n");
			}
			return new String(listaStatistiche);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.out.println(ERRORE);
			return "";
		}
		finally {
			if(resultSet != null) try {resultSet.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			if(statement != null) try {statement.close();} catch (SQLException e) {logger.error(e.getMessage()); System.out.println(ERRORE);}
			DBHandler.getInstance().chiudiConnessione();
		}
	}
	
}
