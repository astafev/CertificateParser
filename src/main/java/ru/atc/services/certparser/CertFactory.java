package ru.atc.services.certparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.config.Property;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Из файла с сертификатом должен делать карту с нужными значениями. Все зависит от конфига.
 * Основной метод: parseCertificate(File file)
 */
public class CertFactory {

    private Configuration configuration = Configuration.getInstance();
    String certificate;
    public static Logger log = LoggerFactory.getLogger("certificateparser.CertFactory");


    /**@param certFile файл сертификата
     * @return сертификат одной большой строкой - то что certutil возвращает*/
    public String getCertificate(File certFile) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("certutil \"" + certFile.getAbsolutePath()+ "\"");
        log.debug("executing:  " + ("certutil \"" + certFile.getAbsolutePath()+ "\""));
        StringBuilder cert = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("Cp1251")));
        int i;
        while ((i = reader.read()) != -1) {
            cert.append((char)i);
        }
        return cert.toString();
    }

    /**Парсит сертификат
     * @return возвращает карту &lt;Название св-ва, значение&gt;,
     * среди которых есть "Серийный номер" (в десятичном формате), "Сертификат", где весь сертификат строкой
     * <br />
     * Есть 3 св-ва обязательных: файл с сертификатом, серийный номер, то что выдала утилитка certutil (при нормальной работе сам сертификат)
     * */
    public synchronized Map<Property, String> parseCertificate(File certificateFile) throws IOException {
        Map<Property, String> certMap = new LinkedHashMap<Property, String>(3 + configuration.config.length);
        this.certificate = getCertificate(certificateFile);
        log.debug("Read " + certificate.length() + " symbols to certificate.");

        BigInteger serial;
        System.out.println(configuration.propSet);
        for(Property prop:configuration.propSet) {
            //prop[0] - паттерн
            //prop[1] - имя
            //prop[2] - где искать
            if(prop.getName().equals("Серийный номер")){
                try {
                    serial = new BigInteger(parse("Серийный номер: ", 0, 500)[0], 16);
                } catch (NumberFormatException e) {
                    serial = new BigInteger("0");
                    log.error("Didn't found serial number! Probably");
                }
                certMap.put(prop, serial.toString(10));
                continue;
            }

            if(prop.getName().equals("Сертификат")){
                certMap.put(prop, certificate);
                continue;
            }
            int index;
            if(prop.getSectionInCert() == null){
                index = certificate.indexOf("Субъект");
            } else {
                index = certificate.indexOf(prop.getSectionInCert());
            }
            String[] value = parse(prop.getPatternInCert(), index, index+1000);
            if(value.length>1) {
                log.warn("Found more than one occurences of \"" + prop.getPatternInCert() +
                        "\" in this part (from " + index + "):\n" + certificate.substring(index, index+1000));//
            }
            certMap.put(prop, value[0]);
        }
        return certMap;
    }

    /**
     * */
    private String[] parse(String pattern, int beginIndex, int endIndex) {
        if(beginIndex < 0) {
            beginIndex = 0;
        }
        String[] certparts = certificate.substring(beginIndex, endIndex).split(pattern);

        if(certparts.length==1) {
            return  new String[]{"Not found!"};
        }
        String[] values = new String[certparts.length-1];
        for(int i = 1; i<certparts.length; i++) {
            values[i-1] = certparts[i].substring(0, certparts[i].indexOf("\r"));
        }
        return values;
    }
}
