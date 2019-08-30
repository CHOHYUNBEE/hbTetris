package com.hbTetris;

import java.io.*;

public class FileMake {
    public static void fileWrite(String name, int score)      // 파일저장
    {
        try
        {
            DataOutputStream out = new DataOutputStream(new FileOutputStream("input.txt", true));

            out.writeBytes(name+" : "+score+"\n");
            out.flush();
            out.close();
        }catch(Exception e){ }
    }

    public static void fileRead()         // 파일에서 데이터를 읽어오는 부분
    {
        try
        {
            BufferedReader in = new BufferedReader (new InputStreamReader(new FileInputStream("input.txt")));
            String s;

            do
            {
                s = in.readLine();
                System.out.println(s);
            }while(s!=null);
            in.close();
        }catch(Exception e){ }
    }
}
