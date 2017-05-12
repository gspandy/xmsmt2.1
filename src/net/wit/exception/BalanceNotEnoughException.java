package net.wit.exception;

public class BalanceNotEnoughException extends Exception {

		private static final long	serialVersionUID	= 9001L;

		public BalanceNotEnoughException() {
			super();
		}

		public BalanceNotEnoughException(String message, Throwable cause) {
			super(message, cause);
		}

		public BalanceNotEnoughException(String message) {
			super(message);
		}

		public BalanceNotEnoughException(Throwable cause) {
			super(cause);
		}
}
