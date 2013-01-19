package ru.atc.services.certparser.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atc.services.certparser.config.Configuration;
import ru.atc.services.certparser.config.Property;
import ru.atc.services.certparser.gui.MainWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 03.01.13
 * Time: 12:49
 */
public class ScriptGeneratorListener implements ActionListener{
    public static Logger log = LoggerFactory.getLogger("certificateparser.db");

    /**имя/паттерн*/
    Map<Property,Pattern> patterns;
//    static Matcher m = patterns.matcher("");
    public File fileWithScripts;
    public BufferedWriter writer;
    public static ScriptGeneratorListener scriptGenerator;

    String templateScript;


    private ScriptGeneratorListener()  {

    }

    public static ScriptGeneratorListener getInstance() throws IOException{
        if(scriptGenerator == null) {
            scriptGenerator = new ScriptGeneratorListener();
            scriptGenerator.fileWithScripts = new File(Configuration.getInstance().target_script_file);
            if(!scriptGenerator.fileWithScripts.exists()) {
                try {
                    scriptGenerator.fileWithScripts.createNewFile();
                } catch (IOException e1) {
                    log.error("Unable to create file for scripts " + scriptGenerator.fileWithScripts.toString(), e1);
                    scriptGenerator.fileWithScripts = new File("generated_scripts.sql");
                    try {
                        scriptGenerator.fileWithScripts.createNewFile();
                    } catch (IOException e2) {
                        log.error("Unable to create file for scripts " + scriptGenerator.fileWithScripts.toString() + " It's going to be impossible to create scripts", e2);

                    }
                }
            }
            scriptGenerator.writer = new BufferedWriter(new FileWriter(scriptGenerator.fileWithScripts));
        }
        return scriptGenerator;
    }




    public void renewConfig() throws IOException {
        scriptGenerator = null;      //хули мучаться

        //создаем новый файл

//        templateScript = null;
//        patterns = null;
//        fileWithScripts = null;
//        writer = null;
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        try {


            //МУ-ХА-ХА
            Map<Property,JTextComponent> values = MainWindow.getInstance().certificatePanel.propFields;
            String script;
            script = generateScriptFromFile(values, Configuration.getInstance().template_script);
            writer.write(script);
            writer.flush();
            log.info("Wrote something to script");
            log.debug(script);
        } catch (IOException e1) {
            log.error("Бедаааа!", e1);
            JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Бедаа при создании скрипта", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String generateScriptFromFile(Map<Property,JTextComponent> values, String templateScriptFile) throws IOException {
        Map<Property, String> map = new LinkedHashMap<Property, String>(values.size());
        for(Property property : values.keySet()) {
            map.put(property, values.get(property).getText());
        }
        StringBuilder sb = new StringBuilder();

        if(templateScript == null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(templateScriptFile), Charset.forName("UTF-8")));
            String s;
            while((s= reader.readLine())!=null) {
                sb.append(s);
                sb.append('\n');
            }
            templateScript = sb.toString();
        }
      /*  List<String> strings = java.nio.file.Files.readAllLines(templateScript.toPath(), Charset.forName("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for(String s:strings) {
            sb.append(s);
        }*/

        return generateScript(map, templateScript);
    }


    /**на первый взгляд крайне ебаная реализация, особенно предыдущий метод
     * todo
     * to_test пока работает...*/
    public String generateScript(Map<Property, String> values, String templateScript) {
        String script = templateScript;
        if(patterns == null) {
            patterns = new HashMap<Property, Pattern>();
            for(Property prop: values.keySet()) {
                if(prop.getPatternInScript()==null) {
                    continue;
                }
                patterns.put (prop, Pattern.compile(prop.getPatternInScript()) );
            }
        }

//        StringBuffer b = new StringBuffer(templateScript.length());
        for(Property prop: values.keySet()) {
            if(prop.getPatternInScript()==null) {
                continue;
            }
            Matcher m = patterns.get(prop).matcher(script);
            script = m.replaceAll(values.get(prop));
        }
        return script;
    }
}
