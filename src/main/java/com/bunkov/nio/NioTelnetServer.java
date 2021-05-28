package com.bunkov.nio;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class NioTelnetServer {

	public static final String LS_COMMAND = "\tls    view all files and directories\n";
	public static final String MKDIR_COMMAND = "\tmkdir [dirname]    create directory\n";
	public static final String TOUCH_COMMAND = "\ttouch [filename]    create file\n";
	public static final String CHANGE_DIRECTORY = "\tcd [path]    change directory\n";
	public static final String RM_COMMAND = "\trm [filename | dirname]    delete file | directory\n";
	public static final String COPY_COMMAND = "\tcopy [src] [target]    copy\n";
	public static final String CAT_COMMAND = "\tcat [filename]    view context\n";
	public static final String CHANGE_NICKNAME = "\tnick [nickname]    not realized\n";
	private final Path ROOT_PATH = Path.of("./");

	private final ByteBuffer buffer = ByteBuffer.allocate(512);
	public Path path = Path.of("server");
	public NioTelnetServer() throws IOException {
		ServerSocketChannel server = ServerSocketChannel.open();
		server.bind(new InetSocketAddress(5678));
		server.configureBlocking(false);
		//OP_ACCEPT, OP_READ, OP_WRITE
		Selector selector = Selector.open();

		server.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("Server started");

		while(server.isOpen()){
			selector.select();

			var selectionKeys = selector.selectedKeys();

			var iterator = selectionKeys.iterator();

			while (iterator.hasNext()){
				var key=iterator.next();
				if(key.isAcceptable()){
					handleAccept(key, selector);
				}if(key.isReadable()){
					handleRead(key,selector);
				}
				iterator.remove();
			}


		}


	}

	private void handleRead(SelectionKey key, Selector selector) throws IOException {
		SocketChannel channel =((SocketChannel)key.channel());
		SocketAddress client = channel.getRemoteAddress();
		int readBytes = channel.read(buffer);
		if(readBytes< 0 ){
			channel.close();
			return;
		}else if(readBytes == 0){
			return;
		}
		buffer.flip();
		StringBuilder sb = new StringBuilder();
		while(buffer.hasRemaining()){
			sb.append((char) buffer.get());
		}
		buffer.clear();

		//TODO
		//touch [filename] - создание файла
		//mkdir [dirname]  - создание директории
		//cd [path] - перемещение по каталогу(.. | ~ )
		//rm [filename | dirname] - удаление файла или папки
		//copy [src] [target] -  копирование файла или папки
		//cat [filename] - просмотр содержимого
		// вывод nickname в начале строки


		sendMessage("", selector, client);
		if(key.isValid()){
			String command = sb
					.toString()
					.replace("\n", "")
					.replace("\r", "");

			String[] finalCommand = command.split(" ");

			if("--help".equals(finalCommand[0])){
				sendMessage(System.lineSeparator()+LS_COMMAND+System.lineSeparator()
						+MKDIR_COMMAND+System.lineSeparator()
						+CHANGE_NICKNAME+System.lineSeparator()
						+CAT_COMMAND+System.lineSeparator()
						+CHANGE_DIRECTORY+System.lineSeparator()
						+RM_COMMAND+System.lineSeparator()+
						TOUCH_COMMAND+System.lineSeparator()
						+COPY_COMMAND+System.lineSeparator(), selector, client);

			} else if("ls".equals(finalCommand[0])){
				sendMessage(getFileList().concat("\n"), selector, client);

			} else if("touch".equals(finalCommand[0])){
					sendMessage(fileCreating(finalCommand[1]), selector, client);

			} else if("mkdir".equals(finalCommand[0])){
				sendMessage(directoryCreating(finalCommand[1]), selector, client);

			}
			else if("rm".equals(finalCommand[0])){
				sendMessage(deleting(finalCommand[1]), selector, client);

			}
			else if("copy".equals(finalCommand[0])){
				sendMessage(copyCommand(finalCommand[1],finalCommand[2]), selector, client);

			}
			else if("cat".equals(finalCommand[0])){
				sendMessage(viewContent(finalCommand[1]), selector, client);

			}
			else if("cd".equals(finalCommand[0])){
				sendMessage(comeDirectory(finalCommand[1]), selector, client);

			}
			else if("nick".equals(finalCommand[0])){
				//Не знаю как сделать. Видел в третьем уроке, но не стал делать тут, ибо не своими силами получается.

			}else if("exit".equals(finalCommand[0])){
				System.out.println("Client logged out. IP: "+ channel.getRemoteAddress());
				channel.close();
				return;
			}
		}
	}

	private String comeDirectory(String dirPath) {
		if("..".equals(dirPath)){
			Path directoryPath = path.getParent();
			if (directoryPath == null || !directoryPath.toString().startsWith("server")) {
				return System.lineSeparator()+ "You can't go upper";
			}
			path = Path.of(directoryPath.toString());
			return "";
		}else if("~".equals(dirPath)){
			if (path == Path.of(ROOT_PATH.toString())) {
				return System.lineSeparator()+ "You are in root";
			}
			path = Path.of(ROOT_PATH.toString());
			return "";
		}else {
			Path directoryPath = Path.of(path.toString(), dirPath);
			if (Files.exists(directoryPath)) {
				path = Path.of(directoryPath.toString());
				return "";
			} else {
				return System.lineSeparator()+ "Incorrect directory name";
			}
		}
	}

	private String viewContent(String filename) throws IOException { //cat [filename]
		Path filePath = Path.of(path.toString(), filename);
		if(Files.exists(filePath)){
			if(!Files.isDirectory(filePath)){
				byte[] bytes = Files.readAllBytes(filePath);
				StringBuilder sb = new StringBuilder();
				for(byte b: bytes){
					sb.append((char)b);
				}
				return System.lineSeparator()+ sb.toString();
			}else{
				return System.lineSeparator()+"Wrong name. Use filename";
			}
		}else{
			return System.lineSeparator()+"File is not founded";
		}
	}

	private String copyCommand(String src, String target) throws IOException { //copy [src] [target]
		Path srcPath = Path.of(path.toString(), src);
		Path targetPath = Path.of(path.toString(), target);
		if(Files.exists(srcPath)){
			if(!Files.exists(targetPath)){
				if(Files.isDirectory(srcPath)){
					Files.createDirectory(targetPath);
				}else{
					Files.createFile(targetPath);
				}
			}
			if(Files.isDirectory(srcPath)&& Files.isDirectory(targetPath)){
				//
				return System.lineSeparator()+"Copy completed correctly";
			}else{
				byte[] bytes = Files.readAllBytes(srcPath);
				Files.writeString(targetPath,new String(bytes, StandardCharsets.UTF_8),StandardOpenOption.APPEND);
				return System.lineSeparator()+"Copy completed correctly";
			}
		}
		return System.lineSeparator()+ "src obj is not found";

	}

	private String deleting(String objName) throws IOException { //rm [name]
		Path deletingObjPath = Path.of(path.toString(), objName);
		if(Files.deleteIfExists(deletingObjPath)){
			return System.lineSeparator()+"Deletion completed correctly";
		}
		return System.lineSeparator()+"Name is incorrect";
	}

	private String directoryCreating(String dirname) throws IOException { //mkdir [dirname]
		Path creatingDirName = Path.of(path.toString(), dirname);
		if(!Files.exists(creatingDirName)){
			Files.createDirectory(creatingDirName);
			return System.lineSeparator()+"Directory created. Directory name: "+ dirname;
		}
		return System.lineSeparator()+"This directory is exist.";
	}

	private String fileCreating(String filename) throws IOException { //touch [filename]
		Path creatingFilePath = Path.of(path.toString(), filename);
		if(!Files.exists(creatingFilePath)){
			Files.createFile(creatingFilePath);
			return System.lineSeparator()+"File created. Filename: "+ filename;
		}
		return System.lineSeparator()+"This file is exist.";
	}

	private String getFileList() {
		return System.lineSeparator()+ String.join(" ", new File(path.toString()).list());
	}

	private void sendMessage(String message, Selector selector, SocketAddress client) throws IOException {
		message+= System.lineSeparator()+path.toString()+"\\ ";
		for(SelectionKey key: selector.keys()){
			if(key.isValid()&& key.channel() instanceof SocketChannel){
				if(((SocketChannel)key.channel()).getRemoteAddress().equals(client)){
					((SocketChannel)key.channel())
							.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
				}
			}
		}


	}

	private void handleAccept(SelectionKey key, Selector selector) throws IOException {
		SocketChannel channel =((ServerSocketChannel)key.channel()).accept();

		channel.configureBlocking(false);
		System.out.println("Client accepted. IP: "+channel.getRemoteAddress());
		channel.register(selector, SelectionKey.OP_READ, "Some attach");
		channel.write(ByteBuffer.wrap("Hello user!".getBytes(StandardCharsets.UTF_8)));
		channel.write(ByteBuffer.wrap("Enter --help for support info \n".getBytes(StandardCharsets.UTF_8)));





	}

	public static void main(String[] args) throws IOException {
		new NioTelnetServer();
	}
}
