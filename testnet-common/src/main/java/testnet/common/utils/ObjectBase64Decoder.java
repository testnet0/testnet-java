/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-10-31
 **/
package testnet.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class ObjectBase64Decoder {
    public static <T> T decodeFields(T obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            Object decodedValue = decodeValue(value); // 递归解码
            if (value != decodedValue) {
                field.set(obj, decodedValue);
            }
        }
        return obj;
    }

    private static Object decodeValue(Object value) throws IllegalAccessException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return decodeString((String) value);
        } else if (value instanceof Collection) {
            return decodeCollection((Collection<?>) value);
        } else if (value.getClass().isArray()) {
            return decodeArray(value);
        }
        return value; // 其他类型不处理
    }

    public static String decodeString(String originalValue) {
        if (originalValue.startsWith("\"") && originalValue.endsWith("\"")) {
            originalValue = originalValue.substring(1, originalValue.length() - 1);
        }
        if (isBase64(originalValue)) {
            // decodedValue = decodedValue.replace("\\\"", "\"");
            return new String(Base64.getDecoder().decode(originalValue));
        }
        return originalValue;
    }

    private static Collection<?> decodeCollection(Collection<?> collection) {
        for (Object element : collection) {
            try {
                if (element instanceof String) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> collectionCasted = (Collection<Object>) collection;
                    collectionCasted.remove(element);
                    collectionCasted.add(decodeValue(element));
                }
            } catch (IllegalAccessException e) {
                // Handle the exception or log it
            }
        }
        return collection;
    }

    private static Object decodeArray(Object array) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            if (element instanceof String) {
                Array.set(array, i, decodeString((String) element));
            }
        }
        return array;
    }

    private static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(allFields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return allFields;
    }
}
