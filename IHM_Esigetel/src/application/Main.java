package application;
	
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import controlleur.Controler;
import controlleur.Joystick;
import controlleur.JoystickTest;
import controlleur.Vrep;
import controlleur.VrepVar;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	public static Vrep vrep;
	Scene scene;
	public static Joystick myStick;
	public static Service<Void> joystickService;
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Fenetre.fxml"));
			scene = new Scene(root,607,643);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMaxHeight(643);
			primaryStage.setMaxWidth(655);
			primaryStage.setMaximized(false);
			primaryStage.show();
			connection();
			startJoystick();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startJoystick() {
		joystickService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                    	new JoystickTest();
						return null;
                    }
                };
            }
        };
        joystickService.start();
	}

	private Object connection() {
		final Service<Void> calculateService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                    	VrepVar.manuel = false;
                    	VrepVar.record = false;
                    	VrepVar.replay = false;
                    	VrepVar.auto = true;
                    	VrepVar.recordValue = new Vector<Integer>();
                    	vrep = new Vrep("127.0.0.1", 19997, true, true, 5000, 5);
                    	Iterator<controlleur.Object> it = vrep.objects.iterator();
                    	while(it.hasNext())
                    	{
                    		controlleur.Object obj = it.next();
                    		VrepVar.objectName.add(obj.name);
                    	}
                    	vrep.position_init();
                    	myStick = new Joystick();
                    	float[] posit = new float[3];
                    	posit[0] = -0.3f;
                    	posit[1] = -0.3f;
                    	posit[2] = 0.5f;
                    	for(int i=0; i< Main.myStick.controllerName.size(); i++)
                		{
                			VrepVar.controllerName.add(Main.myStick.controllerName.get(i));
                		}
                    	VrepVar.nbrfois = 0;
                    	VrepVar.index = 0;
                    	while(true)
                    	{
                    		if(VrepVar.manuel || VrepVar.record || VrepVar.auto){
                            	switch(VrepVar.buttonIndex)
                                {
                                case "0":
                                	float[] pos = new float[3];
                                	pos[0] = 0.0f;
                                	pos[1] = -0.005f;
                                	pos[2] = 0.0f;
                                	vrep.deplacer(pos, 250);
                                	break;
                                case "2":
                                	float[] pos2 = new float[3];
                                	pos2[0] = 0.0f;
                                	pos2[1] = 0.005f;
                                	pos2[2] = 0.0f;
                                	vrep.deplacer(pos2, 250);
                                	break;
                                case "1":
                                	float[] pos3 = new float[3];
                                	pos3[0] = 0.005f;
                                	pos3[1] = 0.0f;
                                	pos3[2] = 0.0f;
                                	vrep.deplacer(pos3, 250);
                                	break;
                                case "3":
                                	float[] pos4 = new float[3];
                                	pos4[0] = -0.005f;
                                	pos4[1] = 0.0f;
                                	pos4[2] = 0.0f;
                                	vrep.deplacer(pos4, 250);
                                	break;
                                case "5":
                                	float[] pos5 = new float[3];
                                	pos5[0] = 0.0f;
                                	pos5[1] = 0.0f;
                                	pos5[2] = -0.005f;
                                	vrep.deplacer(pos5, 250);
                                	break;
                                case "7":
                                	float[] pos7 = new float[3];
                                	pos7[0] = 0.0f;
                                	pos7[1] = 0.0f;
                                	pos7[2] = 0.005f;
                                	vrep.deplacer(pos7, 250);
                                	break;
                                case "4":
                                	vrep.saisir(250);
                                	break;
                                case "6":
                                	vrep.relacher(250);
                                	break;
                                }
                            }
                    		if(VrepVar.replay){//cas du replay
                            	switch(VrepVar.recordValue.get(VrepVar.index).intValue())
                                {
                                case 0:
                                	float[] pos = new float[3];
                                	pos[0] = 0.0f;
                                	pos[1] = -0.005f;
                                	pos[2] = 0.0f;
                                	vrep.deplacer(pos, 250);
                                	break;
                                case 2:
                                	float[] pos2 = new float[3];
                                	pos2[0] = 0.0f;
                                	pos2[1] = 0.005f;
                                	pos2[2] = 0.0f;
                                	vrep.deplacer(pos2, 250);
                                	break;
                                case 1:
                                	float[] pos3 = new float[3];
                                	pos3[0] = 0.005f;
                                	pos3[1] = 0.0f;
                                	pos3[2] = 0.0f;
                                	vrep.deplacer(pos3, 250);
                                	break;
                                case 3:
                                	float[] pos4 = new float[3];
                                	pos4[0] = -0.005f;
                                	pos4[1] = 0.0f;
                                	pos4[2] = 0.0f;
                                	vrep.deplacer(pos4, 250);
                                	break;
                                case 5:
                                	float[] pos5 = new float[3];
                                	pos5[0] = 0.0f;
                                	pos5[1] = 0.0f;
                                	pos5[2] = -0.005f;
                                	vrep.deplacer(pos5, 250);
                                	break;
                                case 7:
                                	float[] pos7 = new float[3];
                                	pos7[0] = 0.0f;
                                	pos7[1] = 0.0f;
                                	pos7[2] = 0.005f;
                                	vrep.deplacer(pos7, 250);
                                	break;
                                case 4:
                                	vrep.saisir(250);
                                	break;
                                case 6:
                                	vrep.relacher(250);
                                	break;
                                }
                    			VrepVar.index++;
                    			if(VrepVar.index >= VrepVar.recordValue.size())
                    			{
                    				VrepVar.index = 0;
                    				VrepVar.buttonIndex = "-1";
                    			}
                    			//pause de 25ms
                    			try{
            						Thread.sleep(25);
            					}catch(InterruptedException ex)
            					{
            						Thread.currentThread().interrupt();
            					}
                            }
                    	}
                    }
                };
            }
        };
        calculateService.start();
        return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
