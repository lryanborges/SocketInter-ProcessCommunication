package RingTopology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Datagrams.Message;

public class ServerSimulation implements Runnable {

	ServerSocket serverSocket;
	ObjectInputStream input;
	ClientSimulation clientSimulation;
	
	public ServerSimulation(ServerSocket server, ClientSimulation client) {
		this.serverSocket = server;
		this.clientSimulation = client;
	}
	
	@Override
	public void run() {
		
		try {
			Socket connectedSocket = serverSocket.accept();
			input = new ObjectInputStream(connectedSocket.getInputStream());
			
			while(Process.conexion) {
				Message<String> receivedMsg = (Message<String>) input.readObject();
				
				if(receivedMsg.getMessage().equalsIgnoreCase("fim")) {
					Process.conexion = false;
				} else {
					if(receivedMsg.getReceiverProcess() == Process.processNumber) {
						System.out.println();
						System.out.println("Mensagem recebida do P" + receivedMsg.getReferentProcess() + ": " + receivedMsg.getMessage());
						Process.receiveds.add(receivedMsg); // add no log de recebidas
					} else if(receivedMsg.getReceiverProcess() != 255){ // elseif pra evitar que fique loop infinito se for broadcast
						System.out.println("Reencaminhando pacote...");
						clientSimulation.output.writeObject(receivedMsg);
					}
					
					// caso seja via broadcast
					if(receivedMsg.getReceiverProcess() == 255 && receivedMsg.getReferentProcess() != Process.processNumber) { 
						System.out.println("(Broadcast)Mensagem recebida do P" + receivedMsg.getReferentProcess() + ": " + receivedMsg.getMessage());
						//System.out.println("Reencaminhando pacote..."); 
						clientSimulation.output.writeObject(receivedMsg);
						Process.receiveds.add(receivedMsg); // add no log de recebidas
					}	
				}
				
			}
			
			input.close();
			serverSocket.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
