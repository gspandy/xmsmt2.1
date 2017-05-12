package net.wit.mobile.dao;

import net.wit.mobile.bo.AuthCode;




public interface  IAuthCodeDao {
	public boolean add(final AuthCode authCode);
	 public AuthCode get(final String keyId) ;
	 public boolean update(final AuthCode authCode);
	 public void remove(String token);
}
