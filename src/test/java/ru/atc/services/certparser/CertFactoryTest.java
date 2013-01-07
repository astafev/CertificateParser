package ru.atc.services.certparser;

import java.io.File;
import java.util.Map;

import org.testng.annotations.*;
import ru.atc.services.certparser.config.Property;

import static org.testng.Assert.*;

public class CertFactoryTest {


//    public XCertFileRetr retr = new XCertFileRetr();



    @BeforeTest
    public void setConfiguration() {

;
    }

    @Test
    public void verifiesHowParserParsesTestCertificate() throws Exception {
        CertFactory factory = new CertFactory();
        Map<Property, String> certMap= factory.parseCertificate(new File("cert_fail.cer"));

        assertEquals(certMap.get("ОГРН"),"1058600008807");
        assertEquals(certMap.get("Субъект"),"АУ \"Многофункциональный центр Югры\"");
        System.out.println(certMap.get("Серийный номер"));
        System.out.println(certMap.get("Сертификат"));
    }
}
