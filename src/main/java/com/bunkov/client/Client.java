package com.bunkov.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;


/**
 * FX client - file storage
 * Client commands: upload filename | download filename
 */

public class Client {
	private final Socket socket;
	private final DataOutputStream out;
	private final DataInputStream in;
	@FXML
	public TextField commandField;

	public Client() throws IOException {
		socket = new Socket("localhost", 6788);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());


	}

//	public void send(ActionEvent actionEvent) {
//		String message = commandField.getText();
//		try {
//			out.writeUTF(message);
//			String command = in.readUTF();
////			if("done".equalsIgnoreCase(command)){
////
////			}
//			System.out.println(command);
//		}catch (EOFException e){
//			System.err.println("Reading command err from "+ socket.getInetAddress());
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public void doCommand(ActionEvent actionEvent) {
		String[] cmd = commandField.getText().split(" ");

		if("upload".equals(cmd[0])){
			sendFile(cmd[1]);
		}else if("download".equals(cmd[0])){
			getFile(cmd[1]);
		}else{
			//sendMessage(commandField.getText());
		}
	}

	private void getFile(String filename) {
		//homework
	}

	private void sendFile(String filename) {
		try{
			File file = new File("client/" + filename);
			if(!file.exists()){
				throw new FileNotFoundException();
			}
			long fileLength = file.length();
			FileInputStream fis = new FileInputStream(file);
			out.writeUTF("upload");
			out.writeUTF(filename);
			out.writeLong(fileLength);

			int read = 0;
			byte[] buffer = new byte[8*1024];
			while((read = fis.read(buffer)) !=-1){
				out.write(buffer,0,read);
			}
			out.flush();
			String status = in.readUTF();
			System.out.println("Sending status: " + status);
		}catch (FileNotFoundException e){
			System.err.println("File not found - /client/"+filename);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
