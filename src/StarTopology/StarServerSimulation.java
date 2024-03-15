package StarTopology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Datagrams.Message;

import java.util.Set;

public class StarServerSimulation implements Runnable {

	private Socket connectedSocket;
	private ObjectInputStream input;
	private int connectedProcess;
	private boolean notSettedProcessNumber = true;
	
	public StarServerSimulation(Socket connected) {
		this.connectedSocket = connected;
		connectedProcess = 1;
	}
	
	@Override
	public void run() {
		
		try {
			
			input = new ObjectInputStream(connectedSocket.getInputStream());
			
			while(StarServerProcess.conexion) {
				
				Message<String> receivedMsg = (Message<String>) input.readObject();
				
				if(receivedMsg.getMessage().equalsIgnoreCase("fim")) {
					StarServerProcess.conexion = false;
					StarProcess.conexion = false;
				} else {
					if(receivedMsg.getReceiverProcess() == StarProcess.processNumber) {
						System.out.println();
						System.out.println("Mensagem recebida do P" + receivedMsg.getReferentProcess() + ": " + receivedMsg.getMessage());	
						
						StarClientSimulation.receiveds.add(receivedMsg);
					}
					
					if(receivedMsg.getReceiverProcess() == 255 && receivedMsg.getReferentProcess() != StarProcess.processNumber) {
						System.out.println();
						System.out.println("(Broadcast)Mensagem recebida do P" + receivedMsg.getReferentProcess() + ": " + receivedMsg.getMessage());	
						
						StarClientSimulation.receiveds.add(receivedMsg);
					}
					
					connectedProcess = receivedMsg.getReferentProcess(); // o conectado a ele é o emissor da msg
					
					if(connectedProcess == 1) { // n faz pro connectedProcess = 1 pra n iniciar 2 servidores na mesma porta
						notSettedProcessNumber = false;
					}
					
					if(notSettedProcessNumber && StarProcess.processNumber == 1 && connectedProcess != 1) { // processNumber = 1 quer dizer que só o servidor pode fazer isso
						Socket connectToSend = new Socket("127.0.0.1", connectedProcess + 10000); 
						ObjectOutputStream sendToOutput = new ObjectOutputStream(connectToSend.getOutputStream());
						StarServerProcess.outputs.put(connectedProcess, sendToOutput);
						notSettedProcessNumber = false;
					}
					
					if(receivedMsg.getReceiverProcess() != 1 && StarProcess.processNumber != receivedMsg.getReceiverProcess() && receivedMsg.getReceiverProcess() != 255) { 
						System.out.println();
						if(connectedProcess == 1) {
							System.out.println("Encaminhando pacote...");
						} else {
							System.out.println("Reencaminhando pacote...");	
						}
						System.out.println("Destinatário: P" + receivedMsg.getReceiverProcess());
						StarServerProcess.outputs.get(receivedMsg.getReceiverProcess()).writeObject(receivedMsg);
					}
					
					if(receivedMsg.getReceiverProcess() == 255 && StarProcess.processNumber == 1) { // processNumber = 0 quer dizer que só o servidor pode fazer isso
						for(Map.Entry<Integer, ObjectOutputStream> eachSocketProcess : StarServerProcess.outputs.entrySet()) {
							if(eachSocketProcess.getKey() != receivedMsg.getReferentProcess()) {
								eachSocketProcess.getValue().writeObject(receivedMsg);
							}
						}
					}
				}
				
			}
			
			input.close();
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
