package com.chuang.exception;


import org.apache.commons.lang.StringUtils;

/**
 * 接口返回的异常错误
 * 
 * @author zhimin.duan
 * 
 */
public final class QException extends RuntimeException{
    private static final long serialVersionUID = -2884414252638860664L;

    private ErrorCode errorCode;
    private int code = -1;

    public QException() {
        super();
    }

    public QException(ErrorCode err, String message) {
        super(message);
        errorCode = err;
        if (errorCode != null) {
            code = errorCode.getCode();
        }
    }
    public QException(int code, String message) {
        super(message);
        errorCode = ErrorCode.getErrorCode(code);
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getNotifyMessage() {
        String msg = super.getMessage();
        if (StringUtils.isBlank(msg) && errorCode != null) {
            msg = errorCode.getMessage();
        }
        if (StringUtils.isNotBlank(msg)) {
            return msg;
        }
        return "";
    }

    public int getCode() {
        return code;
    }
}