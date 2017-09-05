package Models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;


public class ModelLoader {
	
	private HashMap<String, File> modelFiles;
	private HashMap<String, Model> models;
	HashMap<String, File[]> corr;
	
	
	public void loadModelsDefaultPath(){
		modelFiles = new HashMap<String, File>();
		models = new HashMap<String, Model>();
		corr = new HashMap<String, File[]>();
		
		// load basic models
        File dirToOpen = null;
        try {
        	dirToOpen = new File("Models");
        } catch (IllegalArgumentException iae) {
            System.out.println("File Not Found");
        }
        File[] l = dirToOpen.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(".xmi");
			}
		});
        for(File c : l){
        	modelFiles.put(c.getName(), c);
        	loadModelFromXMI(c);
        }
        // load correlation + models
        try {
        	dirToOpen = new File("Correlations");
        } catch (IllegalArgumentException iae) {
            System.out.println("File Not Found");
        }
        File[] l2 = dirToOpen.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(".xmi");
			}
		});
        for(File c : l2){
        	modelFiles.put(c.getName(), c);
        	loadModelFromXMI(c);
        }
        
        for(String current : corr.keySet()){
        	File[] files = corr.get(current);
        	String[] reqImplNames = current.split("-");
        	String req = reqImplNames[0]+".xmi";
        	String impl = reqImplNames[1]+".xmi";
        	String reqMapped = current+"-req.xmi";
        	String implMapped = current+"-impl.xmi";
        	try {
				models.put(current, new CorrelationModel(files[0], files[1], models.get(impl), models.get(req), models.get(implMapped),models.get(reqMapped)));
				models.remove(reqMapped);
				models.remove(implMapped);
				modelFiles.remove(reqMapped);
				modelFiles.remove(implMapped);
				String path = models.get(current).getFile().getCanonicalPath();
				saveGraphAsPNG(path.substring(0, path.length()-4)+".png",models.get(current));
				saveGraphAsPNG(path.substring(0, path.length()-4)+".svg",models.get(current));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
	}
	
	private void loadModelFromXMI(File file){
		String type = getFileType(file);
		
		if(type.equals("implementation")){
			try {
				models.put(file.getName(), new ImplementationModel(file));
				saveGraphAsPNG(file.getCanonicalPath().substring(0, file.getCanonicalPath().length()-4)+".png",models.get(file.getName()));
				saveGraphAsSVG(file.getCanonicalPath().substring(0, file.getCanonicalPath().length()-4)+".svg",models.get(file.getName()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(type.equals("requirements")){
			try {
				models.put(file.getName(), new RequirementsModel(file));
				saveGraphAsPNG(file.getCanonicalPath().substring(0, file.getCanonicalPath().length()-4)+".png",models.get(file.getName()));
				saveGraphAsSVG(file.getCanonicalPath().substring(0, file.getCanonicalPath().length()-4)+".svg",models.get(file.getName()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(type.equals("correlation")){
			String name = file.getName();
			name = name.substring(0, name.length()-9);
			File[] mdl = new File[2];
			if(corr.containsKey(name)){
				mdl = corr.get(name);
				mdl[0]=file;
				corr.replace(name, mdl);
			}else{
				mdl[0]=file;
				corr.put(name, mdl);
			}
		}else if(type.equals("protocol")){
			String name = file.getName();
			name = name.substring(0, name.length()-13);
			File[] mdl = new File[2];
			if(corr.containsKey(name)){
				mdl = corr.get(name);
				mdl[1]=file;
				corr.replace(name, mdl);
			}else{
				mdl[1]=file;
				corr.put(name, mdl);
			}
		}
		
	}
	
	private String getFileType(File file){
		String type = "";
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line = null;
			while((line = reader.readLine()) != null){
				if(line.contains("xmlns:impl=\"platform:/plugin/mbse_metamodels/model/implementation.ecore\"")){
					type = "implementation";
					break;
				}else if(line.contains("xmlns:req=\"platform:/plugin/mbse_metamodels/model/requirements.ecore\"")){
					type = "requirements";
					break;
				}else if(line.contains("org.moflon.tgg.runtime:CorrespondenceModel xmi:version=\"2.0\"")){
					type = "correlation";
					break;
				}else if(line.contains("org.moflon.tgg.runtime:PrecedenceStructure xmi:version=\"2.0\"")){
					type = "protocol";
					break;
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return type;
	}
	
	public Set<String> getModelNames(){
		return models.keySet();
	}
	
	public FileInputStream loadGraphFromPNG(String model){
		try {
			File file = models.get(model).getFile();
			FileInputStream fis = new FileInputStream(file.getCanonicalPath().substring(0, file.getCanonicalPath().length()-4)+".png");
			return fis;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveGraphAsPNG(String path, Model model){
		model.writeGraphToFile(path);
	}
	
	public void saveGraphAsSVG(String path, Model model){
		model.writeGraphToSVG(path);
	}
	
	public byte[] getGraphOfModel(String model){
		return models.get(model).getDotGraph();
	}
	
	public HashMap<String, File> getModelFiles(){
		return modelFiles;
	}
	
	public Model getModel(String model){
		return models.get(model);
	}
	
	
}
