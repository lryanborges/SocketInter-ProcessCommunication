package StarTopology;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Datagrams.Message;


public class StarClientSimulation implements Runnable {

	private Socket socket;
	private ObjectOutputStream output;
	static List<Message<String>> sends;
	static List<Message<String>> receiveds;
	
	public StarClientSimulation(Socket client) {
		this.socket = client;
		sends = new ArrayList<Message<String>>();
		receiveds = new ArrayList<Message<String>>();
	}
	
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		int receiverProcess = 0;
		
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			
			// msg enviada pra p1 pra reconhecimento dos processos conectados a ele
			Message<String> acknowledgment = new Message<String>(1, "Reconhecimento...");
			acknowledgment.setReferentProcess(StarProcess.processNumber);
			output.writeObject(acknowledgment);
			
			while(StarProcess.conexion) {
				
				if(StarProcess.processNumber == 1) {
					scan.nextLine();	
				}
				
				System.out.println("--------------------------------------");
				System.out.println("PROCESSO " + StarProcess.processNumber);
				System.out.println("--------------------------------------");
				System.out.println("[1] - Enviar mensagem ");
				System.out.println("[2] - Visualizar mensagens enviadas");
				System.out.println("[3] - Visualizar mensagens recebidas");
				System.out.println("--------------------------------------");
				int opc = 0;
				
				do {
					try {
						opc = Integer.parseInt(scan.nextLine());
					} catch(NumberFormatException e) {
						System.out.println("Entrada inválida! Digite um número inteiro.");
					}
				} while(opc == 0);
				
				switch(opc) {
				case 1:
					for(int i = 1; i <= 4; i++) {
						if(i != StarProcess.processNumber) {
							System.out.println("["+i+"] - Processo " + i);	
						}
					}
					System.out.println("[255] - Broadcast");
					
					do {
						try {
							System.out.print("Enviar mensagem para o processo: ");
							receiverProcess = Integer.parseInt(scan.nextLine());
						} catch(NumberFormatException e) {
							System.out.println("Entrada inválida! Digite um número inteiro.");
						}
					} while((receiverProcess < 1 || receiverProcess > 4) && receiverProcess != 255);

					System.out.print("Digite sua mensagem: ");
					String msg = scan.nextLine();
					
					if(msg.equalsIgnoreCase("fim")){
						StarProcess.conexion = false;
					}
		
					Message<String> messageToSend = new Message<String>(receiverProcess, msg);
					messageToSend.setReferentProcess(StarProcess.processNumber);
					output.writeObject(messageToSend);
					output.flush();
					sends.add(messageToSend); // aqui é pra add as mensagens enviadas num log
					receiverProcess = 0; // aqui é pra setar pra próxima vez q executar poder tratar erros na leitura de qm recebe
					
					break;
				case 2:
					System.out.println("--------------------------------------");
					System.out.println("Mensagens enviadas");
					for(Message<String> send : sends) {
						if(send.getReceiverProcess() == 255) {
							System.out.print("P"+send.getReferentProcess()+" pra Todos: ");
						} else {
							System.out.print("P"+send.getReferentProcess()+" pra P"+send.getReceiverProcess()+": ");
						}
						System.out.println(send.getMessage());
					}
					break;
				case 3:
					System.out.println("--------------------------------------");
					System.out.println("Mensagens recebidas");
					for(Message<String> received : receiveds) {
						if(received.getReceiverProcess() == 255) {
							System.out.print("P"+received.getReferentProcess()+" pra Todos: ");
						} else {
							System.out.print("P"+received.getReferentProcess()+" pra P"+received.getReceiverProcess()+": ");
						}
						System.out.println(received.getMessage());
					}
					break;
				default:
					System.out.println("Opção inválida.");
				}
				
				
			}
			
			scan.close();
			output.close();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
