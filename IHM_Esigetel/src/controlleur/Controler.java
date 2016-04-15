package controlleur;

import java.awt.Dimension;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class Controler implements Initializable {
	
	@FXML
	private Button playSim;
	
	@FXML
	private Button stopSim;
	
	@FXML
	private Button pauseSim;
	
	@FXML
	private ListView<String> listName;
	
	@FXML
	private Label handleLab;
	
	@FXML
	private Label positionLab;
	
	@FXML
	private Label orientationLab;
	
	@FXML
	private ComboBox<String> foundController;
	
	@FXML
	private TabPane manuel_auto;
	
	@FXML
	private Tab manuel;
	
	@FXML
	private Tab automatique;
	
	@FXML
	private ToggleButton startmanuel;
	
	@FXML
	private ToggleButton recordmanuel;
	
	@FXML
	private ToggleButton replaymanuel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		foundController.setItems(VrepVar.controllerName);
		foundController.getSelectionModel().selectFirst();
        listName.setItems(VrepVar.objectName);
        listName.setEditable(false);
	}
	
	public void startSim(ActionEvent event)
	{
		VrepVar.play = true;
		VrepVar.pause = false;
		VrepVar.stop = false;
		Main.vrep.startSimul();
		System.out.println("StartSimul: " + VrepVar.play );
	}

	public void stopSim(ActionEvent event)
	{
		VrepVar.play = false;
		VrepVar.pause = false;
		VrepVar.stop = true;
		Main.vrep.stopSimul();
		System.out.println("StopSimul: " + VrepVar.stop);
	}
	
	public void pauseSim(ActionEvent event)
	{
		VrepVar.play = false;
		VrepVar.pause = true;
		VrepVar.stop = false;
		Main.vrep.pauseSimul();
		System.out.println("PauseSimul: " + VrepVar.pause);
	}
	
	public void selectionChanged1(MouseEvent event)
	{
			Main.vrep.getObjectPosition(Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).handle);
			Main.vrep.getObjectOrientation(Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).handle);
			handleLab.setText("Handle: " + String.valueOf(Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).handle));
			positionLab.setText("Position: ( x: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).posX) + ", y: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).posy) + ", z: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).posz) + " )");
			orientationLab.setText("Orientation: ( a: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).alpha) + ", b: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).beta) + ", g: " + String.format("%1.2g",Main.vrep.objects.elementAt(this.listName.getSelectionModel().getSelectedIndex()).gamma) + " )");
	}
	
	public void joystictTabControl(ActionEvent event)
	{
		if(startmanuel.isSelected())
		{
			VrepVar.manuel = true;
			VrepVar.auto = false;
		}else
		{
			VrepVar.manuel = false;
		}
	}
	
	public void joystickRecord(ActionEvent event)
	{
		if(recordmanuel.isSelected())
		{
			VrepVar.record = true;
			Main.vrep.position_init();
		}
		else
		{
			VrepVar.record = false;
		}
	}
	
	public void joystickReplay(ActionEvent event)
	{
		if(replaymanuel.isSelected())
		{
			VrepVar.replay =true;
			VrepVar.index = 0;
			Main.vrep.position_init();
		}else
		{
			VrepVar.replay = false;
		}
	}
	
}
