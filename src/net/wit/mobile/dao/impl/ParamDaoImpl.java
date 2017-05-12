package net.wit.mobile.dao.impl;

import net.wit.dao.impl.BaseDaoImpl;
import net.wit.mobile.bo.Param;
import net.wit.mobile.dao.IParamDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午5:48
 * To change this template use File | Settings | File Templates.
 */
@Repository("paramDaoImpl")
public class ParamDaoImpl extends BaseDaoImpl<Param, Long> implements IParamDao {

    private static final String TAB_NAME = "t_plat_param";

    private static final String GET_PARAM_ALL = "select * from "+TAB_NAME;

    private static final String GET_PARAM_BY_NAME = "select param_value from "+TAB_NAME+" where param_name=?";

    private static final String DELETE_PARAM_BY_PARAM_NAME = "delete from "+TAB_NAME+" where param_name=?";

    @Override
    public List<Map<String, Object>> getParamAll() {
        Session session = (Session) entityManager.getDelegate();
        if (session != null) {
            return session.createSQLQuery(GET_PARAM_ALL).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getParamValueByName(String name) {
        Session session = (Session) entityManager.getDelegate();
        if (session != null) {
            List<Map<String, Object>> list = session.createSQLQuery(GET_PARAM_BY_NAME).setParameter(0, name).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
            if (CollectionUtils.isNotEmpty(list)) {
                return (String) list.get(0).get("param_value");
            }
        }
        return "";
    }

    public boolean addParam(Param param) {
        boolean flag = true;
        try {
            entityManager.persist(param);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public boolean updateParam(Param param) {
        boolean flag = true;
        try {
            entityManager.merge(param);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public boolean deleteParamByParamName(String paramName) {
        boolean flag = true;
        try {
            Session session = (Session) entityManager.getDelegate();
            if (session != null) {
                session.createSQLQuery(DELETE_PARAM_BY_PARAM_NAME).setParameter(0, paramName).executeUpdate();
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}
