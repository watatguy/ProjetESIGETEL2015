package controlleur;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VrepVar {
	public static boolean play = false;
	public static boolean pause = false;
	public static boolean stop = false;
	public static boolean selectedObjChanged = false;
	public static ObservableList<String> objectName = FXCollections.observableArrayList();
	public static ObservableList<String> controllerName = FXCollections.observableArrayList();
	public static boolean manuel;
	public static boolean record;
	public static boolean replay;
	public static boolean auto;
	public static boolean auto_end;
	public static String buttonIndex;
	public static int nbrfois;
	public static int index;
	public static Vector<Integer> recordValue;
	
}
