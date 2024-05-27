package as.rpc.core.api;

/**
 * RPC 统一异常类
 *
 * <p>
 *
 * @author: Abner Song
 * <p>
 * @date: 2024/5/27 10:15
 */
public class RpcException extends RuntimeException {
    private String errorCode;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    // X -> 技术类异常
    // Y -> 业务类异常
    // Z -> unknown
    public static final String SOCKET_TIME_OUT_EXCEPTION = "X001" + "-" + "http_invoke_time";
    public static final String NO_SUCH_METHOD_EXCEPTION = "X002" + "-" + "method_not_exists";
    public static final String UNKNOWN_EXCEPTION = "Z001" + "-" + "unknown_exception";
}
