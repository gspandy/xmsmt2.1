package net.wit.util;

/**
 * 二元组类
 * @author Administrator
 *
 * @param <A>
 * @param <B>
 */
public class TupleTwo<A, B> {
	public final A first;
	public final B second;

	public TupleTwo(A a, B b) {
		first = a;
		second = b;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
