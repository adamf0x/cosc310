package control;

import ChatBot.ChatAI;
import ChatBot.InterludeConversation;
import types.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;
public class TestRun extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	private static TestRun singleton;
	public static Scanner scn;	
	public static LinkedList<Word> wList;
	public static Word[] wListA;
	static BorderPane window = new BorderPane();
	static HBox inputArea = new HBox();
	static HBox outputArea = new HBox();
	static TextField input = new TextField();
	static TextArea output = new TextArea();
	static int count = 0;
	public static ChatAI AI;
	public static ArrayList<String> userInput = new ArrayList<>();
	public static ArrayList<String> aiOutput = new ArrayList<>();
	public static Stack<Integer> nodeNums = new Stack<Integer>();
	public void start(Stage theStage){
		//set constraints on JavaFX components and create the scene
		nodeNums.push(0);
		output.setEditable(false);
		Button submit = new Button("Submit");
		Button undo = new Button("Undo");
		submit.setMinWidth(60);
		submit.setMinHeight(50);
		undo.setMinHeight(50);
		undo.setMinWidth(60);
		input.setMinHeight(50);
		input.setMinWidth(900);
		output.setMinHeight(550);
		output.setMinWidth(1025);
		output.setLayoutX(0);
		inputArea.getChildren().add(input);
		inputArea.getChildren().add(submit);
		inputArea.getChildren().add(undo);
		inputArea.setLayoutY(550);
		outputArea.getChildren().add(output);
		window.getChildren().add(inputArea);
		window.getChildren().add(outputArea);
		theStage.setTitle("chatbot");
		theStage.setScene(new Scene(window));
		theStage.show();
		initializeSNLP();
		AI = new ChatAI();



		//handle click of the submit button
		submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				output.appendText("You: " + input.getText() + "\n\n");
				System.out.println(input.getText());
				AI.handleInput(input.getText());
				userInput.add(input.getText());
				nodeNums.push(AI.curr);
				input.clear();
				count++;
			}
		});
		//handle user using enter key to enter text
		input.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				output.appendText("You: " + input.getText() + "\n\n");
				AI.handleInput(input.getText());
				userInput.add(input.getText());
				nodeNums.push(AI.curr);
				input.clear();
				count++;
			}
		});
		/*if(AI.sn != null && AI.sn.interNode) {
			output.appendText(AI.generateResponse(""));
		}*/
		undo.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if(userInput.size() > 0 && aiOutput.size() > 0 && AI.curr > 0) {
					if(AI.sList.get(AI.curr).interNode == true) {
						output.setText(output.getText().replace("Driver: " + AI.currInternodeText + "\n",""));
					}
					int prevNodeNum = AI.curr;
					while(AI.curr == prevNodeNum && nodeNums.size() != 0) {
						AI.curr = nodeNums.pop();
					}
					if(AI.curr == prevNodeNum) {
						AI.curr = 0;
					}
					output.setText(output.getText().replace("You: " + userInput.get(userInput.size()-1) + "\n\n",""));
					userInput.remove(userInput.size()-1);
					output.setText(output.getText().replace("Driver: " + aiOutput.get(aiOutput.size()-1) + "\n",""));
					aiOutput.remove(aiOutput.size()-1);
				}
			}
		});
	}


	public static ChatAI getAI() {
		return AI;
	}

	public static void initializeSNLP() {
		InterludeConversation.init();
	}

	public static void addTextToWindow(String text) {
		output.appendText(text);
	}

	public static String clearText() {
		String rVal = output.getText();
		output.clear();
		return rVal;
	}


}
