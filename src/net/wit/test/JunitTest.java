package net.wit.test;
import java.util.Calendar;
import java.util.Date;

import net.wit.dao.BatchJobDao;
import net.wit.service.BatchJobService;
import net.wit.util.DateUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "file:resource/applicationContext.xml")
@ContextConfiguration(locations = { "classpath*:/applicationContext.xml", "classpath*:/applicationContext-shiro.xml","classpath*:/applicationContext-redis.xml" })
public class JunitTest extends AbstractJUnit4SpringContextTests{

	@Autowired
	private BatchJobService batchJobService;
	
	@Autowired
	private BatchJobDao batchJobDao;
	
	@Test
    public void test()  {
//		batchJobService.calculateRent(new Date());
		batchJobDao.findOrderReturn(new Date());
	}
	public static void main(String[] args) {
		Date startDate = DateUtil.addMonth(new Date(), 2);
		System.out.println(startDate);
		Calendar cal=Calendar.getInstance();
		cal.setTime(startDate);
		System.out.println(DateUtil.setLastDayOfMonth(cal));
	}
}
