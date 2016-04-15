package controlleur;

import java.util.ArrayList;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Joystick {
	public ArrayList<Controller> foundControllers;
	public ArrayList<String> controllerName;
	public boolean hasController;
	
	public Joystick()
	{
		foundControllers = new ArrayList<>();
		controllerName = new ArrayList<String>();
		hasController = false;
		searchForControllers();
        if(!foundControllers.isEmpty())
            hasController = true;
        else
        	controllerName.add("No controller found!");
	}

	private void searchForControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];
            
            if (
                    controller.getType() == Controller.Type.STICK || 
                    controller.getType() == Controller.Type.GAMEPAD || 
                    controller.getType() == Controller.Type.WHEEL ||
                    controller.getType() == Controller.Type.FINGERSTICK
               )
            {
                foundControllers.add(controller);
                
                controllerName.add(controller.getName() + " - " + controller.getType().toString() + " type");
            }
        }
    }
}
