package StarTopology;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StarServerProcess implements Runnable {

	static int processNumber = 0;
	private ServerSocket socketServer;
	static boolean conexion = true;
	static Map<Integer, ObjectOutputStream> outputs;
	
	public StarServerProcess(int number) {
		StarServerProcess.processNumber = number;
		outputs = Collections.synchronizedMap(new HashMap<Integer, ObjectOutputStream>());
		this.run();
	}

	@Override
	public void run() {

		try {
			socketServer = new ServerSocket(10001); // nessa mesmo pq estrela é com um servidor só
			System.out.println("Servidor P1 iniciado...");	
			System.out.println("Pressione Enter para P1 abrir menu de envio de mensagens");
			
			Socket socket = new Socket("127.0.0.1", 10001); 
			StarProcess.processNumber = 1;
			StarClientSimulation client = new StarClientSimulation(socket);
			Thread clientThread = new Thread(client);
			clientThread.start();
			
			while(true) {
				Socket connected = socketServer.accept();
				StarServerSimulation server = new StarServerSimulation(connected);
				
				Thread serverThread = new Thread(server);
				serverThread.start();				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new StarServerProcess(1);
	}
	
}
