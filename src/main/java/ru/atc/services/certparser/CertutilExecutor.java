package ru.atc.services.certparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Date: 10.01.13
 * Time: 16:49
 */
public class CertutilExecutor {
    public static String execute(String execString) throws IOException {
        //todo???
        Process process = Runtime.getRuntime().exec(execString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("Cp1251")));
            //проверим, что команда выполнена успешно
            String s1 = reader.readLine(); //Входная длина
            String s2 = reader.readLine(); //Выходная длина || Функция EncodeToFile возвратила Файл существует. 0x80070050 (WIN32: 80)
            String s3 = reader.readLine(); //CertUtil: -encode - команда успешно выполнена. || CertUtil: -encode команда НЕ ВЫПОЛНЕНА: 0x80070050 (WIN32: 80)

            //БЕДА!!
            if(!s3.contains("успешно")) {
                String s;
                StringBuilder error = new StringBuilder();
                while((s =reader.readLine())!=null) {
                    error.append(s);
                }
                reader.close();
                throw  new IOException("Ошибка:\n" + s1 +'\n'+ s2 +'\n'+ s3 + error.toString());
            }
            reader.close();
        return null;
    }

}
