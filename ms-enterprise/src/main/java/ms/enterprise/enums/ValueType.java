package ms.enterprise.enums;

import java.util.Arrays;
import java.util.List;

public enum ValueType {

    NUMBER(0, "number"),
    PER_CENT(1, "percent"),
    THOUSANDTH(2, "thousandth"),
    TEXT(3, "text");

    private long id;
    private String i18nCode;

    ValueType(long id, String i18nCode){
        this.id = id;
        this.i18nCode = i18nCode;
    }

    public static final ValueType values[] = values();

    public String getI18nCode(){
        return this.i18nCode;
    }
    public long getId(){
        return this.id;
    }

    public static ValueType get(Long id) {
        if(id == null){
            return null;
        }
        return ValueType.values[id.intValue()];
    }

    public static List<ValueType> list(){
        return Arrays.asList(
                ValueType.NUMBER,
                ValueType.PER_CENT,
                ValueType.THOUSANDTH,
                ValueType.TEXT
        );
    }
}
