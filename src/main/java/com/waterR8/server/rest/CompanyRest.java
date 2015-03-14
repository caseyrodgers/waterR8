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
import com.waterR8.model.ComplexContact;
import com.waterR8.model.Gateway;
import com.waterR8.model.Sensor;
import com.waterR8.model.SequenceInfo;
import com.waterR8.model.Unit;
import com.waterR8.server.dao.CompanyDao;
import com.waterR8.server.dao.GatewayDao;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")

public class CompanyRest implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(CompanyRest.class.getName());

    @POST
    @GET
    @Path("/companies")
    public String getCompanies() throws Exception {
    	List<Company> companies = CompanyDao.getInstance().getCompanies();
    	
//    	 final ObjectMapper mapper = JsonFactory.create();
//    	 String json = mapper.toJson(companies);
//    	 Object o = mapper.fromJson(json, List.class);
    	
    	
        return JsonWriter.objectToJson(companies);
    }
    
    @GET
    @Path("company/delete/{companyId}")
    public String processCompanyDelete(@PathParam("companyId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteCompany(id));
	}

    @POST
    @Path("company/update")
    public String processCompanyUpdate(String data) throws Exception  {
    	JSONObject jo = new JSONObject(data);
    	JSONObject jo2 = jo.getJSONObject("company");
    	jo2.put("@type", "com.waterR8.model.Company");
        Company company = (Company)JsonReader.jsonToJava(jo2.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().updateCompany(company));
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
    @Path("complex/delete/{complexId}")
    public String deleteComplex(@PathParam("complexId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteComplex(id));
	}

    
    @POST
    @Path("complex/add")
    public String processComplexAdd(String data) throws Exception  {
    	return processComplexAddAux(data);
	}
    
    @POST
    @Path("complex/update")
    public String processComplexUpdate(String data) throws Exception  {
    	JSONObject jo = new JSONObject(data);
    	JSONObject jo2 = jo.getJSONObject("complex");
    	jo2.put("@type", "com.waterR8.model.Complex");
        Complex complex = (Complex)JsonReader.jsonToJava(jo2.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().updateComplex(complex));
    }

    @POST
    @Path("complex/gateway/update")
    public String processComplexGatewayUpdate(String data) throws Exception  {
    	return processComplexGatewayUpdateAux(data);
    }
    
     

	private String processComplexGatewayUpdateAux(String data) throws Exception {
    	JSONObject jo = new JSONObject(data);
    	int complex = jo.getInt("complex");
    	JSONObject jo2 = jo.getJSONObject("gateway");
    	jo2.put("@type", "com.waterR8.model.Gateway");
    	Gateway gateway = (Gateway)JsonReader.jsonToJava(jo2.toString());
    	return JsonWriter.objectToJson(GatewayDao.getInstance().updateComplexGateway(complex, gateway));		
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
    
    @POST
    @Path("unit/update")
    public String updateUnit(String data) throws Exception  {
    	JSONObject jo = new JSONObject(data);
    	JSONObject jo2 = jo.getJSONObject("unit");
    	jo2.put("@type", "com.waterR8.model.Unit");
        Unit complex = (Unit)JsonReader.jsonToJava(jo2.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().updateUnit(complex));
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

    
    @POST
    @Path("sensor/update")
    public String updateSensor(String data) throws Exception  {
    	JSONObject jo = new JSONObject(data);
    	JSONObject jo2 = jo.getJSONObject("sensor");
    	jo2.put("@type", "com.waterR8.model.Sensor");
        Sensor sensor = (Sensor)JsonReader.jsonToJava(jo2.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().updateSensor(sensor));
    }    
    
    
    @GET
    @Path("sensor/delete/{sensorId}")
    public String deleteSensor(@PathParam("sensorId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().deleteSensor(id));
	}

    
    
    
    @GET
    @POST
    @Path("company/network/{companyId}")
    public String getNetworkMapForCompany(@PathParam("companyId") int id, String dataJson) throws Exception  {
    	SequenceInfo selectedSequence=null;
    	if(dataJson != null && dataJson.length() > 0) {
    		selectedSequence = (SequenceInfo)JsonReader.jsonToJava(dataJson);
    	}
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getCompanyMapForCompany(id, selectedSequence));
	}
    @GET
    @Path("complex/network/{complexId}")
    public String getNetworkMapForComplex(@PathParam("complexId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getCompanyMapForComplex(id));
	}
    
    @GET
    @Path("unit/network/{unitId}")
    public String getNetworkMapForUnit(@PathParam("unitId") int id) throws Exception  {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getCompanyMapForUnit(id));
	}
    

    
    @POST
    @Path("gateway/command") 
    public String doGatewayCommand(String jsonData) throws Exception {
    	return doGatewayCommand_Aux(jsonData);
    }

	private String doGatewayCommand_Aux(String jsonData) throws Exception {
    	JSONObject jo = new JSONObject(jsonData);
    	int commandId = jo.getInt("command");
    	int complexId = jo.getInt("complex");
    	
        return JsonWriter.objectToJson(GatewayDao.getInstance().makeCommandRequest(complexId, commandId));    	
	}


    
    @GET
    @Path("gateway/available/{complexId}") 
    public String doGetAvailableGateways(@PathParam("complexId") int complexId) throws Exception {
    	return JsonWriter.objectToJson(GatewayDao.getInstance().getAvailableGateways(complexId));
    }

    @GET
    @Path("complex/contacts/{complexId}") 
    public String doGetComplexContacts(@PathParam("complexId") int complexId) throws Exception {
    	return JsonWriter.objectToJson(CompanyDao.getInstance().getComplexContacts(complexId));
    }
    
    @POST
    @Path("complex/contacts/add") 
    public String doAddComplexContact(String jsonData) throws Exception {
    	return doAddComplexContactAux(jsonData);
    }
    
    private String doAddComplexContactAux(String jsonData) throws Exception {
    	JSONObject jo = new JSONObject(jsonData);
    	jo.put("@type", "com.waterR8.model.ComplexContact");
    	ComplexContact contact = (ComplexContact)JsonReader.jsonToJava(jo.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().addComplexContact(contact));
    }
    
    @POST
    @Path("complex/contacts/update") 
    public String doUpdateComplexContact(String jsonData) throws Exception {
    	return doUpdateComplexContactAux(jsonData);
    }
    
    private String doUpdateComplexContactAux(String jsonData) throws Exception {
    	JSONObject jo = new JSONObject(jsonData);
    	jo.put("@type", "com.waterR8.model.ComplexContact");
    	ComplexContact contact = (ComplexContact)JsonReader.jsonToJava(jo.toString());
        
    	return JsonWriter.objectToJson(CompanyDao.getInstance().updateComplexContact(contact));
    }
    

}

