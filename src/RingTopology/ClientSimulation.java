package RingTopology;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import Datagrams.Message;

public class ClientSimulation implements Runnable {

	Socket socket;
	ObjectOutputStream output;
	
	public ClientSimulation(Socket client) {
		this.socket = client;
	}
	
	@Override
	public void run() {

		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			Scanner scan = new Scanner(System.in);
			int receiverProcess = 0;
			
			while(Process.conexion) {
				System.out.println("--------------------------------------");
				System.out.println("PROCESSO " + Process.processNumber);
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

				switch (opc) {
				case 1:
				
					for(int i = 1; i <= 4; i++) {
						if(i != Process.processNumber) {
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
						Process.conexion = false;
					}
					
					Message<String> messageToSend = new Message(receiverProcess, msg);
					messageToSend.setReferentProcess(Process.processNumber);
					output.writeObject(messageToSend);
					output.flush();
					Process.sends.add(messageToSend); // aqui é pra add as mensagens enviadas num log
					receiverProcess = 0; // aqui é pra setar pra próxima vez q executar poder tratar erros na leitura de qm recebe
					
					break;
				case 2:
					System.out.println("--------------------------------------");
					System.out.println("Mensagens enviadas");
					for(Message<String> send : Process.sends) {
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
					for(Message<String> received : Process.receiveds) {
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
			
			output.close();
			scan.close();
			socket.close();
			
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
