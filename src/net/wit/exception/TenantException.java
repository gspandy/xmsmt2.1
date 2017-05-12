package net.wit.exception;

/**
 * @ClassName：OrderException @Description：订单异常
 * @author：Chenlf
 * @date：2015年9月11日 下午4:54:02
 */
public class TenantException extends Exception {
	private static final long serialVersionUID = 9001L;

	public TenantException() {
		super();
	}

	public TenantException(String message, Throwable cause) {
		super(message, cause);
	}

	public TenantException(String message) {
		super(message);
	}

	public TenantException(Throwable cause) {
		super(cause);
	}
}
