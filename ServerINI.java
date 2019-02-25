package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerINI extends Thread
{

    public void run()
    {
        try (ServerSocket s = new ServerSocket(Cfg.SERVERPORT))
        {
            int i = 1;

            while (true)
            {
                Socket incoming = s.accept();
                System.out.println("Новое подключение # " + i);
                Runnable r = new ServerCore(incoming);
                Thread t = new Thread(r);
                t.start();
                i++;
            }

        }
        catch (IOException x )
        {
            x.printStackTrace();
        }

    }

}
