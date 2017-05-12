package net.wit.mobile.service.impl;

import net.wit.mobile.bo.Param;
import net.wit.util.CacheUtil;
import net.wit.mobile.dao.IParamDao;
import net.wit.mobile.service.IParamService;
import net.wit.service.impl.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午6:07
 * To change this template use File | Settings | File Templates.
 */
@Service("paramService")
public class ParamServiceImpl extends BaseServiceImpl<Param, String> implements IParamService {

    @Autowired
    private IParamDao iParamDao;

    public List<Map<String, Object>> getParamAll() {
        return iParamDao.getParamAll();
    }

    public String getParamValueByName(String name) {
        return iParamDao.getParamValueByName(name);
    }

    public boolean addParam(Param param) {
        return iParamDao.addParam(param);
    }

    public void refreshParamByValue(String value) {
        CacheUtil.refreshParamByValue(value);
    }

    public void refreshParamAll() {
        List<Map<String, Object>> list = getParamAll();
        if (CollectionUtils.isNotEmpty(list)) {
            String value;
            for (Map<String, Object> map : list) {
                value = (String) map.get("param_value");
                if (StringUtils.isNotEmpty(value)) {
                    refreshParamByValue(value);
                }
            }
        }
    }

    public boolean updateParam(Param param) {
        return iParamDao.updateParam(param);
    }

    public boolean deleteParamByParamName(String paramName) {
        return iParamDao.deleteParamByParamName(paramName);
    }

    public boolean updateParamAndRefreshCache(Param param) {
        if (param != null) {
            String paramName = param.getParamName();
            if (StringUtils.isNotEmpty(paramName)) {
                refreshParamByValue(paramName);
            }
        }

        return updateParam(param);
    }

    public boolean deleteParamAndRefreshCacheByParamName(String paramName) {
        refreshParamByValue(paramName);
        return deleteParamByParamName(paramName);
    }
}
