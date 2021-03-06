package amidar.sweep;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import amidar.AmidarSimulator;
import amidar.ConfMan;

/**
 * Holds all information of a sweep over parameters, FU-configurations (TODO) and applications
 * @author jung
 *
 */
public class SweepConfig {
	
	/**
	 * The dimension of the sweep
	 */
	private int dimensions = 0;
	
	/**
	 * The number of simulations of that sweep
	 */
	private int nrOfInstances;
	
	/**
	 * Map that stores the parameters that are sweeped as keys and the different manifestations in a map as value
	 */
	private LinkedHashMap<String,Set<Object>> sweeps;
	
	/**
	 * Holds all applications of the sweep in a Set. Needed for remote Manager to convert all applications to AXT on the remote host
	 */
	private HashSet<String> applications;
	
	/**
	 * Holds the manifestations of the swept parameters for all simulations
	 */
	private Object[][] instanceConfigurations;
	
	/**
	 * The configuration which is used as base for the swep
	 */
	private ConfMan baseConfiguration;
	
	/**
	 * The configurations of all simulations to be performed
	 */
	private ConfMan[] configManagers;
	
	private static final String SPEEDUPTAG_LONG = "_long";
	private static final String SPEEDUPTAG_SHORT = "_short";
	
	
	/**
	 * Creates configurations for all simulations of a sweep using a base configuration and a sweep configuration
	 * @param configManager the base configuration for all simulations
	 * @param sweepConfig the sweep configuration describing the parameters to be swept
	 */
	public SweepConfig(ConfMan configManager, String sweepConfig, boolean isSpeedupMeasurement){
		this.baseConfiguration = configManager;
		
		JSONParser parser = new JSONParser();
		sweeps = new LinkedHashMap<String,Set<Object>>();
		applications = new HashSet<String>();
		
		try {
			JSONObject sweep = (JSONObject) parser.parse(new FileReader(sweepConfig));
			
			JSONArray application = (JSONArray) sweep.get("application");
			if(application != null){
				int size = application.size();
				TreeSet<Object> apps = new TreeSet<Object>();
				for(int i = 0; i< size; i++){
					String appName = (String)application.get(i);
					if(!isSpeedupMeasurement){
						String convertedApp = AmidarSimulator.convertApplication(appName,null);
						applications.add(appName);
						apps.add(convertedApp);
					} else {
						String convertedApp = AmidarSimulator.convertApplication(appName+SPEEDUPTAG_LONG,null);
						applications.add(appName+SPEEDUPTAG_LONG);
						apps.add(convertedApp);
						String convertedApp2 = AmidarSimulator.convertApplication(appName+SPEEDUPTAG_SHORT,null);
						applications.add(appName+SPEEDUPTAG_SHORT);
						apps.add(convertedApp2);
					}
				}
				sweeps.put("application", apps);
			}
			
			JSONObject functionalUnits = (JSONObject) sweep.get("fu");
			if(functionalUnits != null){
				for(Object fuO: functionalUnits.keySet()){
					String fu = (String)fuO;
					JSONArray fuValues = (JSONArray) functionalUnits.get(fu);
					int size = fuValues.size();
					LinkedHashSet<Object>	 values = new LinkedHashSet<>();
					for(int i = 0; i < size; i++){
						values.add(fuValues.get(i));
					}
					sweeps.put(fu,values);
				}
			}
			
			JSONObject parameter = (JSONObject) sweep.get("parameter");
			if(parameter != null){
				for(Object paramO: parameter.keySet()){
					String param = (String)paramO;
					JSONArray paramValues = (JSONArray) parameter.get(param);
					int size = paramValues.size();
					TreeSet<Object> values = new TreeSet<Object>();
					for(int i = 0; i < size; i++){
						values.add(paramValues.get(i));
					}
					sweeps.put(param, values);
				}
			}
			
			
			
			dimensions = sweeps.size();
			
			nrOfInstances = 1;
			
			for(Set<Object> sw: sweeps.values()){
				nrOfInstances *= sw.size();
			}
			
			
			instanceConfigurations = new Object[nrOfInstances+1][dimensions];
			
			int dimSplit = 1;
			int dimCnt = 0;
			
			for(String p: sweeps.keySet()){
				Object[] values = sweeps.get(p).toArray();
				int nrOfValues = values.length;
				int valuePointer = 0;
				dimSplit *= nrOfValues;
				int bundleSize = nrOfInstances / dimSplit;
				int bundleCnt = 0;
				for(int i = 0; i < nrOfInstances; i++){
					instanceConfigurations[i][dimCnt] = values[valuePointer];
					bundleCnt++;
					if(bundleCnt == bundleSize){
						bundleCnt = 0;
						valuePointer++;
						if(valuePointer==nrOfValues){
							valuePointer = 0;
						}
					}
				}
				instanceConfigurations[nrOfInstances][dimCnt] = p;
				
				dimCnt++;
			}
			
			
			for(String c : getSweepConfigurations()){
				System.out.println(c);
			}
			createConfManager();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Returns textual description of all simulations of this sweep
	 * @return the configurations
	 */
	public String[] getSweepConfigurations(){
		String[] result = new String[nrOfInstances];
		int cnt = 0;
		
		for(int i = 0; i < nrOfInstances; i++){
			StringBuffer r = new StringBuffer();
			for(int dim = 0; dim < dimensions; dim++){
				r.append(instanceConfigurations[nrOfInstances][dim]);
				r.append(": ");
				r.append(instanceConfigurations[i][dim]);
				r.append("\t");
			}
			result[i] = r.toString();
		}

		
		
		return result;
	}
	
	/**
	 * Returns the names of all configurations of this sweep
	 * @return the names as string array
	 */
	public String[] getSweepConfigurationFileNames(){
		String[] result = new String[nrOfInstances];
		int cnt = 0;
		
		for(int i = 0; i < nrOfInstances; i++){
			StringBuffer r = new StringBuffer();
			for(int dim = 0; dim < dimensions; dim++){
				r.append(instanceConfigurations[nrOfInstances][dim]);
				r.append(":");
				r.append(instanceConfigurations[i][dim]);
				r.append("-");
			}
			result[i] = r.toString();
		}

		
		
		return result;
	}
	
	/**
	 * internal method to create all configurations of this sweep
	 */
	private void createConfManager(){
		configManagers = new ConfMan[nrOfInstances];
		for(int i = 0; i < nrOfInstances; i++){
			ConfMan c = baseConfiguration.getDeepCopy();
			
			for(int dim = 0; dim < dimensions; dim ++){
				if(instanceConfigurations[nrOfInstances][dim] == "application"){
					c.setApplication((String)instanceConfigurations[i][dim]);
				} else if(c.getSynthesisConfig().containsKey((String)instanceConfigurations[nrOfInstances][dim])){
					c.getSynthesisConfig().put((String)instanceConfigurations[nrOfInstances][dim], instanceConfigurations[i][dim]);
				} else if(c.getFuConfigFiles().containsKey((String)instanceConfigurations[nrOfInstances][dim])){
					c.getFuConfigFiles().put((String)instanceConfigurations[nrOfInstances][dim], (String)instanceConfigurations[i][dim]);
				}
			}
			
			
			
			configManagers[i] = c;
		}
		
	}
	
	/**
	 * Returns configurations of all simulations of this sweep
	 * @return
	 */
	public ConfMan[] getConfManager(){
		return configManagers;
	}
	
	/**
	 * Returns the names of all applications used in this sweep
	 * @return
	 */
	public String[] getApplications(){
		String[] result = new String[applications.size()];
		result = applications.toArray(result);
		return result;
	}
	
	public LinkedHashMap<String, Set<Object>> getSweeps(){
		return sweeps;
	}
	
	public Object[][] getInstanceConfigurations(){
		return instanceConfigurations;
	}

}
