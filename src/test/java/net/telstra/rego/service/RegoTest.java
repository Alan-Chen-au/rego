package net.telstra.rego.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import net.telstra.rego.entity.Car;

public class RegoTest {
	
	private static Rego rego;
	
	@BeforeClass
	public static void init() {
		rego = Rego.getInstance();
	}
	
	@Test
	public void singleInstanceTest() {
		Rego rego1 = Rego.getInstance();
		Rego rego2 = Rego.getInstance();
		assertEquals("get two different instances. ", rego1, rego2);
	}
	
	@Test
	public void getTest() {
		Car car = rego.get("ABC124"); 
		assumeNotNull(car);
		assertEquals("car id is not equal.", "2", car.getId() );
		assertEquals("make is not equal", "Volkswagen", car.getMake()); 
		assertEquals("model is not equal", "Passat", car.getModel()); 
		assertEquals("year is not equal", 2012, car.getYear()); 
	}
	
	@Test
	public void get2Test() {
		Car car = rego.get("abc"); 
		assertEquals("should not find one car", null, car);
	}
	
	@Test
	public void getByCarIdTest() {
		
		Car car = rego.getByCarId("2"); 
		assumeNotNull(car);
		assertEquals("car id is not equal.", "2", car.getId() );
		assertEquals("make is not equal", "Volkswagen", car.getMake()); 
		assertEquals("model is not equal", "Passat", car.getModel()); 
		assertEquals("year is not equal", 2012, car.getYear()); 
	}
	
	@Test
	public void registerTest() {
		String carId = "6000";
		String make = "AUDI"; 
		String model = "TT"; 
		int year = 2020;
		
		Car car = new Car(carId, make, model, year); 
		String registrationId = "MMM123"; 
		boolean result = rego.register(registrationId, car);
		assertTrue("cannot register a car. ", result);
		Car c = rego.get(registrationId); 
		assertEquals("car id is not equal.", carId, c.getId() );
		assertEquals("make is not equal", make, c.getMake()); 
		assertEquals("model is not equal", model, c.getModel()); 
		assertEquals("year is not equal", year, c.getYear()); 
	}
	
	@Test
	public void register2Test() {
		String registrationId = "kkk123"; 
		String carId = "5006"; 
		rego.register(registrationId, carId);
		Car c1 = rego.get(registrationId); 
		Car c2 = rego.getByCarId(carId); 
		assertEquals("car id is not equal.", c2.getId(), c1.getId() );
		assertEquals("make is not equal", c2.getMake(), c1.getMake()); 
		assertEquals("model is not equal", c2.getModel(), c1.getModel()); 
		assertEquals("year is not equal", c2.getYear(), c1.getYear()); 
	}
	
	@Test
	public void removeTest() {
		String registrationId = "NNN123"; 
		String carId = "5076"; 
		boolean result = rego.register(registrationId, carId);
		assertTrue("cannot register a car. ", result);
		
		rego.remove(registrationId); 
		
		Car car = rego.get(registrationId); 
		assertEquals("should not find one car", null, car);
	}
	
	// @Test // do not enable this test. It will cause other test cases fail. 
	// Because the method removes all the entities in the map. 
	// Until add the test order; it should be the last one. 
	public void destroyTest() {
		
		String registrationId = "NNN123"; 
		String carId = "5076"; 
		boolean result = rego.register(registrationId, carId);
		assertTrue("cannot register a car. ", result);
		
		rego.destroy(); 
		
		Car car = rego.get(registrationId); 
		assertEquals("should not find one car", null, car);
		
	}
}
