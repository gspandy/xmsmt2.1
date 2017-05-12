package net.wit.exception;

/**
 * @ClassName：OrderException @Description：订单异常
 * @author：Chenlf
 * @date：2015年9月11日 下午4:54:02
 */
public class OrderException extends Exception {
	private static final long serialVersionUID = 9001L;

	public OrderException() {
		super();
	}

	public OrderException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrderException(String message) {
		super(message);
	}

	public OrderException(Throwable cause) {
		super(cause);
	}
}
