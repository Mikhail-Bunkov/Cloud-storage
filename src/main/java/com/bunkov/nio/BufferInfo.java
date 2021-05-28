package com.bunkov.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class BufferInfo {
	public static void main(String[] args) throws IOException {
//		FileChannel channel = new RandomAccessFile("server"+ File.separator+"1.txt","rw").getChannel();
//		ByteBuffer buffer = ByteBuffer.allocate(20);
//		channel.read(buffer);
//		buffer.flip();
////		while(buffer.hasRemaining()){
////			System.out.print((char) buffer.get());
////		}
//		System.out.println(buffer);
//
//		byte[] byteBuf = new byte[20];
//		int pos = 0;
//		while (buffer.hasRemaining()){
//			byteBuf[pos++] = buffer.get();
//		}
//		System.out.println(new String(byteBuf, StandardCharsets.UTF_8));
//
//		System.out.println("Buffer sample");

		ByteBuffer b1 = ByteBuffer.allocate(5);
		System.out.println(b1);
		b1.put((byte)1);
		b1.put((byte)2);
		b1.put((byte)3);
		System.out.println(b1);
		b1.flip();
		System.out.println(b1);




	}
}
