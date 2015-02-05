package com.waterR8.server.rest;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.waterR8.model.Company;
import com.waterR8.model.Complex;
import com.waterR8.model.Sensor;
import com.waterR8.model.Unit;
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
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteCompany(id));
	}
    
    @GET
    @Path("complex/delete/{complexId}")
    public String deleteComplex(@PathParam("complexId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteComplex(id));
	}

    
    @POST
    @Path("complex/add")
    public String processComplexAdd(String data) throws Exception  {
    	return processComplexAddAux(data);
	}

	private String processComplexAddAux(String data) throws Exception  {
		// convert back into a Company object
		JSONObject jo = new JSONObject(data);
		jo.put("@type", "com.waterR8.model.Complex");
        Complex complex = (Complex)JsonReader.jsonToJava(jo.toString());
    	
    	return JsonWriter.objectToJson(CompanyDao.getInstance().addComplex(complex));
	}

    @POST
    @Path("unit/add")
    public String processUnitAdd(String data) throws Exception  {
    	return processUnitAddAux(data);
	}

	private String processUnitAddAux(String data) throws Exception  {
		// convert back into a Company object
		JSONObject jo = new JSONObject(data);
		jo.put("@type", "com.waterR8.model.Unit");
		Unit unit = (Unit)JsonReader.jsonToJava(jo.toString());
    	
    	return JsonWriter.objectToJson(CompanyDao.getInstance().addUnit(unit));
	}
    
    @GET
    @Path("unit/delete/{unitId}")
    public String deleteUnit(@PathParam("unitId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteUnit(id));
	}

    @POST
    @Path("sensor/add")
    public String addSensor(String data) throws Exception  {
    	JSONObject jo = new JSONObject(data);
    	jo.put("@type", "com.waterR8.model.Sensor");
    	Sensor sensor = (Sensor)JsonReader.jsonToJava(jo.toString());
    	return JsonWriter.objectToJson(CompanyDao.getInstance().addSensor(sensor));
	}

    @GET
    @Path("sensor/delete/{sensorId}")
    public String deleteSensor(@PathParam("sensorId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteSensor(id));
	}


}

