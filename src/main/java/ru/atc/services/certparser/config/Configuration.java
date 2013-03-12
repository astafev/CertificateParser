package ru.atc.services.certparser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.atc.services.certparser.db.ScriptGeneratorListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Конфигурация считыается из xml файла с известной структурой. Если файла нет либо в нем не присутствует какое-то значение, берется значения по умолчанию,
 * хотя отсутствие конфигурационного файла это все же ошибка.
 */
public class Configuration implements ActionListener{
    public static Logger log = LoggerFactory.getLogger("certificateparser.config");

    public static String configFile = "configuration.xml";

    public String URL = "http://oraas.rt.ru:7777/gateway/services/SID0003318";

    /***/
    public Set<Property> propSet = new TreeSet<Property>();

    public boolean TO_USE_PROXY = false;
    public String proxyHost = "localhost";
    public int proxyPort = 8888;

    public boolean TO_SEND_AUTHOMATICALLY = false;

    XMLReader xmlReader = null;

    //singleton
    private static Configuration configuration;
    private ConfigParserHandler configParserHandler = new ConfigParserHandler();

    public String template_script = "template_script.sql";
    public String target_script_file = "generated_script.sql";

    /**
     *
     * */
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
            } catch (MalformedURLException e) {
                log.error("Не нашел конфигурационный файл!", e);
                JOptionPane.showMessageDialog(null,"Не нашел конфигурационный файл!\n" + configFile + '\n' +  e.toString() + "\nПодробнее в логах", "Не нашел конфигурационный файл!" , JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                log.error("пиздец", e);
                JOptionPane.showMessageDialog(null, e.toString()+ "\nПодробнее в логах", "Ошибка при чтении конфигурационного файла!", JOptionPane.ERROR_MESSAGE);
            }
            log.debug("Config created");
        }
        return configuration;
    }

    /**считывает конфиг из файла
     * @param configFile файл с xml-конфигом*/
    public void init(String configFile) throws SAXException, IOException {
        //считывает конфигурация из xml-файла
        //to_test
        this.propSet = new LinkedHashSet<Property>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFile);

        try{
            xmlReader.parse(new InputSource(in));
        } finally {
            propSet.add(new Property("Серийный номер", "%serial%", "Серийный номер: ", "Сертификат X509"));
            propSet.add(new Property("Сертификат", null, null, null));
        }
        in.close();
    }

    /**считывает конфиг из файла по умолчанию (поле configFile)*/
    public void init() throws SAXException, IOException {
        this.init(Configuration.configFile);
    }

    /**Перегружает конфиг из файла по умолчанию
     * <br /> Должен срабатывать по нажатии кнопки "Reload config" пользователем*/
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ScriptGeneratorListener.getInstance().renewConfig();
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
        private boolean template_script = false;
        private boolean target_script_file = false;


        private Property prop;

        @Override
        public void startDocument() {

        }

        @Override
        public void startElement (String uri, String localName,
                                  String qName, Attributes attributes)
                throws SAXException
        {
            if(uri.equals("")){
                switch (localName) {
                    case "service":
                        this.service = true;
                        break;
                    case "properties":
                        this.properties = true;
                        break;
                    case "property":
                        this.property = true;
                        break;
                    case "name":
                        this.name = true;
                        break;
                    case "patternInScript":
                        this.patternInScript = true;
                        break;
                    case "patternInCert":
                        this.patternInCert = true;
                        break;
                    case "send_automatically":
                        this.send_automatically = true;
                        break;
                    case "url":
                        this.url = true;
                        break;
                    case "proxy":
                        this.proxy = true;
                        break;
                    case "use_proxy":
                        this.use_proxy = true;
                        break;
                    case "host":
                        this.host = true;
                        break;
                    case "port":
                        this.port = true;
                        break;
                    case "sectionInCert":
                        this.sectionInCert = true;
                        break;
                    case "template_script":
                        this.template_script = true;
                        break;
                    case "target_script_file":
                        this.target_script_file = true;
                        break;

                }

            }
        }

        @Override
        public void endElement (String uri, String localName, String qName)
                throws SAXException
        {
            switch (localName) {
                case "service":
                    this.service = false;
                    break;
                case "properties":
                    this.properties = false;
                    break;
                case "property":
                    Configuration.this.propSet.add(prop);
                    this.property = false;
                    break;
                case "name":
                    this.name = false;
                    break;
                case "patternInScript":
                    this.patternInScript = false;
                    break;
                case "patternInCert":
                    this.patternInCert = false;
                    break;
                case "send_automatically":
                    this.send_automatically = false;
                    break;
                case "url":
                    this.url = false;
                    break;
                case "proxy":
                    this.proxy = false;
                    break;
                case "use_proxy":
                    this.use_proxy = false;
                    break;
                case "host":
                    this.host = false;
                    break;
                case "port":
                    this.port = false;
                    break;
                case "sectionInCert":
                    this.sectionInCert = false;
                    break;
                case "template_script":
                    this.template_script = false;
                    break;
                case "target_script_file":
                    this.target_script_file = false;
                    break;

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
            } else if(template_script) {
                Configuration.this.template_script = String.valueOf(ch, start, length);
            } else if(target_script_file) {
                Configuration.this.target_script_file = String.valueOf(ch, start, length);
            }
        }

        @Override
        public void endDocument(){
            Configuration.log.info("Finished to read config from " + Configuration.configFile);
        }

    }
}



