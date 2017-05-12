package net.wit.mobile.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import net.sf.json.JSONObject;
import net.wit.mobile.bo.AuthCode;
import net.wit.mobile.dao.IAuthCodeDao;
@Repository("authCodeDaoImpl")
public class AuthCodeDaoImpl extends AbstractBaseRedisDao<String, AuthCode> implements IAuthCodeDao{
	
	private String AuthCodeToString(final AuthCode authCode){
		JSONObject json = new JSONObject();
		json.put(AuthCode.TEL,authCode.getTel());
		json.put(AuthCode.CODE,authCode.getCode());
		json.put(AuthCode.CURRENTTIMEMILLIS,authCode.getCurrentTimeMillis());
		return json.toString();
	}
	
	 public boolean add(final AuthCode authCode) {
	        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
	            public Boolean doInRedis(RedisConnection connection)  
	                    throws DataAccessException {
	                RedisSerializer<String> serializer = getRedisSerializer();  
	                byte[] key  = serializer.serialize(authCode.getTel()); 
	                byte[] value = serializer.serialize(AuthCodeToString(authCode));
	                return connection.setNX(key, value);
	            }  
	        });  
	        return result;  
	    }  
	
	
    public AuthCode get(final String keyId) {
    	AuthCode result = redisTemplate.execute(new RedisCallback<AuthCode>() {
            public AuthCode doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key = serializer.serialize(keyId);  
                byte[] value = connection.get(key);  
                if (value == null) {  
                    return null;  
                }  
                String js = serializer.deserialize(value);  
                JSONObject jsobject = JSONObject.fromObject(js);
                return new AuthCode(jsobject.getString(AuthCode.TEL), jsobject.getString(AuthCode.CODE),
                		Long.valueOf(jsobject.getString(AuthCode.CURRENTTIMEMILLIS)));
            }  
        });  
        return result;  
    }  
    
    
    public boolean update(final AuthCode authCode) {
        String key = authCode.getTel();
        if (get(key) == null) {  
            throw new NullPointerException("数据行不存在, key = " + key);  
        }  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            public Boolean doInRedis(RedisConnection connection)  
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key  = serializer.serialize(authCode.getTel());  
                byte[] value = serializer.serialize(AuthCodeToString(authCode));  
                connection.set(key, value);
                return true;  
            }  
        });  
        return result;  
    }  
    
    public void remove(String token) {
        delete(token);
    }
    
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
  
}
