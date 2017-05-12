package net.wit.domain;

import net.wit.entity.Admin;
import net.wit.entity.Order;

public interface DistributionStrategy {

	/** 利润分配 */
	public void distribution(Order order, Admin operator);
}
