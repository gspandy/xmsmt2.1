/**
 *====================================================
 * 文件名称: BankInfoService.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月4日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.BankInfo;

/**
 * @ClassName: BankInfoService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午5:38:16
 */
public interface BankInfoService extends BaseService<BankInfo, Long> {

	List<BankInfo> findListByNo(String cardNo);

}
