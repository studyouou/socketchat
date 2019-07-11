package org.ougen;

import sun.reflect.generics.scope.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Author: OuGen
 * Discription:
 * Date: 17:18 2019/7/11
 */
public class Server extends Thread{
    private ServerSocket serverSocket;
    private ArrayList<Socket> socket_list = new ArrayList<>();
    private LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
        new SendMsg().start();
        while (true){
            try {
                Socket socket = serverSocket.accept();
                new ReciveMsg(socket).start();
                socket_list.add(socket);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("hi  i  am your socket".getBytes());
//                socket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        new Server(new ServerSocket(1111)).start();
    }
    class SendMsg extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    String msg = blockingQueue.take();
                    for (Socket socket : socket_list){
                        System.out.println("服务器发送消息"+msg+"给"+socket.getPort());
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write((socket.getLocalAddress().getHostName()+" "+socket.getPort()+":"+msg).getBytes());
                    }
                } catch (InterruptedException e) {
                    System.out.println("服务被打断");
                } catch (IOException e) {
                    System.out.println("获取异常");
                }
            }

        }
    }
    class ReciveMsg extends Thread {
        private Socket socket;
        public ReciveMsg(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            int len = 0;
            byte[] buf = new byte[1024];
            while (true) {
                try {
                    InputStream inputStream = socket.getInputStream();
                    while ((len=inputStream.read(buf))>0){
                        blockingQueue.offer(new String(buf));
                    }
              } catch (IOException e) {
                    System.out.println("有人退出群聊");
                    return;
                }

            }
        }
    }
}
