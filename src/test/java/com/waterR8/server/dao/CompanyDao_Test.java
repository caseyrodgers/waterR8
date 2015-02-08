package com.waterR8.server.dao;

import java.sql.Connection;

import junit.framework.TestCase;

import com.waterR8.model.Company;
import com.waterR8.model.CompanyDetails;
import com.waterR8.model.CompanyNetworkMap;
import com.waterR8.model.ComplexDetails;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.SensorNetworkStatus;
import com.waterR8.model.UnitDetails;
import com.waterR8.server.ConnectionPool;
import com.waterR8.util.SqlUtilities;

public class CompanyDao_Test extends TestCase {
	
	public CompanyDao_Test(String name) throws Exception {
		super(name);
	}
	
	public void testCreate() throws Exception {
		assert(CompanyDao.getInstance() != null);
	}
	
	
	public void testGetCompanyMapForCompany() throws Exception {
		CompanyNetworkMap networkMap = CompanyDao.getInstance().getCompanyMapForCompany(14);
		assertNotNull(networkMap);
		assertTrue(networkMap.getNetworkNodes().size() > 0);
	}
	
	public void testGetCompanyMapForComplex() throws Exception {
		CompanyNetworkMap networkMap = CompanyDao.getInstance().getCompanyMapForComplex(5);
		assertNotNull(networkMap);
		assertTrue(networkMap.getNetworkNodes().size() > 0);
	}
	
	public void testNetworkStatus() throws Exception {
		Connection conn=null;
		try {
			conn = ConnectionPool.getConnection();
			int company=14;
			int complex=2;
			int unit=2;
			int sensor=1;
			SensorNetworkStatus status = CompanyDao.getInstance().getNetworkStatus(conn, company, complex, unit, sensor);
			assertNotNull(status);
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	
	public void testGetCompany() throws Exception {
		Company company = CompanyDao.getInstance().getCompany(1);
		assertNotNull(company);
		assertNotNull(company.getCompanyName());
	}
	
	public void testGetCompanyDetails() throws Exception {
		CompanyDetails details = CompanyDao.getInstance().getCompanyDetails(1);
		assertNotNull(details);
		assertNotNull(details.getNetworkStatus());
	}

	public void testGetComplex() throws Exception {
		ComplexDetails complex = CompanyDao.getInstance().getComplexDetails(2);
		assertNotNull(complex);
		assertNotNull(complex.getCompany());
		assertNotNull(complex.getUnits().size() > 0);
	}
	
	public void testGetComplexDetails() throws Exception {
		ComplexDetails details = CompanyDao.getInstance().getComplexDetails(1);
		assertNotNull(details);
		assertTrue(details.getComplex().getId() > 0);
		assertNotNull(details.getNetworkStatus());
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
		assertTrue(details.getSensor().getRole() != null);
	}
}
