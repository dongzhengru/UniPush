package top.zhengru.unipush.common.model.vo;

import top.zhengru.unipush.common.enums.ResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 * @author zhengru
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public static <T> ResponseVO<T> ok() {
        return restResult(null, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg());
    }

    public static <T> ResponseVO<T> ok(T data) {
        return restResult(data, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg());
    }

    public static <T> ResponseVO<T> ok(String msg) {
        return restResult(null, ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ResponseVO<T> ok(T data, String msg) {
        return restResult(data, ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ResponseVO<T> fail() {
        return restResult(null, ResponseCode.SYSTEM_ERROR.getCode(), ResponseCode.SYSTEM_ERROR.getMsg());
    }

    public static <T> ResponseVO<T> fail(String msg) {
        return restResult(null, ResponseCode.SYSTEM_ERROR.getCode(), msg);
    }

    public static <T> ResponseVO<T> fail(T data) {
        return restResult(data, ResponseCode.SYSTEM_ERROR.getCode(), ResponseCode.SYSTEM_ERROR.getMsg());
    }

    public static <T> ResponseVO<T> fail(T data, String msg) {
        return restResult(data, ResponseCode.SYSTEM_ERROR.getCode(), msg);
    }

    public static <T> ResponseVO<T> fail(Integer code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> ResponseVO<T> fail(ResponseCode responseCode) {
        return restResult(null, responseCode.getCode(), responseCode.getMsg());
    }

    public static <T> ResponseVO<T> fail(ResponseCode responseCode, String msg) {
        return restResult(null, responseCode.getCode(), msg);
    }

    public static <T> ResponseVO<T> error(ResponseCode responseCode, String msg) {
        return restResult(null, responseCode.getCode(), msg);
    }

    public static <T> ResponseVO<T> error(Integer code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> ResponseVO<T> restResult(T data, Integer code, String msg) {
        ResponseVO<T> apiResult = new ResponseVO<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static boolean isSuccess(ResponseVO<?> response) {
        return response != null && ResponseCode.SUCCESS.getCode().equals(response.getCode());
    }
}
