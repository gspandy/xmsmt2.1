package net.wit.mobile.dao;

import net.wit.mobile.bo.Param;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午5:47
 * To change this template use File | Settings | File Templates.
 */
public interface IParamDao {
    public List<Map<String, Object>> getParamAll();

    public String getParamValueByName(String name);

    public boolean addParam(Param param);

    public boolean updateParam(Param param);

    public boolean deleteParamByParamName(String paramName);
}
