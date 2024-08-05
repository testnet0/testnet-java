/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-11-10
 **/
package testnet.common.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;


public class JsonUtils {

    private static final Logger log = Logger.getLogger(JsonUtils.class.getName());

    // 从JsonArray中提取指定的字段 并且重新命名字段
    public static JSONArray extractJsonArrayAndRename(JSONArray inputArray, ArrayList<String> originalFields, ArrayList<String> newFields) {
        if (originalFields.size() != newFields.size()) {
            throw new IllegalArgumentException("Original and new fields arrays must have the same length.");
        }
        JSONArray resultArray = new JSONArray();
        for (int i = 0; i < inputArray.size(); i++) {
            try {
                JSONObject originalObject = inputArray.getJSONObject(i);
                // 创建新的 JSONObject，并根据数组映射关系添加新的键值对
                JSONObject newObject = new JSONObject();
                for (int j = 0; j < originalFields.size(); j++) {
                    String originalFieldName = originalFields.get(j);
                    String newFieldName = newFields.get(j);

                    if (originalObject.containsKey(originalFieldName)) {
                        Object fieldValue = originalObject.get(originalFieldName);
                        newObject.put(newFieldName, fieldValue);
                    }
                }
                // 将新的 JSONObject 添加到结果数组中
                resultArray.add(newObject);
            } catch (JSONException e) {
                log.info("JSONException:" + e.getMessage());
            }
        }

        return resultArray;
    }

    // 从json中提取指定的字段 并且重新命名字段

    public static JSONObject extractJsonObjectAndRename(JSONObject inputObject, ArrayList<String> originalFields, ArrayList<String> newFields) {
        if (originalFields.size() != newFields.size()) {
            throw new IllegalArgumentException("Original and new fields arrays must have the same length.");
        }
        // 创建新的 JSONObject，并根据数组映射关系添加新的键值对
        JSONObject newObject = new JSONObject();

        for (int j = 0; j < originalFields.size(); j++) {
            String originalFieldName = originalFields.get(j);
            String newFieldName = newFields.get(j);
            if (inputObject.containsKey(originalFieldName)) {
                try {
                    Object fieldValue = inputObject.get(originalFieldName);
                    newObject.put(newFieldName, fieldValue);
                } catch (JSONException e) {
                    log.info("JSONException:" + e.getMessage());
                }
            }
        }

        return newObject;
    }

}
