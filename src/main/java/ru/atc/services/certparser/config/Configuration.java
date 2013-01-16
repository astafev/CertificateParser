package ru.atc.services.certparser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 */
public class Configuration implements ActionListener{
    public static Logger log = LoggerFactory.getLogger("certificateparser.config");

    public static String configFile = "configuration.xml";

    public String URL = "http://oraas.rt.ru:7777/gateway/services/SID0003318";

    public String[][] config = new String[][]{
            {"OID\\.1\\.2\\.643\\.100\\.1=",  "ОГРН",    "Субъект"},
            {"CN=",                           "Субъект", "Субъект"}
    };

    /***/
    public Set<Property> propSet = new TreeSet<Property>();

    /* сейчас будет сложно реализовать такой метод, да он и нафиг не нужен
    public Property getProperty(String name) {

        return configMap.get(name);
    }*/


    public final int propertiesNumber = config.length;

    public boolean TO_USE_PROXY = false;
    public String proxyHost = "localhost";
    public int proxyPort = 8888;

    public boolean TO_SEND_AUTHOMATICALLY = true;

    XMLReader xmlReader = null;

    //singleton
    private static Configuration configuration;
    private ConfigParserHandler configParserHandler = new ConfigParserHandler();

    public synchronized static Configuration getInstance() {
        if(configuration == null) {
            configuration = new Configuration();
            try {
                configuration.xmlReader = XMLReaderFactory.createXMLReader();
                configuration.xmlReader.setContentHandler(configuration.configParserHandler);
                //по хорошему надо сделать error handler, но в общем-то нафиг он не нужен сейчас
                configuration.xmlReader.setErrorHandler(configuration.configParserHandler);


            } catch (SAXException e) {
                log.error("Unable to create XMLFactory ", e);
                e.printStackTrace();
            }
            try {
                configuration.init(configFile);
            } catch (SAXException e) {
                log.error("кривой конфиг или еще какая фигня", e);
            } catch (IOException e) {
                log.error("пиздец", e);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            log.debug("Config created");
        }
        return configuration;
    }

    public void init(String configFile) throws SAXException, IOException {
        //считывает конфигурация из xml-файла
        //to_test
        this.propSet = new LinkedHashSet<Property>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFile);
        xmlReader.parse(new InputSource(in));

        propSet.add(new Property("Серийный номер", "Серийный номер:", "%serial%"));
        propSet.add(new Property("Сертификат", null, null));
        in.close();
    }
    public void init() throws SAXException, IOException {
        this.init(Configuration.configFile);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            this.init();
        } catch (SAXException e1) {
            JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Неверный конфиг!", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Ошибка при чтении конфигурационного файла!", JOptionPane.ERROR_MESSAGE);
        }
    }


    private class ConfigParserHandler extends DefaultHandler {
        private boolean service = false;
        private boolean properties = false;
        private boolean property = false;
        private boolean name = false;
        private boolean patternInScript = false;
        private boolean patternInCert = false;
        private boolean send_automatically = false;
        private boolean url = false;
        private boolean proxy = false;
        private boolean use_proxy = false;
        private boolean host = false;
        private boolean port = false;
        private boolean sectionInCert = false;

        private Property prop;

        @Override
        public void startDocument() {

        }

        @Override
        public void startElement (String uri, String localName,
                                  String qName, Attributes attributes)
                throws SAXException
        {
            if(localName.equals("service")) {
                this.service = true;
            } else if(localName.equals("properties")) {
                this.properties = true;
            } else if(localName.equals("property")) {
                this.property = true;
            } else if(localName.equals("name")) {
                this.name = true;
            } else if(localName.equals("patternInScript")) {
                this.patternInScript = true;
            } else if(localName.equals("patternInCert")) {
                this.patternInCert = true;
            } else if(localName.equals("send_automatically")) {
                this.send_automatically = true;
            } else if(localName.equals("url")) {
                this.url = true;
            }  else if(localName.equals("proxy")) {
                this.proxy = true;
            } else if(localName.equals("use_proxy")) {
                this.use_proxy = true;
            } else if(localName.equals("host")) {
                this.host = true;
            } else if(localName.equals("port")) {
                this.port = true;
            } else if(localName.equals("sectionInCert")) {
                this.sectionInCert = true;
            }
        }

        @Override
        public void endElement (String uri, String localName, String qName)
                throws SAXException
        {
            if(localName.equals("service")) {
                this.service = false;
            } else if(localName.equals("properties")) {
                this.properties = false;
            } else if(localName.equals("property")) {
                Configuration.this.propSet.add(prop);
                this.property = false;
            } else if(localName.equals("name")) {
                this.name = false;
            } else if(localName.equals("patternInScript")) {
                this.patternInScript = false;
            } else if(localName.equals("patternInCert")) {
                this.patternInCert = false;
            } else if(localName.equals("send_automatically")) {
                this.send_automatically = false;
            } else if(localName.equals("url")) {
                this.url = false;
            }  else if(localName.equals("proxy")) {
                this.proxy = false;
            } else if(localName.equals("use_proxy")) {
                this.use_proxy = false;
            } else if(localName.equals("host")) {
                this.host = false;
            } else if(localName.equals("port")) {
                this.port = false;
            } else if(localName.equals("sectionInCert")) {
                this.sectionInCert = false;
            }
        }

        //АХТУНГ! из-за того что при разборе длинного тэга, метод может вызываться пару раз, все возможно будет нереально глючить
        //если че, исправлять надо тут: все = заменить на += и в классе Property все сделать не null
        @Override
        public void characters(char[] ch,
                               int start,
                               int length){
            if(properties) {
                if(property) {
                    if(name) {
                        prop = new Property(String.valueOf(ch,start, length ));
                        return;
                    }
                    if(patternInScript) {
                        prop.patternInScript = String.valueOf(ch,start, length );
                        return;
                    }
                    if(patternInCert) {
                        prop.patternInCert = String.valueOf(ch,start, length );
                        return;
                    }
                    if(sectionInCert) {
                        prop.sectionInCert = String.valueOf(ch,start, length );
                    }
                }
            } else if(service) {
                if(send_automatically) {
                    Configuration.this.TO_SEND_AUTHOMATICALLY = Boolean.parseBoolean(String.valueOf(ch, start, length));
                    return;
                }
                if(url) {
                    Configuration.this.URL = String.valueOf(ch, start, length);
                    return;
                }
                if(proxy) {
                    if(use_proxy) {
                        Configuration.this.TO_USE_PROXY = Boolean.parseBoolean(String.valueOf(ch, start, length));
                        return;
                    }
                    if(host) {
                        Configuration.this.proxyHost = String.valueOf(ch, start, length);
                    }
                    if(port) {
                        Configuration.this.proxyPort = Integer.parseInt(String.valueOf(ch, start, length));
                    }
                }
            }
        }

        @Override
        public void endDocument(){
            Configuration.log.info("Finished to read config from " + Configuration.configFile);
        }

    }
}



