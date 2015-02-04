package com.waterR8.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.waterR8.server.rest.CompanyRest;

public class WaterR8Application  extends Application {
	    private Set<Object> singletons = new HashSet<Object>();

	    public WaterR8Application() {
	        singletons.add(new CompanyRest());
	    }

	    @Override
	    public Set<Object> getSingletons() {
	        return singletons;
	    }
	
}
