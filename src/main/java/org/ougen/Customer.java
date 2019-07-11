package org.ougen;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;


/**
 * Author: OuGen
 * Discription:
 * Date: 17:12 2019/7/11
 */
public class Customer {
    public static void main(String[] args) {
        try {
            new Customer().getStartToChat();
        } catch (IOException e) {
            throw new RuntimeException("出现错误");
        }
    }

    private void getStartToChat() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",1111));
        new SendMsg(socket).start();
        new ReciveMsg(socket).start();
    }

    private static class ReciveMsg extends Thread {
        private Socket socket;
        public ReciveMsg(Socket socket){
            this.socket=socket;
        }
        @Override
        public void run() {
            while (true){
                try {
                    InputStream inputStream = socket.getInputStream();
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while ((len = inputStream.read(buf))>0){
                        System.out.println(new String(buf));
                        buf = new byte[1024];
                    }
                } catch (IOException e) {
                    throw new RuntimeException("连接异常");
                }
            }
        }
    }

    private static class SendMsg extends Thread {
        private Socket socket;
        public SendMsg(Socket socket){
            this.socket=socket;
        }
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true){
                String msg = scanner.nextLine();
                try {
                    //这里发送的消息自己也会接受到
                    if ("over".equals(msg)) break;
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(msg.getBytes());
//                    socket.shutdownOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
