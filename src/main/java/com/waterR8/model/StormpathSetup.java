package com.waterR8.model;

import java.util.Map;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.tenant.Tenant;

public class StormpathSetup {
	
	public StormpathSetup() throws Exception {
		String path = System.getProperty("user.home") + "/stormpath.properties";

		ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();
		Client client = Clients.builder().setApiKey(apiKey).build();	

		
		Tenant tenant = client.getCurrentTenant();
		ApplicationList applications = tenant.getApplications(
		        Applications.where(Applications.name().eqIgnoreCase("waterR8"))
		);
		Application application = applications.iterator().next();
		
		System.out.println("My App: " +  application);
		
		
		Account account = client.instantiate(Account.class);
		
		

		//Set the account properties
		account.setGivenName("Steve");
		account.setSurname("Smith");
		account.setUsername("steve"); //optional, defaults to email if unset
		account.setEmail("stevesmith@waterr8.com");
		account.setPassword("rotorsmithA1");
		CustomData customData = account.getCustomData();
		customData.put("favoriteColor", "white");
		
		GroupList groups = account.getGroups();

		//Create the account using the existing Application object
		Account user=null;
		for(Account a: application.getAccounts()		) {
			if(a.getUsername().equals("steve")) {
				user = a;
				break;
			}
		}
		
		System.out.println("user account: " + user);
		
	}
	
	static public void main(String as[]) {
		try {
			new StormpathSetup();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
