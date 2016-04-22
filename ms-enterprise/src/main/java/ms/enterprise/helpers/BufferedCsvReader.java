package ms.enterprise.helpers;

import grails.util.Holders;
import ms.enterprise.Attribute;
import ms.enterprise.Municipality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BufferedCsvReader {

    private static final Logger log =
            LoggerFactory.getLogger(BufferedCsvReader.class);

    private AttributeStore attrService =
            (AttributeStore)
                    Holders.getGrailsApplication().getMainContext().getBean(
                            "attributeStoreService");

    private List<String> headers = new ArrayList<>();
    private List<Attribute> labels = new ArrayList<>();
    private List<Map<String, Object>> values = new ArrayList<>();
    private Municipality municipality;

    public BufferedCsvReader(){

    }

    public void readFolkeMengdeOgBVekst(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("data/folkemengdogendringer.csv"
                ), StandardCharsets.ISO_8859_1)
            );
            String line = "";
            String cvsSplitBy = ";";
            long linesRead = 0;

            int range = 0;
            while ((line = reader.readLine()) != null){
                String[] fields = line.split(cvsSplitBy);

                if(linesRead == 3){
                    for(int i = 2; i < fields.length; i++){
                        String headerName = fields[i];
                        headers.add(headerName);
                    }
                }
                if(linesRead > 3){
                    fields[0] = fields[0].replaceAll("[\".]", "");;
                    if(fields[0].length() < 2 && linesRead < 16){
                        createAttribute(fields[1]);
                    } else if(fields[0].length() > 1) {
                        this.municipality =
                                attrService.getMunicipality(fields[0]);
                        range = 0;
                    }

                    if(fields[0].length() < 2 && this.municipality != null){
                        createValue(fields, range, 2);
                        range++;
                    }

                }
                linesRead++;
                if(linesRead == 5140){
                    break;
                }
            }
            attrService.saveAttrVal(values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAttribute(String field){
        for(String header : headers){
            labels.add(attrService.save(field + " - " + header));
        }

    }

    private void createValue(String[] fields, int range, int hPos){
        int start = (fields.length - hPos) * range;
        for(int i = hPos ; i < fields.length; i++){
            int labelPos = i + (start-hPos);
            Attribute attribute = labels.get(labelPos);
            long value = 0;
            try{
                value = Long.valueOf(fields[i]);
            } catch (NumberFormatException e){}

            Map<String, Object> item = new HashMap<>();
            item.put("attribute", attribute);
            item.put("municipality", this.municipality);
            item.put("value", value);
            values.add(item);
        }
    }

    private void createValueFromDecimal(String[] fields, int range){
        int start = (fields.length -2) * range;
        for(int i = 2 ; i < fields.length; i++){
            int labelPos = i + (start-2);
            Attribute attribute = labels.get(labelPos);
            long value = 0;
            try{
                Double val = Double.valueOf(fields[i]) * 1000;
                value = val.longValue();
            } catch (NumberFormatException e){}

            Map<String, Object> item = new HashMap<>();
            item.put("attribute", attribute);
            item.put("municipality", this.municipality);
            item.put("value", value);
            values.add(item);
        }
    }

    public void readForbrukerKraft(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("data/forbrukerkraft.csv"
                            ), StandardCharsets.ISO_8859_1)
            );
            String line = "";
            String cvsSplitBy = ";";
            long linesRead = 0;

            int range = 0;
            while ((line = reader.readLine()) != null){
                String[] fields = line.split(cvsSplitBy);

                if(linesRead == 3){
                    for(int i = 2; i < fields.length; i++){
                        String label = "Nettoforbruk kraft (KWh)";
                        String headerName = fields[i]
                                .replaceAll("[^\\p{L}\\p{N}]+", "");
                        headers.add(label + " - " + headerName);
                    }
                }
                if(linesRead > 4){
                    fields[0] = fields[0].replaceAll("[\".]", "");;
                    if(fields[0].length() < 2 && linesRead < 16){
                        createAttribute(fields[1]);
                    } else if(fields[0].length() > 1) {
                        this.municipality =
                                attrService.getMunicipality(fields[0]);
                        range = 0;
                    }

                    if(fields[0].length() < 2 && this.municipality != null){
                        createValueFromDecimal(fields, range);
                        range++;
                    }

                }
                linesRead++;
                if(linesRead == 3453){
                    break;
                }
            }
            attrService.saveAttrVal(values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readInnERMenn(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    "data/innvandrereetterregion_menn.csv"
                            ), StandardCharsets.ISO_8859_1)
            );
            String line = "";
            String cvsSplitBy = ";";
            long linesRead = 0;

            int range = 0;
            while ((line = reader.readLine()) != null){
                String[] fields = line.split(cvsSplitBy);

                if(linesRead == 2){
                    for(int i = 3; i < fields.length; i++){
                        String label = "Innvandrere - Menn";
                        String headerName = fields[i]
                                .replaceAll("[^\\p{L}\\p{N}]+", "");
                        headers.add(label + " - " + headerName);
                    }
                }
                if(linesRead > 3){
                    fields[0] = fields[0].replaceAll("[\".]", "");;
                    if(fields[0].length() < 2 &&
                            linesRead < 15 &&
                            fields[1].length() < 2){
                        createAttribute(fields[2]);
                    } else if(fields[0].length() > 1) {
                        this.municipality =
                                attrService.getMunicipality(fields[0]);
                        range = 0;
                    }

                    if(fields[0].length() < 2 &&
                            this.municipality != null &&
                            fields[1].length() < 2){
                        createValue(fields, range, 3);
                        range++;
                    }

                }
                linesRead++;
                if(linesRead == 4284){
                    break;
                }
            }
            attrService.saveAttrVal(values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInnERKvinner(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    "data/innvandrereetterregion_kvinner.csv"
                            ), StandardCharsets.ISO_8859_1)
            );
            String line = "";
            String cvsSplitBy = ";";
            long linesRead = 0;

            int range = 0;
            while ((line = reader.readLine()) != null){
                String[] fields = line.split(cvsSplitBy);

                if(linesRead == 2){
                    for(int i = 3; i < fields.length; i++){
                        String label = "Innvandrere - Kvinner";
                        String headerName = fields[i]
                                .replaceAll("[^\\p{L}\\p{N}]+", "");
                        headers.add(label + " - " + headerName);
                    }
                }
                if(linesRead > 3){
                    fields[0] = fields[0].replaceAll("[\".]", "");

                    if(fields[0].length() < 2 &&
                            linesRead < 15 &&
                            fields.length > 2){
                        createAttribute(fields[2].replaceAll("[\"]+", ""));
                    } else if(fields[0].length() > 1) {
                        this.municipality =
                                attrService.getMunicipality(fields[0]);
                        range = 0;
                    }

                    if(fields[0].length() < 2 &&
                            this.municipality != null &&
                            fields.length > 2){
                        createValue(fields, range, 3);
                        range++;
                    }

                }
                linesRead++;
                if(linesRead == 4284){
                    break;
                }
            }
            attrService.saveAttrVal(values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
