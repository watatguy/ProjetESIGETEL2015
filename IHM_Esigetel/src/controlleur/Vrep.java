package controlleur;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import application.Main;
import coppelia.*;

public class Vrep {
	private int handle_manipSphere;
	private int handle_view;
	private int handle_pinceD;
	private int handle_pinceG;
	private IntWA resolution;
	boolean fermer;
	CharWA image;
	private String IP;
	private int port;
	private int idClient;
	private remoteApi vrep;
	public boolean error;
	public Vector<Object> objects;
	public Vector<Object> cubes;
	private boolean estCharger;
	private int handle_charge;
	//Methodes
	public Vrep(String ip, int port, boolean wait, boolean notReconnect, int timeOut, int commThreadCycle)
	{
		image = new CharWA(64*64*3);
		estCharger = false;
		handle_charge = -1;
		resolution = new IntWA(2);
		objects = new Vector<Object>();
		cubes = new Vector<Object>();
		fermer = false;
		IP = ip;
		this.port = port;
		vrep = new remoteApi();
		vrep.simxFinish(-1);//fermeture de toute ancienne connexion
		idClient = vrep.simxStart(ip, port, wait, notReconnect, timeOut, commThreadCycle);
		if(idClient == -1) {
			error = true;
			System.out.println("Erreur de connection à Vrep");
		}
		else {
			error = false;
			loadObjectData();
		}
		for(int i=0; i<objects.size();i++)
		{
			if(objects.get(i).type == 0 && objects.get(i).name.matches("Cube[0-9]"))
			{
				cubes.add(objects.get(i));
			}
		}
	}
	
	private void loadObjectData()
	{
		objects.clear();
		IntWA handles = new IntWA(100);
		IntWA intData = new IntWA(100);
		FloatWA floatData = new FloatWA(100);
		StringWA stringData = new StringWA(100);
		//recupération des nom des objets
		vrep.simxGetObjectGroupData(idClient, vrep.sim_appobj_object_type, 0, handles, intData, floatData, stringData, vrep.simx_opmode_oneshot_wait);
		for(int i=0; i< stringData.getLength(); i++)
		{
			objects.add(new Object());
			objects.lastElement().name = stringData.getArray()[i];
			objects.lastElement().handle = handles.getArray()[i];
			System.out.println(handles.getArray()[i] + " : " + stringData.getArray()[i]);
			if(objects.lastElement().name.equals("manipSphere"))
			{
				handle_manipSphere = handles.getArray()[i];
			}
			if(objects.lastElement().name.equals("View"))
			{
				handle_view = handles.getArray()[i];
			}
			if(objects.lastElement().name.equals("Pince_D"))
			{
				handle_pinceD = handles.getArray()[i];
			}
			if(objects.lastElement().name.equals("Pince_G"))
			{
				handle_pinceG = handles.getArray()[i];
			}
		}
		//recuperation des types
		vrep.simxGetObjectGroupData(idClient, vrep.sim_appobj_object_type, 1, handles, intData, floatData, stringData, vrep.simx_opmode_streaming + 500);
		Iterator<Object> it = objects.iterator();
		while(it.hasNext())
		{
			Object obj = it.next();
			for(int i=0; i< intData.getLength(); i++)
			{
				if(obj.handle == handles.getArray()[i])
				{
					obj.type = intData.getArray()[i];
					break;
				}
			}
		}
		//recuperation de la position et de l'orientation absolu des objet
		Vector<FloatWA> floatData2 = new Vector<FloatWA>();
		Vector<FloatWA> floatData3 = new Vector<FloatWA>();
		vrep.simxGetObjectPosition(idClient, 0, -1, floatData, vrep.simx_opmode_streaming + 500);
		Iterator<Object> it2 = objects.iterator();
		while(it2.hasNext())
		{
			Object obj1 = it2.next();
			floatData2.add(new FloatWA(3));
			vrep.simxGetObjectPosition(idClient, obj1.handle, -1, floatData2.lastElement(), vrep.simx_opmode_oneshot_wait);
			obj1.posX = floatData2.lastElement().getArray()[0];
			obj1.posy = floatData2.lastElement().getArray()[1];
			obj1.posz = floatData2.lastElement().getArray()[2];
			floatData3.add(new FloatWA(3));
			vrep.simxGetObjectOrientation(idClient, obj1.handle, -1, floatData3.lastElement(), vrep.simx_opmode_oneshot_wait);
			obj1.alpha = floatData3.lastElement().getArray()[0];
			obj1.beta = floatData3.lastElement().getArray()[1];
			obj1.gamma = floatData3.lastElement().getArray()[2];
		}
		System.out.println("nombre d'objet: " + objects.size());
		Iterator<Object> it5 = objects.iterator();
		while(it5.hasNext())
		{
			Object obj = it5.next();
			System.out.println(obj.name + ": handle: " + obj.handle  + " | type: " + obj.type +  " | orientation: (" +obj.alpha + ", " +obj.beta + ", " +obj.gamma + ") | position: (" +obj.posX + ", " + obj.posy + ", " + obj.posz + ") ");
		}
	}
	
	public void startSimul()
	{
		vrep.simxStartSimulation(idClient, vrep.simx_opmode_oneshot);
	}

	public void stopSimul()
	{
		vrep.simxStopSimulation(idClient, vrep.simx_opmode_oneshot);
	}
	
	public void pauseSimul()
	{
		vrep.simxPauseSimulation(idClient, vrep.simx_opmode_oneshot);
	}
	
	public void EndConnection()
	{
		vrep.simxFinish(idClient);
	}
	
	public int deplacer(float[] position, int time_ms)
	{
		FloatWA pos = new FloatWA(3);
		pos.getArray()[0] = position[0];
		pos.getArray()[1] = position[1];
		pos.getArray()[2] = position[2];
		vrep.simxSetObjectPosition(idClient, handle_manipSphere, handle_manipSphere, pos, vrep.simx_opmode_oneshot);
		/*if(estCharger)
		{
			pos.getArray()[0] *= -1;
			pos.getArray()[1] *= 1;
			pos.getArray()[2] *= -1;	
			vrep.simxSetObjectPosition(idClient, handle_charge, handle_charge, pos, vrep.simx_opmode_oneshot);
		}*/
		return 0;
	}
	
	public int deplacer_global(float[] position, int time_ms)
	{
		final Service<Void> calculateService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                    	if(VrepVar.auto && !VrepVar.manuel && !VrepVar.replay)
                    	{
                    		while(idClient == -1);
                    		boolean x_ok = false, y_ok = false, z_ok = false;
                    		do{
                    			//pause de 25ms
                    			try{
            						Thread.sleep(time_ms);
            					}catch(InterruptedException ex)
            					{
            						Thread.currentThread().interrupt();
            					}
                    		FloatWA pos = new FloatWA(3);
                    		vrep.simxGetObjectPosition(idClient, handle_manipSphere, -1, pos, vrep.simx_opmode_oneshot_wait);
                    		if(pos.getArray()[0] <= (position[0] - 0.05f))
                    		{
                    			VrepVar.buttonIndex = "3";
                    			continue;
                    		}else if(pos.getArray()[0] >= (position[0] + 0.05f))
                    		{
                    			VrepVar.buttonIndex = "1";
                    			continue;
                    		}else
                    		{
                    			x_ok = true;
                    		}
                    		if(pos.getArray()[1] <= (position[1] - 0.05f))
                    		{
                    			VrepVar.buttonIndex = "2";
                    			continue;
                    		}else if(pos.getArray()[1] >= (position[1] + 0.05f))
                    		{
                    			VrepVar.buttonIndex = "0";
                    			continue;
                    		}else
                    		{
                    			y_ok = true;
                    		}
                    		if(pos.getArray()[2] <= (position[2] - 0.05f))
                    		{
                    			VrepVar.buttonIndex = "7";
                    			continue;
                    		}else if(pos.getArray()[2] >= (position[2] + 0.05f))
                    		{
                    			VrepVar.buttonIndex = "5";
                    			continue;
                    		}else
                    		{
                    			x_ok = true;
                    		}
                    		}while(!x_ok || !y_ok || !z_ok);
                    		VrepVar.buttonIndex = "-1";
                    		
                    	}
						return null;
                            }
                };
            }
        };
        calculateService.start();
		return 0;
	}
	
	public int relacher(int time_ms)
	{
		if(estCharger)
		{
			estCharger = false;
			vrep.simxSetObjectParent(idClient, handle_charge, -1, true, vrep.simx_opmode_oneshot_wait);
		}
		return 0;
	}
	
	public int saisir(int time_ms)
	{
		if(!estCharger){
		//saisir de l'object détecté
		for(int j = 0; j<cubes.size();j++)
		{
			FloatWA posit = new FloatWA(3);
			FloatWA posit2 = new FloatWA(3);
			vrep.simxGetObjectPosition(idClient, cubes.get(j).handle, -1, posit, vrep.simx_opmode_oneshot_wait);
			vrep.simxGetObjectPosition(idClient, handle_manipSphere, -1, posit2, vrep.simx_opmode_oneshot_wait);
			if(Math.abs(posit.getArray()[0] - posit2.getArray()[0]) < 0.05f && Math.abs(posit.getArray()[1] - posit2.getArray()[1]) < 0.05f && Math.abs(posit.getArray()[2] - posit2.getArray()[2]) < 0.05f)
			{
				vrep.simxSetObjectParent(idClient, cubes.get(j).handle, 32, true, vrep.simx_opmode_oneshot_wait);
				handle_charge = cubes.get(j).handle;
				estCharger = true;
				break;
			}
		}
		}
		return 0;
	}
	
	public int position_init()
	{
		FloatWA pos = new FloatWA(3);
		pos.getArray()[0] =(float) (-700.32 * 0.001);
		pos.getArray()[1] =(float) (-0.015 * 0.001) ;
		pos.getArray()[2] =(float) 0.680;
		vrep.simxSetObjectPosition(idClient, handle_manipSphere, -1, pos, vrep.simx_opmode_oneshot);
		this.relacher(62);
		return 0;
	}
	
	public void arretUrgence()
	{
		
	}
	
	public void getObjectPosition(int handle)
	{
		FloatWA floatData2 = new FloatWA(3);
		Iterator<Object> it2 = objects.iterator();
		while(it2.hasNext())
		{
			Object obj1 = it2.next();
			if(handle == obj1.handle){
				vrep.simxGetObjectPosition(idClient, handle, -1, floatData2, vrep.simx_opmode_oneshot_wait);
				obj1.posX = floatData2.getArray()[0];
				obj1.posy = floatData2.getArray()[1];
				obj1.posz = floatData2.getArray()[2];
			}
		}
	}
	public void getObjectOrientation(int handle)
	{
		FloatWA floatData2 = new FloatWA(3);
		Iterator<Object> it2 = objects.iterator();
		while(it2.hasNext())
		{
			Object obj1 = it2.next();
			if(handle == obj1.handle){
				vrep.simxGetObjectOrientation(idClient, handle, -1, floatData2, vrep.simx_opmode_oneshot_wait);
				obj1.alpha = floatData2.getArray()[0];
				obj1.beta = floatData2.getArray()[1];
				obj1.gamma = floatData2.getArray()[2];
			}
		}
	}
	
	public void imageView()
	{
		vrep.simxGetVisionSensorImage(idClient, handle_view, resolution, image, 0, vrep.simx_opmode_streaming+500);
		int res = vrep.simxGetVisionSensorImage(idClient, handle_view, resolution, image, 0, vrep.simx_opmode_oneshot_wait);
		if(res == vrep.simx_return_ok)	
			System.out.println(handle_view + " " +Arrays.toString(image.getArray()));
		else
		{
			StringWA errorstr = new StringWA(30);
			vrep.simxGetLastErrors(idClient, errorstr, vrep.simx_opmode_oneshot_wait);
			System.out.println("Error: " + Arrays.toString(errorstr.getArray()) + " " + res);
		}
	}
}
