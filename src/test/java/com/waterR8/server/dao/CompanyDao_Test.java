package com.waterR8.server.dao;

import junit.framework.TestCase;

import com.waterR8.model.Company;
import com.waterR8.model.ComplexDetails;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.UnitDetails;

public class CompanyDao_Test extends TestCase {
	
	public CompanyDao_Test(String name) throws Exception {
		super(name);
	}
	
	public void testCreate() throws Exception {
		assert(CompanyDao.getInstance() != null);
	}
	
	public void testGetCompany() throws Exception {
		Company company = CompanyDao.getInstance().getCompany(1);
		assertNotNull(company);
		assertNotNull(company.getCompanyName());
	}

	public void testGetComplex() throws Exception {
		ComplexDetails complex = CompanyDao.getInstance().getComplexDetails(2);
		assertNotNull(complex);
		assertNotNull(complex.getCompany());
		assertNotNull(complex.getUnits().size() > 0);
	}
	
	public void testGetUnitDetails() throws Exception {
		UnitDetails details = CompanyDao.getInstance().getUnitDetails(1);
		assertNotNull(details);
		assertNotNull(details.getSensors());
		assertTrue(details.getSensors().size() > 0);
		assertNotNull(details.getUnit().getUnitNumber());
	}
	
	public void testGetSensorDetails() throws Exception {
		SensorDetails details = CompanyDao.getInstance().getSensorDetail(2);
		assertNotNull(details);
		assertTrue(details.getCompany().getId() > 0);
		assertTrue(details.getSensor().getId() > 0);
	}
}
