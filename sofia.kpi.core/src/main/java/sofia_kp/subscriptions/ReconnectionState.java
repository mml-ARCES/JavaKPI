package sofia_kp.subscriptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class ReconnectionState implements SubscritpionState {

	@Override
	public SubscritpionState handle(AutoSubscription context) {
		SubscritpionState nextState = this;
		System.out.println("Stato riconnessione");
		prepareNewConnection(context);
		Socket socket = context.getSocket();
		try {
			socket.connect(context.getAddress());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			context.setPrintWriter(printWriter);
			context.setReader(bufferedReader);
			nextState = context.ReSubscribeState();
		} catch (IOException e) { 
			nextState = this;
		}
		return nextState;
	}

	private void prepareNewConnection(AutoSubscription context) {
		Socket socket = context.getSocket();
		if(!socket.isClosed()){
			try {
				context.setSocket( new Socket() );
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
