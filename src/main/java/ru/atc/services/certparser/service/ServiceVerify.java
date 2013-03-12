package ru.atc.services.certparser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.atc.services.certparser.config.Configuration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Посылает сертификат в Base64 на сервис проверки сертификатов
 * //todo заменить использование apache http client на базовую библиотеку java
 */
public class ServiceVerify {
    public static final File base64encodedCertificate = new File("base64encodedCert.cer");

    public static Logger log = LoggerFactory.getLogger("certificateparser.service");
    public final URL url;
    /**ответ от сервиса проверки сертификатов<br/>
     * "0" - все ОК*/
    private String response;

    public static String firstPart = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esv=\"http://esv.server.rt.ru\">\n" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<esv:VerifyCertificate>" +
            "<esv:certificate>";
    public static String lastPart = "</esv:certificate>" +
            "</esv:VerifyCertificate>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    XMLReader xr = null;

    public ServiceVerify() throws MalformedURLException {
        this.url = new URL(Configuration.getInstance().URL);
        if(Configuration.getInstance().TO_USE_PROXY) {
//            this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(Configuration.getInstance().proxyHost, Configuration.getInstance().proxyPort)); //прокси
            System.setProperty("http.proxyHost", Configuration.getInstance().proxyHost);
            System.setProperty("http.proxyPort", Integer.toString(Configuration.getInstance().proxyPort));
        } else{
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
        }
        try {
            xr = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            log.error("Unable to create XMLFactory ", e);
            e.printStackTrace();

        }
        ResponseParserHandler handler = new ResponseParserHandler();
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);
    }


    /**
     * Посылает сертификат на сервис проверки
     *
     * */
    public InputStream sendCertificate(File certFile) throws IOException {
        HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", "http://esv.server.rt.ru/VerifyCertificate");

        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //send
        BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
        out.write(firstPart.getBytes());
        out.write(getBase64Encoding(certFile).getBytes());
        out.write(lastPart.getBytes());
        out.close();
        log.debug("Send request");
        //get response
        return connection.getInputStream();
    }

    /**
     * Все проверяет
     * @return "0\n" если все ОК<br /> код и описание ошибки в ином случае
     * */
    public String validateCertificate(File certFie) throws IOException, SAXException {
        xr.parse(new InputSource(sendCertificate(certFie)));
        return this.response;
    }

    public synchronized String getBase64Encoding(File certificateFile) throws IOException {
        //Надо удалять чтобы certUtil не выдавал ошибку
        if(base64encodedCertificate.exists()) {
            if(!base64encodedCertificate.delete()) {
                log.error("Couldn't delete file " + base64encodedCertificate.getAbsolutePath());
            }
        }

        BufferedReader reader = new BufferedReader(new FileReader(certificateFile));
        if( reader.readLine().contains("BEGIN CERTIFICATE")) {
            reader.close();
            return readBase64EncodedCertificate(certificateFile);
        } else {
            Process process = Runtime.getRuntime().exec("certutil -encode \"" + certificateFile.getAbsolutePath() + "\" \"" + base64encodedCertificate.getAbsolutePath() + "\"");
            log.debug("executing:  " + ("certutil -encode \"" + certificateFile.getAbsolutePath()+ "\" \"" + base64encodedCertificate.getAbsolutePath() + "\""));

            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("Cp1251")));
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
                log.error("Ошибка при декодировании файла:\n" + s1 +'\n'+ s2 +'\n'+ s3 + error.toString());
                reader.close();
                throw  new IOException("Ошибка при декодировании файла:\n" + s1 +'\n'+ s2 +'\n'+ s3 + error.toString());
            }
            reader.close();
//            process.destroy();
            return readBase64EncodedCertificate(base64encodedCertificate);
        }
    }

    private String readBase64EncodedCertificate(File certFile) throws IOException {
        StringBuilder result = new StringBuilder();
        List<String> lines = Files.readAllLines(certFile.toPath(), Charset.forName("Cp1251"));
        //первую и последнюю строчку не берем
        for(int i = 1; i<lines.size()-1; i++) {
            result.append(lines.get(i));
        }
        return result.toString();
    }


    /**
     *
     * */
    private class ResponseParserHandler extends DefaultHandler {
        // b=0 не дошли, b=1 - дошли, b=2 - все ок, b=3 - ошибка
//        byte b;
//        boolean codeStarted
        boolean codeElement = false;

        @Override
        public void startDocument() {
            ServiceVerify.this.response = "";
        }

        public void startElement (String uri, String localName,
                                  String qName, Attributes attributes)
                throws SAXException
        {
            if(localName.equals("Code")){
                codeElement = true;
            }
        }
        public void endElement (String uri, String localName, String qName)
                throws SAXException
        {
            if(localName.equals("Code")){
                codeElement = false;
            }
        }
        @Override
        public void characters(char[] ch,
                               int start,
                               int length){
            if(codeElement){
                ServiceVerify.this.response = new String(ch, start, length);
            } else
                if(!response.equals("0")){
                    ServiceVerify.this.response += '\n';
                    ServiceVerify.this.response += new String(ch, start, length);
                }

        }

        @Override
        public void endDocument(){
//            report.append("</td>");
        }

    }

}

