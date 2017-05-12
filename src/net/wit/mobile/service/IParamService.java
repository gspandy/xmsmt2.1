package net.wit.mobile.service;

import net.wit.mobile.bo.Param;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午6:06
 * To change this template use File | Settings | File Templates.
 */
public interface IParamService {

    public List<Map<String, Object>> getParamAll();

    public String getParamValueByName(String name);

    public boolean addParam(Param param);

    public boolean updateParam(Param param);

    public boolean deleteParamByParamName(String paramName);

    public void refreshParamByValue(String value);

    public void refreshParamAll();

    public boolean updateParamAndRefreshCache(Param param);

    public boolean deleteParamAndRefreshCacheByParamName(String paramName);

}
