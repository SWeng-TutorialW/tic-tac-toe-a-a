package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	public char symbol;
	public boolean isMyTurn;

	private SimpleClient(String host, int port) {
		super(host, port);
		symbol = ' ';
		isMyTurn = false;
	}

	@Override
	protected void handleMessageFromServer(Object msg)
	{
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
			return;
		}

		String msgString = msg.toString();

		if (msgString.startsWith("next turn"))
		{
			isMyTurn = !isMyTurn;
			PrimaryController.makeMove();
		}

		else if(msgString.startsWith("place")) {
			msgString = msgString.replaceFirst("^\\w+\\s*", "");
			PrimaryController.registerInput(msgString);
		}

		else if (msgString.startsWith("first"))
		{
			if (msgString.endsWith("" + symbol)) {
				isMyTurn = true;
			}
			else {
				isMyTurn = false;
			}
			PrimaryController.startGame();
			PrimaryController.makeMove();
		}

		else if (msgString.startsWith("waiting for player"))
		{
			isMyTurn = false;
			PrimaryController.makeMove();
			PrimaryController.updateDisplay("Waiting for player");
		}

		else if (msgString.startsWith("you are"))
		{
			symbol = msgString.charAt(msgString.length()-1);
		}

		else if (msgString.startsWith("winner"))
		{
			isMyTurn = false;
			PrimaryController.gameOver(msgString);
		}


	}

	@Override
	protected void connectionEstablished()
	{
		System.out.println("Successfully joined game");
	}

	@Override
	protected void connectionClosed()
	{
		System.out.println("User disconnected");
	}

	@Override
	protected void connectionException(Exception exception)
	{
		System.out.println("Error: " + exception.getMessage());
	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
