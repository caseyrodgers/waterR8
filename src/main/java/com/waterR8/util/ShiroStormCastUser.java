package com.waterR8.util;

import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.Subject;

/** provide way to extract user information from 
 *   Shiro Stomcast object ... 
 *   
 *   TODO:  why is this not higher level on the Subject ..
 *   
 *   can we can an Account from a Subject?
 *  
 * @author casey
 *
 */
public class ShiroStormCastUser {
	
	private boolean authenticated;
	private Map<String, String> attributes;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ShiroStormCastUser(Subject subject) {
		authenticated = subject.isAuthenticated();
		
		// why?
		// must be better way to get this information out of stormcast.
		// TODO: find / write API to pull user meta data
		// 
		List names = subject.getPrincipals().asList();
		attributes = (Map<String, String>)names.get(1);
	}
	
	public String getGivenName() {
		return attributes.get("givenName");
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
}
