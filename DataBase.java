package com.company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataBase {
    private static final String url = "jdbc:mysql://" + Cfg.DBIP + ":3306/" + Cfg.DBNAME + "" + "?verifyServerCertificate=false" +
            "&useSSL=false" +
            "&requireSSL=false" +
            "&useLegacyDatetimeCode=false" +
            "&amp" +
            "&serverTimezone=UTC";
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static Boolean auth(String login, String pass)
    {
        String query = "SELECT pass FROM users WHERE login='" + login + "';";
        Boolean answer = false;

        try {
            con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String count = rs.getString(1);
                if (count.equals(pass)) {
                    answer = true;
                }
            }


        } catch (SQLException x) {
            x.printStackTrace();

        }
        return answer;

    }
    public static String gettoken(String login, String pass) throws SQLException
    {
        String token;

        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefgihjklmnopqrstuvwxyz";
        String nums = "0123456789";
        String passSymbols = charsCaps + nums;
        Random rnd = new Random();

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            if (i % 5 == 0 && i > 0) {
                sb.append('-');
            }
            sb.append(passSymbols.charAt(rnd.nextInt(passSymbols.length())));
        }
        token = sb.toString();
        String query = "UPDATE users SET token = '" + token + "' WHERE login = '" + login + "' and pass = '" + pass + "';";
        stmt.executeUpdate(query);
        System.out.println("Токен " + token + " выдан пользователю " + login);
        return token;


    }
    public static void adduser(String name, String login, String pass, String tel, String dozh ) throws SQLException
    {
        String query = "INSERT INTO `sportmag`.`users` (`name`, `login`, `pass`, `tel`, `token`, `dozh`) VALUES ('"+name+"', '"+login+"', '"+pass+"', '"+tel+"', '11', '"+dozh+"');";
        stmt.executeUpdate(query);
    }
    public static String tokenname(String token) throws SQLException
    {
        String query = "SELECT name FROM users WHERE token='" + token + "';";

        String name = "null";
        con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        while (rs.next()) {
            String count = rs.getString(1);
            name = count;
        }


        return name;
    }
    public static String getlevel(String token) throws SQLException
    {
        String query = "SELECT dozh FROM users WHERE token='" + token + "';";

        String level = "null";
        con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        while (rs.next()) {
            String count = rs.getString(1);
            level = count;
        }


        return level;


    }
    public static void additem(String name, String art,  String categ, String coll, String price) throws SQLException
    {
        String query = "INSERT INTO `items` (`name`, `art`, `cat`, `price`, `col`) VALUES ('"+name+"', '"+art+"', '"+categ+"', '"+price+"', '"+coll+"');";

        stmt.executeUpdate(query);
    }
    public static void killtoken( String login, String pass) throws SQLException
    {
        String query = "UPDATE users SET token = 'NA' WHERE login = '" + login + "' and pass = '" + pass + "';";
        stmt.executeUpdate(query);
    }
    public static String[] getiteminfo (String art) throws SQLException
    {
        String[] info = new String[6];
       String query =  "SELECT * FROM items WHERE art ='"+art+"';";
        con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);

        while (rs.next())
        {
            info[0] = rs.getString(1);
            info[1] = rs.getString(2);
            info[2] = rs.getString(3);
            info[3] = rs.getString(4);
            info[4] = rs.getString(5);
            info[5] = rs.getString(6);
        }

        return info ;

    }
    public static boolean sail(String art, int colf) throws SQLException
    {

        String colvo =  getiteminfo(art)[5];
        int col1 = Integer.parseInt(colvo);
        if (col1 > 0) {
            col1 = col1 - colf;
            String query = "UPDATE items SET col = '" + col1 + "' WHERE art = '" + art + "';";
            stmt.executeUpdate(query);
            sailacc(art, colf, getiteminfo(art)[4], getiteminfo(art)[1]);
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void sailacc(String art, int col, String price, String name) throws SQLException
    {
        String query = "INSERT INTO `sportmag`.`sail` ( `art`, `price`, `col`, `name`) VALUES ('"+art+"', '"+price+"', '"+col+"', '"+name+"');";
        stmt.executeUpdate(query);

    }

    public static boolean dropitem (String art, int col, String rizon) throws SQLException
    {

        String colvo =  getiteminfo(art)[5];
        int col1 = Integer.parseInt(colvo);
        if (col1 > 0) {
            col1 = col1 - col;
            String query = "UPDATE items SET col = '" + col1 + "' WHERE art = '" + art + "';";
            stmt.executeUpdate(query);
            dropitemac(art, col, getiteminfo(art)[1], rizon);
            return true;
        }
        else
        {
            return false;
        }

    }
    public static void dropitemac(String art, int col, String name, String rizon) throws SQLException
    {
    String query = "INSERT INTO `sportmag`.`drops` (`name`, `art`, `col`, `rizen`) VALUES ('"+name+"', '"+art+"', '"+col+"', '"+rizon+"');";
    stmt.executeUpdate(query);

    }

    public static List<String> viwesklad() throws SQLException
    {

        ArrayList <String>  tabl =  new ArrayList();
        String query =  "SELECT * FROM items;";
        con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        String tab;

        while (rs.next())
        {
            tab = rs.getString(1) +"|"+rs.getString(2) +"|"+rs.getString(3) +"|"+ rs.getString(4) + "|" + rs.getString(5)+"|"+rs.getString(6);
            tabl.add(tab);
        }

return tabl;
        }

        public static List<String> viewdrop() throws SQLException
        {
            ArrayList <String>  tabl =  new ArrayList();
            String query =  "SELECT * FROM drops;";
            con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            String tab;

            while (rs.next())
            {
                tab = rs.getString(1) +"|"+rs.getString(2) +"|"+rs.getString(3) +"|"+ rs.getString(4) + "|" + rs.getString(5);
                tabl.add(tab);
            }

            return tabl;
        }

        public static List<String> viewsails() throws SQLException
        {

            ArrayList <String>  tabl =  new ArrayList();
            String query =  "SELECT * FROM sail;";
            con = DriverManager.getConnection(url, Cfg.DBUSER, Cfg.DBPASS);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            String tab;

            while (rs.next())
            {
                tab = rs.getString(1) +"|"+rs.getString(2) +"|"+rs.getString(3) +"|"+ rs.getString(4) + "|" + rs.getString(5);
                tabl.add(tab);
            }

            return tabl;
        }
    }












