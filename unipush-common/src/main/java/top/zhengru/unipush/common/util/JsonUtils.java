package top.zhengru.unipush.common.util;

import com.alibaba.fastjson2.JSON;

/**
 * JSON工具类
 *
 * @author zhengru
 */
public class JsonUtils {

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * JSON字符串转对象
     *
     * @param json     JSON字符串
     * @param classType 对象类型
     * @param <T>       泛型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> classType) {
        return JSON.parseObject(json, classType);
    }

    /**
     * JSON字符串转对象（支持泛型）
     *
     * @param json       JSON字符串
     * @param classType  对象类型
     * @param elementType 元素类型
     * @param <T>        泛型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> classType, Class<?> elementType) {
        return JSON.parseObject(json, classType);
    }
}
