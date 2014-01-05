package com.chuang.exception;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableSet;

/**
 * Created with IntelliJ IDEA.
 *
 * @author xiaofeng.ma
 * @since 2013-02-06
 */
public enum ErrorCode {

	FAIL(0, "failed"), // failed
	SUCCESS(200, "succeed"), // succeed
	EMPTY(201, "空消息"), //
	

    //客户端请求问题
    ERR_CLIENT(40000, "客户端错误"), //
    ERR_USER_NOT_LOGIN(40001, "用户未登录"), //
    ERR_USER_NOT_ACCEPT(40003, "此用户长连接被拒绝"), //
    ERR_PARAMETER(40006, "参数错误"), //
    
	ERR_NOT_FOUND(40012, "没有对应的记录"), // no such record
	ERR_CSRF_TOKEN(40017, "csrfToken校验错误"), //
	ERR_USERCENTER_ERR(40020, "用户中心错误"), // user center error
	ERR_USERCENTER_PWDLEN_ERR(40021, "用户密码长度不符合规范"), // user center error
	
	// 5XXXX: caused by server
    //服务器问题
	ERR_UNKNOWN_EXCEPTION(50000, "未知错误"), // internal server error
    ERR_THIRD_SERVER_FAIL(50014, "第三方服务访问异常");
	
    private int code;
    private String message;
    private static EnumSet<ErrorCode> setAll = EnumSet.allOf(ErrorCode.class);
    private static Map<Integer, ErrorCode> mapAll = new HashMap<Integer, ErrorCode>();
    static {
        for (ErrorCode item : setAll) {
            mapAll.put(item.getCode(), item);
        }
    }
    
    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ErrorCode");
        sb.append("{code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
    

    public static ErrorCode getErrorCode(int key) {
        return mapAll.get(key);
    }
    
    // for check client error
    private static final Set<ErrorCode> CLIENT_ERR = new ImmutableSet.Builder<ErrorCode>()
            .add(ErrorCode.ERR_PARAMETER)
            .add(ErrorCode.ERR_NOT_FOUND)
            .add(ErrorCode.ERR_USER_NOT_LOGIN).build();
    
    public static boolean isClientError(ErrorCode errCode){
        // process as server error if no error code found
        if (errCode == null) {
            return false;
        }
        if (CLIENT_ERR.contains(errCode)) {
            return true;
        }
        // check the leading number
        String errStr = "" + errCode.getCode();
        if (StringUtils.isNotBlank(errStr) && errStr.length() > 1 &&
                StringUtils.startsWithIgnoreCase(errStr, "4") ) {
            return true;
        }
        return false;
    }
}
