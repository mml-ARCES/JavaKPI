package sofia_kp.subscriptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import sofia_kp.Subscription;
import sofia_kp.iKPIC;
import sofia_kp.iKPIC_subscribeHandler2;

public class AutoSubscription extends Subscription {
	static SubscritpionState connection = new ConnectionState();
	protected SubscritpionState currentState;
	private final String query;
	private PrintWriter bufferedWriter;
	private final SocketAddress address;
	private iKPIC api;
	
	public AutoSubscription(Socket in_sock, iKPIC_subscribeHandler2 hand,SocketAddress address,String query,iKPIC api) throws IOException {
		super(in_sock, hand);
		this.address = address;
		this.query = query;
		this.setApi(api);
		in_sock.setSoTimeout(10000);
		setPrintWriter(new PrintWriter(new OutputStreamWriter(in_sock.getOutputStream())));
		currentState = new ConnectionState();
		
	}
	
	@Override
	public void run() {
		while(!this.isInterrupted() || currentState.equals(SubscritpionState.EXIT)){
			currentState = currentState.handle(this);
		}
	}
	// Metodi di accesso stato
	// .......//
	BufferedReader getReader(){
		return this.ft_in;
	}
	
	void setReader(BufferedReader reader){
		this.ft_in = reader;
	}
	
	Socket getSocket() {
		return ft_kpSocket;
	}

	void setSocket(Socket ft_kpSocket) {
		this.ft_kpSocket = ft_kpSocket;
	}
	
	iKPIC_subscribeHandler2 getHandler(){
		return this.f_eh;
	}

	String getQuery() {
		return query;
	}
	
	PrintWriter getPrintWriter() {
		return bufferedWriter;
	}

	void setPrintWriter(PrintWriter printWriter) {
		this.bufferedWriter = printWriter;
	}

	SocketAddress getAddress() {
		return address;
	}

	iKPIC getApi() {
		return api;
	}

	void setApi(iKPIC api) {
		this.api = api;
	}
	
	SubscritpionState createConnectionState(){
		return new ConnectionState();
	}
	
	SubscritpionState createReconnectionState(){
		return new ReconnectionState();
	}
	SubscritpionState createJoiningState(){
		return new JoiningState();
	}
	SubscritpionState ReSubscribeState(){
		return new ReSubscribeState();
	}
}
