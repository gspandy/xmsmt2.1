package net.wit.domain.impl;

import net.wit.domain.DistributionStrategy;
import net.wit.entity.Admin;
import net.wit.entity.Order;

import org.springframework.stereotype.Service;

@Service("distributionStrategy")
public class ToholaDistributionStrategy implements DistributionStrategy {

	public void distribution(Order order, Admin operator){
	}
}