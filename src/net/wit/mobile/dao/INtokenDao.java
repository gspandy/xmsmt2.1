package net.wit.mobile.dao;


import net.wit.mobile.bo.NToken;

public interface  INtokenDao {
	
	public NToken get(String keyId) ;
	public boolean add(NToken token) ;
	 public boolean update(final NToken token);
    public void remove(String token);
}
