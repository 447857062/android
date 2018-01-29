package com.deplink.sdk.android.sdk.utlis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huqs on 2016/7/11.
 */
public class ReflectUtlis {
    /**
     * 通过反射机制获取对象中的私有属性
     *
     * @param object
     * @return
     */
    public static Map<String, Object> objectConvertAttribute(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field fields[] = object.getClass().getDeclaredFields();
        Field.setAccessible(fields, true);
        for (Field field : fields) {
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
