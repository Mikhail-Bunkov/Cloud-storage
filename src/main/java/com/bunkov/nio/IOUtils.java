package com.bunkov.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

public class IOUtils {
	public static void main(String[] args) throws IOException, InterruptedException {
		Path testPath = Path.of("./");//Если не указывать то будет корень проекта
		System.out.println(testPath);

		Path path1 = Path.of("server", "1.txt");

		String root = "server";
		Path dirPath = Path.of(root,"1.txt");
		//testPath.toAbsolutePath().iterator().forEachRemaining(System.out::println );


//		WatchService service = FileSystems.getDefault().newWatchService();
//		testPath.register(service,
//				StandardWatchEventKinds.ENTRY_CREATE,
//				StandardWatchEventKinds.ENTRY_MODIFY,
//				StandardWatchEventKinds.ENTRY_DELETE
//				);
//		WatchKey key;
//		String notification = "Event type: %s. File: %s\n";
//		while((key = service.take())!=null){
//			for(WatchEvent event: key.pollEvents()){
//				System.out.printf(notification, event.kind(), event.context());
//			}
//			key.reset();
//		}

//		new Thread(()->{
//			String notification = "Event type: %s. File: %s\n";
//			while(true){
//				try{
//					var key = service.take();
//					if(key.isValid()){
//						var events = key.pollEvents();
//						for(WatchEvent<?> event : events){
//							System.out.printf(notification, event.kind(), event.context());
//						}
//						key.reset();
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();

//		System.out.println("1.txt exists: "+ Files.exists(dirPath));
//
//		Path newPath = Path.of("server","newfile.txt");
//		if(!Files.exists(newPath)){
//			Files.createFile(newPath);
//		}
//		Files.writeString(newPath,"NEW String", StandardOpenOption.WRITE);
//		Files.writeString(newPath,"NEW String", StandardOpenOption.APPEND);
//		Files.delete(newPath);
//		Files.createDirectories(Path.of("server","dir4","dir5","dir7"));

//		Files.walkFileTree(testPath, new FileVisitor<Path>() {
//			@Override
//			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//				System.out.println("pre - "+ dir.getFileName());
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//				System.out.println("visit file - "+ file.getFileName());
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//				System.out.println("visit file failed - "+ file.getFileName());
//				return FileVisitResult.TERMINATE;
//			}
//
//			@Override
//			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//				System.out.println("post - "+dir.getFileName());
//				return FileVisitResult.CONTINUE;
//			}
//		});

		//search

//		Files.walkFileTree(Path.of(""), new SimpleFileVisitor<>(){
//			@Override
//			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//				if("1.txt".equals(file.getFileName().toString())){
//					System.out.println(file.getFileName()+ " id founded. Path: "+ file.toAbsolutePath());
//					return  FileVisitResult.TERMINATE;
//				}
//				return FileVisitResult.CONTINUE;
//			}
//		});
//
//		Files.find(Path.of(""), 10, new BiPredicate<Path, BasicFileAttributes>() {
//			@Override
//			public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
//				return "1.txt".equals(path.getFileName().toString());
//			}
//		}).forEach(System.out::println);

		//reading
		Path serverPath = Path.of("server","1.txt");
		Files.readAllLines(serverPath).stream().forEach(System.out::println);
		System.out.println("____________________________________________________");
		Files.newBufferedReader(serverPath).lines().forEach(System.out::println);
		System.out.println("____________________________________________________");
		byte[] bytes = Files.readAllBytes(serverPath);
		for(byte b: bytes){
			System.out.print((char)b);
		}

	}
}
