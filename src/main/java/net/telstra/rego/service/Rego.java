package net.telstra.rego.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

import net.telstra.rego.entity.Car;

public class Rego {
	
	// ConcurrentHashMap init parameters 
	private static int initialCapacity 	= 1000; 
    private static float loadFactor 	= 0.75f;  
    private static int concurrencyLevel	= 32; 
    
    // Hash map for regos; registration id --> car id
    // will be used in the multiple threads environment 
	private static ConcurrentHashMap<String, String> regoMap; 
	
	// will be used in the multiple threads environment 
	// Hash map for cars; car id --> car details
	private static ConcurrentHashMap<String, Car> carMap; 
	
	// singleton instance for this class; because it will occupy many resources
	// will be used in the multiple threads environment 
	private volatile static Rego instance; 
	
	// private constructor; only use getInstance method
	private Rego() {
		regoMap = new ConcurrentHashMap<String, String>(initialCapacity, loadFactor, concurrencyLevel); 
		carMap = new ConcurrentHashMap<String, Car>(initialCapacity, loadFactor, concurrencyLevel); 
		init();
	}
	
	// entry point to get this singleton instance.
	public static Rego getInstance() {
		if (instance == null ) {
			synchronized (Rego.class) {
				if (instance == null) {
					instance = new Rego();
				}
			}
		}
		
		return instance; 
	}
	
	// get car detail by registration id; return null if without register it.
	// you can get the car detail by car id; use the the this method getByCarId. 
	public Car get(String registerationId) {
		String carId = regoMap.get(registerationId); 
		return carId != null ? carMap.get(carId) : null; 
	}
	
	// get car detail by car id;
	// return null if cannot find it. 
	public Car getByCarId(String carId) {
		return carMap.get(carId); 
	}
	
	// register a car or a new car; 
	// return false; if this car has been registered.
	// return true; if this car has been registered. 
	public boolean register(String registerationId, Car car) {
		// make sure registerationId & car are valid
		// and this car has not been registered. 
		if (isValid(registerationId) && car != null && !regoMap.containsValue(car.getId())) {
			// insert it into rego map
			regoMap.put(registerationId, car.getId());
			// insert it into car map if this is a new car
			if (carMap.get(car.getId()) == null) {
				carMap.put(car.getId(), car); 
			}
			return true; 
		}
		
		return false; 
	}
	
	// register an existing car with car id
	// return false; if this car has been registered.
	// otherwise return true. 
	public boolean register(String registerationId, String carId) {
		// make sure both registerationId and carId are valid
		// and this car has not been registered & this car exists in the carMap
		if (isValid(registerationId) && isValid(carId) 
			&& !regoMap.containsValue(carId) && carMap.get(carId) != null) {
				regoMap.put(registerationId, carId); 
				return true;
		}
		
		return false; 
	}
	
	// remove an entry
	public void remove(String registerationId) {
		regoMap.remove(registerationId); 
	}
	
	// release all resources
	public void destroy() {
		carMap.clear(); 
		regoMap.clear();
	}
	
	// pre-load both rego and car data from resources fold
	private void init() {
		// load car data from resources fold
		loadCarData("car_specs.csv");
        // load rego data from resources fold
		loadRegoData("cars_regos.csv");
	}
	
	// load car data from resources fold
	private void loadCarData(String file) {
		String line = "";
        try ( 	InputStreamReader inputReader = new InputStreamReader(Rego.class.getResourceAsStream("/" + file)); 
        		BufferedReader br = new BufferedReader(inputReader) ) {
        	// discard the first line : title 
        	br.readLine(); 
        	Car car = null;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] items = line.split(",");
                if (items != null && items.length == 18 
                	&& isValid(items[0]) && isValid(items[13]) 
                	&& isValid(items[14]) && isValid(items[15])) {
                	
            		car = new Car(items[0], items[13], items[14], Integer.valueOf(items[15]));
            		carMap.put(items[0], car);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	// load rego data from resources fold
	private void loadRegoData(String file) {
		String line = "";
        try ( 	InputStreamReader inputReader = new InputStreamReader(Rego.class.getResourceAsStream("/" + file)); 
        		BufferedReader br = new BufferedReader(inputReader) ) {
        	// discard the first line : title 
        	br.readLine(); 
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] items = line.split(",");
                if (items != null && items.length == 2 
                	&& isValid(items[0]) && isValid(items[1])) {
                	regoMap.put(items[0], items[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private boolean isValid(String value) {
		return value != null && !"".equals(value); 
	}
}
