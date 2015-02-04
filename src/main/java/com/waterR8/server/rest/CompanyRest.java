package com.waterR8.server.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.waterR8.model.Company;
import com.waterR8.server.dao.CompanyDao;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")

public class CompanyRest implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(CompanyRest.class.getName());

    @POST
    @GET
    @Path("/companies")
    public String getCompanies() throws Exception {
    	List<Company> companies = CompanyDao.getInstance().getCompanies();
        return JsonWriter.objectToJson(companies);
    }
    
    @GET
    @Path("company/{companyId}")
    public String getCompayDetails(@PathParam("companyId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getCompanyDetails(id));
	}
    
    @GET
    @Path("complex/{complexId}")
    public String getComplexDetails(@PathParam("complexId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getComplexDetails(id));
	}
    
    @GET
    @Path("unit/{unitId}")
    public String getUnitDetails(@PathParam("unitId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getUnitDetails(id));
	}
    
    @GET
    @Path("sensor/{sensorId}")
    public String getSensorDetails(@PathParam("sensorId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getSensorDetail(id));
	}

    @POST
    @Path("company/add")
    public String processCompanyAdd(String data) throws Exception  {
    	return processCompanyAddAux(data);
	}

	private String processCompanyAddAux(String data) throws Exception  {
		
		// convert back into a Company object
		JSONObject jo = new JSONObject(data);
		jo.put("@type", "com.waterR8.model.Company");
        Company company = (Company)JsonReader.jsonToJava(jo.toString());
    	
    	return JsonWriter.objectToJson(CompanyDao.getInstance().addCompany(company));
	}
	
	
    @GET
    @Path("company/delete/{companyId}")
    public String processCompanyDelete(@PathParam("companyId") int id) throws Exception  {
    	return processCompanyDeleteAux(id);
	}

	private String processCompanyDeleteAux(int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteCompany(id));
	}

    

}

