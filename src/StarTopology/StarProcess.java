package StarTopology;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;

public class StarProcess implements Runnable {
	
	static int processNumber;
	private Socket socket;
	private ServerSocket serverSocket;
	private String name;
	static boolean conexion = true;
	
	public StarProcess(int number, String name) {
		StarProcess.processNumber = number;
		this.name = name;
		this.run();
	}
	
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		
		try {
			System.out.println("Processo P"+processNumber+" tentando conexão...");
			
			socket = new Socket(name, 10001); // nessa mesmo pq só vai ter um servidor P1
			System.out.println("P"+processNumber+" conectado.");
			
			StarClientSimulation client = new StarClientSimulation(socket);
			Thread clientThread = new Thread(client);
			clientThread.start();
			
			serverSocket = new ServerSocket(processNumber + 10000); // abrir na 10002, 10003 e 10004, pro P1 se conectar
			Socket connectedSocket = serverSocket.accept();
			StarServerSimulation server = new StarServerSimulation(connectedSocket);
			Thread serverThread = new Thread(server);
			serverThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		//new StarProcess(2, "127.0.0.2");
		//new StarProcess(3, "127.0.0.3");
		//new StarProcess(4, "127.0.0.4");
	}
	
}
