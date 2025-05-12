/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class PrimaryController {
	private static PrimaryController controller;
	private Button[] buttons;

	@FXML // fx:id="bottomleft"
	public Button bottomleft; // Value injected by FXMLLoader

	@FXML // fx:id="bottommiddle"
	public Button bottommiddle; // Value injected by FXMLLoader

	@FXML // fx:id="bottomright"
	public Button bottomright; // Value injected by FXMLLoader

	@FXML // fx:id="center"
	public Button center; // Value injected by FXMLLoader

	@FXML // fx:id="display"
	public TextField display; // Value injected by FXMLLoader

	@FXML // fx:id="middleleft"
	public Button middleleft; // Value injected by FXMLLoader

	@FXML // fx:id="middleright"
	public Button middleright; // Value injected by FXMLLoader

	@FXML // fx:id="topleft"
	public Button topleft; // Value injected by FXMLLoader

	@FXML // fx:id="topmiddle"
	public Button topmiddle; // Value injected by FXMLLoader

	@FXML // fx:id="topright"
	public Button topright; // Value injected by FXMLLoader

	@FXML
	private Text youare;

	@FXML
	private Button newgame;

	@FXML
	void handleBottomLeft(ActionEvent event)
	{
		try {
			SimpleClient.getClient().sendToServer("place 20" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleBottomMiddle(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 21" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleBottomRight(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 22" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleCenter(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 11" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleMiddleLeft(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 10" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleMiddleRight(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 12" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleTopLeft(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 00" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleTopMiddle(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 01" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void handleTopRight(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("place 02" + SimpleClient.getClient().symbol);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void newGame(ActionEvent event)
	{
        try {
            SimpleClient.getClient().sendToServer("new game");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	public static void startGame()
	{
		controller.youare.setText("You are\n" + SimpleClient.getClient().symbol);
		controller.youare.setVisible(true);
		controller.newgame.setVisible(false);
		for (Button button : controller.buttons)
		{
			button.setText("");
			button.setDisable(true);
			button.setStyle("");
		}
    }

	@FXML
	void sendWarning(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("#warning");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void initialize(){
		controller = this;
		newgame.setVisible(false);
		youare.setVisible(false);
		try {
			SimpleClient.getClient().sendToServer("join game");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		buttons = new Button[]{bottomleft, bottommiddle, bottomright, middleleft, center, middleright, topleft, topmiddle, topright};
	}

	public static void updateDisplay(String msg)
	{
		controller.display.setText(msg);
	}

	// If it's your turn, choose what to do
	public static void makeMove()
	{
		// Your turn
		if (SimpleClient.getClient().isMyTurn) {
			controller.display.setText("Your turn");
			for (Button button : controller.buttons)
				if (button.getText().isEmpty())
						Platform.runLater(() -> button.setDisable(false));
		}

		// Not your turn
		else {
			controller.display.setText("Opponent's turn");
			for (Button button : controller.buttons)
				button.setDisable(true);
		}

	}

	// Register input
	public static void registerInput(String posTxt)
	{
		int pos = getPos(posTxt);
		char symbol = posTxt.charAt(posTxt.length()-1);
		Platform.runLater(() -> {
			controller.buttons[pos-1].setText(symbol + "");

			if (symbol == SimpleClient.getClient().symbol)
				controller.buttons[pos-1].setStyle("-fx-text-fill: darkgreen;");
			else
				controller.buttons[pos-1].setStyle("-fx-text-fill: darkred;");

			controller.buttons[pos-1].setDisable(true);
		});
	}

	// Game is over
	public static void gameOver(String winner)
	{
		String[] winningBlocks = winner.split(" ");
		Platform.runLater(() -> {
			if (winner.charAt(winner.length()-1) == SimpleClient.getClient().symbol) {
				PrimaryController.updateDisplay("You won :)");
				controller.buttons[getPos(winningBlocks[1])-1].setStyle("-fx-background-color: green;");
				controller.buttons[getPos(winningBlocks[2])-1].setStyle("-fx-background-color: green;");
				controller.buttons[getPos(winningBlocks[3])-1].setStyle("-fx-background-color: green;");
			}
			else if (winner.charAt(winner.length()-1) == 'S')
				PrimaryController.updateDisplay("Stalemate :(");
			else if (winner.charAt(winner.length()-1) == 'D')
				PrimaryController.updateDisplay("Opponent disconnected");
			else {
				PrimaryController.updateDisplay("You lost ;(");
				controller.buttons[getPos(winningBlocks[1])-1].setStyle("-fx-background-color: red;");
				controller.buttons[getPos(winningBlocks[2])-1].setStyle("-fx-background-color: red;");
				controller.buttons[getPos(winningBlocks[3])-1].setStyle("-fx-background-color: red;");
			}

			for (Button button : controller.buttons)
				button.setDisable(true);

			controller.newgame.setVisible(true);
			controller.youare.setVisible(false);

			controller.newgame.setDisable(true);
			PauseTransition delay = new PauseTransition(Duration.seconds(3));
			delay.setOnFinished(event -> controller.newgame.setDisable(false));  // Re-enable after delay
			delay.play();});
	}

	private static int getPos(String posTxt)
	{
		if (posTxt.startsWith("place"))
			posTxt = posTxt.replaceFirst("^\\w+\\s*", "");
		int pos = switch (posTxt.charAt(0)) {
			case '0' -> 7;
			case '1' -> 4;
			default -> 1;
		};
		pos += posTxt.charAt(1) - '0';
		return pos;
	}
}