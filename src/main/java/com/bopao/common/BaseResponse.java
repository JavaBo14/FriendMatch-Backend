package com.bopao.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 通用返回类(返回前端)
 *
 * @param <T>
 * @author <a href="https://github.com/JavaBo14">Bo</a>
 * @from https://github.com/JavaBo14/Matching
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
