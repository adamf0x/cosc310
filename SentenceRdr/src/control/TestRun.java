package control;

import ChatBot.ChatAI;

import types.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class TestRun extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	public static Scanner scn;	
	public static LinkedList<Word> wList;
	public static Word[] wListA;
	static BorderPane window = new BorderPane();
	static HBox inputArea = new HBox();
	static HBox outputArea = new HBox();
	static TextField input = new TextField();
	static TextArea output = new TextArea();
	static int count = 0;

	public void start(Stage theStage){
		//set constraints on JavaFX components and create the scene
		output.setEditable(false);
		Button submit = new Button("Submit");
		submit.setMinWidth(100);
		submit.setMinHeight(50);
		input.setMinHeight(50);
		input.setMinWidth(925);
		output.setMinHeight(550);
		output.setMinWidth(1025);
		output.setLayoutX(0);
		inputArea.getChildren().add(input);
		inputArea.getChildren().add(submit);
		inputArea.setLayoutY(550);
		outputArea.getChildren().add(output);
		window.getChildren().add(inputArea);
		window.getChildren().add(outputArea);
		theStage.setTitle("chatbot");
		theStage.setScene(new Scene(window));
		theStage.show();
		ChatAI AI = new ChatAI();
		
		//handle click of the submit button
		submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
		public void handle(MouseEvent arg0) {
				output.appendText("You : " + input.getText() + "\n\n");
				System.out.println(input.getText());
				output.appendText("Driver: " + AI.generateResponse(input.getText())  + "\n\n");
				input.clear();
				count++;
			}
		});
		//handle user using enter key to enter text
		input.setOnKeyPressed(e -> {
		    if (e.getCode() == KeyCode.ENTER) {
		    	output.appendText("You : " + input.getText() + "\n\n");
				output.appendText("Driver: " + AI.generateResponse(input.getText())  + "\n\n");
				input.clear();
				count++;
		    }
		});
		if(AI.sn != null && AI.sn.interNode) {
			output.appendText(AI.generateResponse(""));
		}
	}
	

}
