package net.wit.dao.impl;

import org.springframework.stereotype.Repository;

import net.wit.dao.MessageDetailsDao;
import net.wit.entity.MessageDetails;

/**
 * @ClassName：MessageDetailsDaoImpl @Description：
 * @author：Chenlf
 * @date：2015年9月13日 下午10:46:31
 */
@Repository("messageDetailsDaoImpl")
public class MessageDetailsDaoImpl extends BaseDaoImpl<MessageDetails, Long>implements MessageDetailsDao {

}
