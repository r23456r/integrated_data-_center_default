package com.idc.common.utils;

import java.io.*;

public class TextIOStreamUtils {

    public static void writeByFileWrite(String path, String string) {
        PrintStream stream = null;

        try {
            stream = new PrintStream(path);//写入的文件path

            stream.print(string);//写入的字符串


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } finally {
            stream.close();
        }
    }

    public static String readerFile(String path){
        String jsonStr = "";
        try {
            File jsonFile = new File(path);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
