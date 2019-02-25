package com.company;


import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ServerCore implements Runnable
{
    private Socket incoming;


    public ServerCore(Socket incomingSocket)
    {
        incoming = incomingSocket;
    }
    public void run()
    {
        try (InputStream inStream = incoming.getInputStream();
             OutputStream outStream = incoming.getOutputStream())
        {

            Scanner in = new  Scanner(inStream, "UTF-8");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream,"UTF-8"),true);

            boolean done  = false;
            while (!done && in.hasNextLine())
            {
                //ТУТ ВЕСЬ ПОЛЕЗНЫЙ КОД
                String line = in.nextLine();
                String[] reqest = line.split("%");


                if (reqest[0].equals("auth"))
                {
                    System.out.println("Запрос на авторизацию лоигн " + reqest[1] + " Пароль " +reqest[2]);
                    if (DataBase.auth(reqest[1],reqest[2]).equals(true))
                    {
                        System.out.println("Авторизация "+reqest[1]+" прошла успешно");
                        out.println("auth_OK_TOKEN_"+ DataBase.gettoken(reqest[1],reqest[2]));

                    }
                    else
                    {
                        System.out.println("Авторизация "+reqest[1]+" не пройдена");
                        out.println("auth_ERR");
                    }

                }
                if(reqest[0].equals("reguser"))
                {
                        System.out.println("Запрос на регистрацию новго пользователя от " + DataBase.tokenname(reqest[1]));
                        if (reqest[1].equals("null"))
                        {
                            out.println("auth_ERR");
                        }
                        else
                        {
                            if(DataBase.getlevel(reqest[1]).equals("direct"))
                            {

                                System.out.println("Пользователь "+ DataBase.tokenname(reqest[1]) + " обладает достаточными правами");
//2 -name 3- login 4-pass 5-tel 6-dozh

                                DataBase.adduser(reqest[2],reqest[3],reqest[4],reqest[5],reqest[6]);
                                System.out.println("Пользоваль создан");
                                out.println("user_created");
                            }
                            else
                            {
                                System.out.println("Пользователю "+  DataBase.tokenname(reqest[1]) + " недосточно прав");
                                out.println("level_ERR");
                            }
                        }
                }
                if (reqest[0].equals("additem"))
                {
    System.out.println("Запрос на добавление товара от пользователя " + DataBase.tokenname(reqest[1]));
    if(DataBase.getlevel(reqest[1]).equals("tavar") | DataBase.getlevel(reqest[1]).equals("direct") )
    {
        System.out.println("Пользователь " + DataBase.tokenname(reqest[1]) + " имеет достаточно прав на добавление товара");

        DataBase.additem(reqest[2],reqest[3],reqest[4],reqest[5],reqest[6]);
        System.out.println("Товар добавлен");
        out.println("additem_OK");

    }
    else
        {
            System.out.println("Пользователю " +  DataBase.tokenname(reqest[1]) +" недосточно прав");
            out.println("level_ERR");
        }
    }
                if (reqest[0].equals("disc"))
                {
                    System.out.println("Запрос на отключение пользователь " + DataBase.tokenname(reqest[1]));
                    DataBase.killtoken(reqest[1],reqest[2]);
                    out.println("exit_ok");
                    out.close();
                }
                if (reqest[0].equals("viewitem"))
                {
                  System.out.println("Поступил запрос на отображение информациии о товаре   " );
                  String[] content = DataBase.getiteminfo(reqest[1]);
// 0 - id 1 - name 2 -art 3- cat 4- price 5 - col
                 out.println(content[0]+"%"+ content[1]+"%"+ content[2]+"%"+ content[3]+"%"+ content[4]+"%"+ content[5]+"%");
                }
                if (reqest[0].equals("sailitem"))
                {
                    System.out.println("Поступил запрс на продажу товара от " + DataBase.tokenname(reqest[1]));
                    if(DataBase.getlevel(reqest[1]).equals("sailer") | DataBase.getlevel(reqest[1]).equals("direct") )
                    {
                        int i = Integer.parseInt(reqest[3].trim());
                       out.println("sail_"+DataBase.sail(reqest[2], i));

                    }
                    else
                    {
                        System.out.println("У пользователя " + DataBase.tokenname(reqest[1])+ " недостаточно прав");
                        out.println("level_ERR");

                    }
                }

                if (reqest[0].equals("dropitem"))
                {
                    System.out.println("Поступил запрс на списание товара от " + DataBase.tokenname(reqest[1]));
                    if( DataBase.getlevel(reqest[1]).equals("direct") )
                    {
                        int i = Integer.parseInt(reqest[3].trim());
                        out.println("drop_"+DataBase.dropitem(reqest[2],i,reqest[4]));
                    }
                    else
                    {
                        System.out.println("У пользователя " + DataBase.tokenname(reqest[1])+ " недостаточно прав");
                        out.println("level_ERR");
                    }

                }
                if (reqest[0].equals("viewsk"))
                {
                    System.out.println("Запрос на список склада.");

                    List<String> table = DataBase.viwesklad();
                    String ans=" ";
                    for (int a= 0; a < table.size();a++) {
                        ans = ans.trim() + "%" +(table.get(a));
                    }
                    out.println(ans);
                }
                if (reqest[0].equals("viewdrop"))
                {     System.out.println("Запрос на список списания.");

                    List<String> table = DataBase.viewdrop();
                    out.println("METKA_S");
                    for (int a= 0; a < table.size();a++) {
                        out.println(table.get(a));
                    }
                    out.println("METKA_E");


                }
                if (reqest[0].equals("viewsail"))
                {
                    System.out.println("Запрос на список продаж.");

                    List<String> table = DataBase.viewsails();
                    out.println("METKA_S");
                    for (int a= 0; a < table.size();a++) {
                        out.println(table.get(a));
                    }
                    out.println("METKA_E");

                }
                if (reqest[0].equals("getname"))
                {
                    out.println(DataBase.tokenname(reqest[1]));
                }
}
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SQLException x)
        {
            x.printStackTrace();
        }
    }
}
