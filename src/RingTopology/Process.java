package RingTopology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import Datagrams.Message;

public class Process implements Runnable {
	
	static int processNumber;
	private ServerSocket socketServer;
	private Socket socket;
	private String name;
	static boolean conexion = true;
	static List<Message<String>> sends;
	static List<Message<String>> receiveds;
	
	public Process(int number, String nome) {
		Process.processNumber = number;
		this.name = nome;
		sends = new ArrayList<Message<String>>();
		receiveds = new ArrayList<Message<String>>();
		this.run();
	}
	
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		
		try {
			
			socketServer = new ServerSocket(processNumber + 10000);
			int socketServerToConnect = (processNumber % 4) + 1; // faz a porta 1 se conectar com a 2, ... e a 4 com a 1 (pelo modulo)
			
			System.out.println("Servidor " + processNumber + " iniciado...");
			System.out.print("Pressione enter pra iniciar o cliente.");
			
			scan.nextLine(); // ponto de parada pra todos os servidores ligarem antes de um socket tentar se conectar
			
			socket = new Socket(name, socketServerToConnect + 10000);
			System.out.println("Processo " + processNumber + " iniciado.");
			
			ClientSimulation clientSimulation = new ClientSimulation(socket);
			Thread clientThread = new Thread(clientSimulation);
			clientThread.start();
			
			ServerSimulation serverSimulation = new ServerSimulation(socketServer, clientSimulation);
			Thread serverThread = new Thread(serverSimulation);
			serverThread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//new Process(1, "127.0.0.1");
		//new Process(2, "127.0.0.2");
		//new Process(3, "127.0.0.3");
		//new Process(4, "127.0.0.4");
	}
	
}
