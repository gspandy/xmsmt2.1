package net.wit.mobile.dao.impl;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONUtils;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.dao.INtokenDao;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("ntokenDao")
public class NTokenDaoImpl extends AbstractBaseRedisDao<String, NToken> implements INtokenDao {

    public void remove(String token) {
        delete(token);
    }

	private String NTokenToString(final NToken token){
		JSONObject json = new JSONObject();
		json.put(NToken.USERID_KEY,token.getMemberId());
		json.put(NToken.TEL_KEY,token.getTel());
		json.put(NToken.EXPIREDTIME_KEY,token.getExpiredTime());
		json.put(NToken.IS_SHOP_KEEPER,token.getIsShopKeeper());
        json.put(NToken.TENANT_ID, token.getTenantId());
        json.put(NToken.RECOMMANDID, token.getRecommandId());
        json.put(NToken.HEAD_IMG, token.getHeadImg());
        json.put(NToken.NICKNAME, token.getNickName());
        json.put(NToken.LEDGERNO, token.getLedgerno());
        json.put(NToken.REALNAME, token.getRealName());
		return json.toString();
	}
	
	
	 public boolean add(final NToken token) {
	        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
	            public Boolean doInRedis(RedisConnection connection)  
	                    throws DataAccessException {
	                RedisSerializer<String> serializer = getRedisSerializer();  
	                byte[] key  = serializer.serialize(token.getNtoken()); 
	                byte[] value = serializer.serialize(NTokenToString(token));
	                return connection.setNX(key, value);
	            }  
	        });  
	        return result;  
	    }  
	      
	    /** 
	     * 批量新增 使用pipeline方式   
	     *<br>------------------------------<br> 
	     *@param list 
	     *@return 
	     */  
//	    public boolean add(final List<NToken> list) {  
//	        Assert.notEmpty(list);  
//	        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
//	            public Boolean doInRedis(RedisConnection connection)  
//	                    throws DataAccessException {  
//	                RedisSerializer<String> serializer = getRedisSerializer();  
//	                for (NToken NToken : list) {  
//	                    byte[] key  = serializer.serialize(NToken.getId());  
//	                    byte[] name = serializer.serialize(NToken.getName());  
//	                    connection.setNX(key, name);  
//	                }  
//	                return true;  
//	            }  
//	        }, false, true);  
//	        return result;  
//	    }  
	      
	    /**  
	     * 删除 
	     * <br>------------------------------<br> 
	     * @param key 
	     */  
	    public void delete(String key) {  
	        List<String> list = new ArrayList<String>();  
	        list.add(key);  
	        delete(list);  
	    }  
	  
	    /** 
	     * 删除多个 
	     * <br>------------------------------<br> 
	     * @param keys 
	     */  
	    public void delete(List<String> keys) {  
	        redisTemplate.delete(keys);  
	    }  
	  
	    /** 
	     * 修改  
	     * <br>------------------------------<br> 
	     * @param token
	     * @return  
	     */  
	    public boolean update(final NToken token) {
	        String key = token.getNtoken();
	        if (get(key) == null) {  
	            throw new NullPointerException("数据行不存在, key = " + key);  
	        }  
	        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
	            public Boolean doInRedis(RedisConnection connection)  
	                    throws DataAccessException {
	                RedisSerializer<String> serializer = getRedisSerializer();  
	                byte[] key  = serializer.serialize(token.getNtoken());  
	                byte[] value = serializer.serialize(NTokenToString(token));  
	                connection.set(key, value);
	                return true;  
	            }  
	        });  
	        return result;  
	    }  
	  
	    /**  
	     * 通过key获取 
	     * <br>------------------------------<br> 
	     * @param keyId 
	     * @return 
	     */  
	    public NToken get(final String keyId) {
	        NToken result = redisTemplate.execute(new RedisCallback<NToken>() {
	            public NToken doInRedis(RedisConnection connection)
	                    throws DataAccessException {
	                RedisSerializer<String> serializer = getRedisSerializer();  
	                byte[] key = serializer.serialize(keyId);  
	                byte[] value = connection.get(key);  
	                if (value == null) {  
	                    return null;  
	                }  
	                String js = serializer.deserialize(value);  
	                JSONObject jsobject = JSONObject.fromObject(js);
	                return new NToken(keyId, jsobject.getString(NToken.USERID_KEY),
	                		jsobject.getString(NToken.TEL_KEY),
	                		jsobject.getString(NToken.EXPIREDTIME_KEY),
	                		jsobject.getString(NToken.IS_SHOP_KEEPER),
                            jsobject.getString(NToken.TENANT_ID),
                            jsobject.getString(NToken.RECOMMANDID),
                            jsobject.getString(NToken.HEAD_IMG),
                            jsobject.getString(NToken.NICKNAME),
                            jsobject.getString(NToken.LEDGERNO),
                            jsobject.getString(NToken.REALNAME));
	            }  
	        });  
	        return result;  
	    }  
}
