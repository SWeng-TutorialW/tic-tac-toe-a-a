package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private char[][] field = new char[3][3];
	private int turn;
	private ServerSocket serverSocket;

	public SimpleServer(int port) throws IOException {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("join game")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				if (SubscribersList.size() == 2) {
					System.out.println("Game starting!");
					sendToAllClients("first " + startGame());
				}
				else
				{
					sendToAllClients("waiting for players " + SubscribersList.size() + "/2");
				}
				client.sendToClient("Player joined successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (msgString.startsWith("leave game")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}

		if (msgString.startsWith("place"))
		{
			turn++;
			field[msgString.charAt(msgString.length()-3)-'0'][msgString.charAt(msgString.length()-2)-'0'] = msgString.charAt(msgString.length()-1);
			sendToAllClients(msgString);

			// Check if the game should be over
			String potentialWinner = gameOver();
			if (!potentialWinner.isEmpty())
				sendToAllClients("winner " + potentialWinner);
			else if (turn == 10)
				sendToAllClients("winner S");  // Stalemate
			else
				sendToAllClients("next turn"); // No winner yet

		}

		if (msgString.startsWith("new game"))
		{
			try {
				if (SubscribersList.size() == 2) {
					System.out.println("Game starting!");
					sendToAllClients("first " + startGame());
				}
				else
				{
					sendToAllClients("waiting for players " + SubscribersList.size() + "/2");
				}
				client.sendToClient("Player joined successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Check if the game is over and return winner
	public String gameOver()
	{
		if (turn < 5)
			return "";

		// Check rows
		if (field[0][0] != ' ' && field[0][0] == field[0][1] && field[0][1] == field[0][2])
			return "00 01 02 " + field[0][0];
		if (field[1][0] != ' ' && field[1][0] == field[1][1] && field[1][1] == field[0][2])
			return "10 11 02 " + field[1][0];
		if (field[2][0] != ' ' && field[2][0] == field[2][1] && field[2][1] == field[2][2])
			return "20 21 22 " + field[2][0];

		// Check columns
		if (field[0][0] != ' ' && field[0][0] == field[1][0] && field[1][0] == field[2][0])
			return "00 10 20 " +field[0][0];
		if (field[0][1] != ' ' && field[0][1] == field[1][1] && field[1][1] == field[2][1])
			return "01 11 21 " + field[0][1];
		if (field[0][2] != ' ' && field[0][2] == field[1][2] && field[1][2] == field[2][2])
			return "02 12 22 " + field[0][2];

		// Check diagonals
		if (field[0][0] != ' ' && field[0][0] == field[1][1] && field[1][1] == field[2][2])
			return "00 11 22 " + field[0][0];
		if (field[0][2] != ' ' && field[0][2] == field[1][1] && field[1][1] == field[2][0])
			return "02 11 20 " + field[0][2];

		// Else no winner yet
		return "";
	}

	// Reset the board, assign symbols and return who starts first
	private char startGame() throws IOException {
		// Reset board
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				field[i][j] = ' ';

		turn = 1;

		// Assign symbols
		char symbol = new Random().nextBoolean() ? 'X' : 'O';
		for(SubscribedClient subscribedClient: SubscribersList) {
			subscribedClient.getClient().sendToClient("you are " + symbol);
			// Inverse symbol
			if (symbol == 'X')
				symbol = 'O';
			else
				symbol = 'X';
		}

		return new Random().nextBoolean() ? 'X' : 'O';
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("Player connected: " + client.getInetAddress());
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Player disconnected");
			sendToAllClients("winner D");
	}
}
