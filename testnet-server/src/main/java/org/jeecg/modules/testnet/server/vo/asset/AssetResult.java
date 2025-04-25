package org.jeecg.modules.testnet.server.vo.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;

import java.io.Serializable;

/**
 * 接口返回数据格式
 *
 * @author scott
 * @email jeecgos@163.com
 * @date 2019年1月19日
 */
@Data
@Schema(description="接口返回对象")
public class AssetResult<T extends AssetBase> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @Schema(description = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @Schema(description = "返回处理消息")
    private String message = "";

    /**
     * 返回代码
     */
    @Schema(description = "返回代码")
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    @Schema(description = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private long timestamp = System.currentTimeMillis();
    @JsonIgnore
    private String onlTable;

    public AssetResult() {
    }

    /**
     * 兼容VUE3版token失效不跳转登录页面
     *
     * @param code
     * @param message
     */
    public AssetResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T extends AssetBase> AssetResult<T> ok() {
        return OK();
    }

    public static <T extends AssetBase> AssetResult<T> ok(T data) {
        AssetResult<T> r = new AssetResult<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static <T extends AssetBase> AssetResult<T> OK() {
        AssetResult<T> r = new AssetResult<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        return r;
    }

    public static <T extends AssetBase> AssetResult<T> OK(T data) {
        AssetResult<T> r = new AssetResult<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static <T extends AssetBase> AssetResult<T> OK(String msg, T data) {
        AssetResult<T> r = new AssetResult<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T extends AssetBase> AssetResult<T> error(String msg, T data) {
        AssetResult<T> r = new AssetResult<T>();
        r.setSuccess(false);
        r.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T extends AssetBase> AssetResult<T> error(String msg) {
        return error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static <T extends AssetBase> AssetResult<T> error(int code, String msg) {
        AssetResult<T> r = new AssetResult<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    /**
     * 无权限访问返回结果
     */
    public static <T extends AssetBase> AssetResult<T> noauth(String msg) {
        return error(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
    }

    public AssetResult<T> success(String message) {
        this.message = message;
        this.code = CommonConstant.SC_OK_200;
        this.success = true;
        return this;
    }

    public AssetResult<T> error500(String message) {
        this.message = message;
        this.code = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;
        this.success = false;
        return this;
    }

}