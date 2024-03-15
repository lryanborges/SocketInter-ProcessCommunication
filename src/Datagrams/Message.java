package Datagrams;

import java.io.Serializable;

public class Message<T> implements Serializable {

	private int receiverProcess;
	private int referentProcess;
	private T message;
	static boolean sendingMsg = false;
	
	public Message(int rcvProcess, T msg) {
		this.receiverProcess = rcvProcess;
		this.message = msg;
	}
	
	public int getReceiverProcess() {
		return receiverProcess;
	}
	public void setReceiverProcess(int receiverProcess) {
		this.receiverProcess = receiverProcess;
	}
	public int getReferentProcess() {
		return referentProcess;
	}
	public void setReferentProcess(int referentProcess) {
		this.referentProcess = referentProcess;
	}
	public T getMessage() {
		return message;
	}
	public void setMessage(T message) {
		this.message = message;
	}
	
}
