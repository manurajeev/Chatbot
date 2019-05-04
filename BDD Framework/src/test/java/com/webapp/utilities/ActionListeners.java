package com.webapp.utilities;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;
/**
 *  <code>ActionListeners</code> is a Listener class, behaviour of all actions are defined in 
 * this class.
 * @author Prabhuling Kalshetti
 *  */

public class ActionListeners extends JPanel {


	private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	private static final String ADD_BUTTON_LABEL = "Add==>>";
	private static final String ADD_ALL_Button = "Add All";

	private static final String REMOVE_BUTTON_LABEL = "<==Remove";
	private static final String RESET_BUTTON = "Reset";
	private static final String PLAY_BUTTON = "RUN";
	
	private static final String DEFAULT_SOURCE_CHOICE_LABEL = "#    Test Cases From Suite   #";

	private static final String DEFAULT_DEST_CHOICE_LABEL = "Execute Test Cases";

	private JLabel sourceLabel;

	private JList sourceList;

	private SortedListModel sourceListModel;

	private JList destList;

	private SortedListModel destListModel;

	private JLabel destLabel;

	private JButton addButton, resetButton, addAllButton;
	private List<String> selectedItems   = new ArrayList<String>();
	private static String [] initialVal ;
	private static JFrame frameObject;
	private JButton removeButton, saveDetails, ExecuteTCs;
		public ActionListeners() {
		initScreen();
	}

	public String getSourceChoicesTitle() {
		return sourceLabel.getText();
	}

	public void setSourceChoicesTitle(String newValue) {
		sourceLabel.setText(newValue);
	}

	public String getDestinationChoicesTitle() {
		return destLabel.getText();
	}

	public void setDestinationChoicesTitle(String newValue) {
		destLabel.setText(newValue);
	}

	public void clearSourceListModel() {
		sourceListModel.clear();
	}

	public void clearDestinationListModel() {
		destListModel.clear();
	}

	public void addSourceElements(ListModel newValue) {
		fillListModel(sourceListModel, newValue);
	}

	public void setSourceElements(ListModel newValue) {
		clearSourceListModel();
		addSourceElements(newValue);
	}

	public void addDestinationElements(ListModel newValue) {
		fillListModel(destListModel, newValue);
	}

	private void fillListModel(SortedListModel model, ListModel newValues) {
		int size = newValues.getSize();
		for (int i = 0; i < size; i++) {
			model.add(newValues.getElementAt(i));
		}
	}

	public void addSourceElements(Object newValue[]) {
		fillListModel(sourceListModel, newValue);
	}

	public void setSourceElements(Object newValue[]) {
		clearSourceListModel();
		addSourceElements(newValue);
	}

	public void addDestinationElements(Object newValue[]) {
		fillListModel(destListModel, newValue);
	}

	private void fillListModel(SortedListModel model, Object newValues[]) {
		model.addAll(newValues);
	}

	public Iterator sourceIterator() {
		return sourceListModel.iterator();
	}

	public Iterator destinationIterator() {
		return destListModel.iterator();
	}

	public void setSourceCellRenderer(ListCellRenderer newValue) {
		sourceList.setCellRenderer(newValue);
	}

	public ListCellRenderer getSourceCellRenderer() {
		return sourceList.getCellRenderer();
	}

	public void setDestinationCellRenderer(ListCellRenderer newValue) {
		destList.setCellRenderer(newValue);
	}

	public ListCellRenderer getDestinationCellRenderer() {
		return destList.getCellRenderer();
	}

	public void setVisibleRowCount(int newValue) {
		sourceList.setVisibleRowCount(newValue);
		destList.setVisibleRowCount(newValue);
	}

	public int getVisibleRowCount() {
		return sourceList.getVisibleRowCount();
	}

	public void setSelectionBackground(Color newValue) {
		sourceList.setSelectionBackground(newValue);
		destList.setSelectionBackground(newValue);
	}

	public Color getSelectionBackground() {
		return sourceList.getSelectionBackground();
	}

	public void setSelectionForeground(Color newValue) {
		sourceList.setSelectionForeground(newValue);
		destList.setSelectionForeground(newValue);
	}

	public Color getSelectionForeground() {
		return sourceList.getSelectionForeground();
	}

	private void clearSourceSelected() {
		Object selected[] = sourceList.getSelectedValues();
		for (int i = selected.length - 1; i >= 0; --i) {
			sourceListModel.removeElement(selected[i]);
		}
		sourceList.getSelectionModel().clearSelection();
	}

	
	private void clearDestinationSelected() {
		Object selected[] = destList.getSelectedValues();
		for (int i = selected.length - 1; i >= 0; --i) {
			destListModel.removeElement(selected[i]);
		}
		destList.getSelectionModel().clearSelection();
	}
	/**
	 * 
	 * <code>initScreen</code> method will load the selection list box with all the test cases from feature/Excel file.
	 *  @author Prabhuling Kalshetti
	 *  */
	private void initScreen() {
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		sourceLabel = new JLabel(DEFAULT_SOURCE_CHOICE_LABEL);
		sourceListModel = new SortedListModel();
		sourceList = new JList(sourceListModel);
		add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				EMPTY_INSETS, 0, 0));
		add(new JScrollPane(sourceList), new GridBagConstraints(0, 1, 1, 5, .5,
				1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				EMPTY_INSETS, 0, 0));
		addButton = new JButton(ADD_BUTTON_LABEL);
		add(addButton, new GridBagConstraints(1, 2, 1, 01, 0, .25,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
        addButton.addActionListener(new AddListener());
		
		
		 addAllButton = new JButton(ADD_ALL_Button);
		 add(addAllButton, new GridBagConstraints(1, 3, 1, 01, 0, .25,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
							0, 6, 0, 6), 0, 0));
		 addAllButton.addActionListener(new AddAllListener());
		
		resetButton = new JButton(RESET_BUTTON);

		add(resetButton, new GridBagConstraints(1, 4, 1, 01, 0, .25,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 5, 0, 5), 0, 0));
	
		resetButton.addActionListener(new ResetListener());
		
		
		removeButton = new JButton(REMOVE_BUTTON_LABEL);

		add(removeButton, new GridBagConstraints(1, 5, 1, 01, 0, .25,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 5, 0, 5), 0, 0));
		removeButton.addActionListener(new RemoveListener());

				
		destLabel = new JLabel(DEFAULT_DEST_CHOICE_LABEL);
		destListModel = new SortedListModel();
		destList = new JList(destListModel);
		
		
		add(destLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		
		
		add(new JScrollPane(destList), new GridBagConstraints(2, 1, 1, 5, .5,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				EMPTY_INSETS, 0, 0));
		
	}
	
	/**
	 * <code>loadIntialValues</code> method loads the initialValues in the UI form, and clear the selected items if any.
	 * @author Prabhuling Kalshetti
	 */	
public void loadIntialValues(){
	addSourceElements(initialVal);
	selectedItems.clear();
}

/**
 * LoadListValues method loads all test cases from feature files into Selection Box list for selection
 * @author Prabhuling Kalshetti
 * @param String List[], JFrame
 */	
 public void LoadListValues(String List[], JFrame f) {
		initialVal = List;
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addSourceElements(List);
		JPanel panel= new JPanel();
		JLabel label=	new JLabel();
		label.setBounds(20, 20, 20, 20);
		panel.add(this);
		panel.setBounds(50,250,600,400);
		JButton execButton	= new JButton("Execute Test Cases");
		execButton.setBounds(215,450,150,30);
		execButton.addActionListener(new ExecutionerListener());
		f.add(execButton);
		f.add(panel);
		f.setSize(700, 600);
		f.setVisible(true);
	}
	
	public class AddAllListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object selected[] = sourceList.getSelectedValues();
			String selectedStrValue ="";

			for(int i=0; i<initialVal.length;i++){
				selectedStrValue = String.valueOf(initialVal[i]).replaceAll("\\[|\\]", "");
				selectedItems.add(selectedStrValue.trim());
				
			}
			if(initialVal.length!=0){
					addDestinationElements(initialVal);
					clearAllSelectedSource();
				}
			
	
		}
	}
	
	public class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object selected[] = sourceList.getSelectedValues();
					
			//System.out.println("Selected Values :"+selected+"--:--"+selected.length);
			String selectedStrValue ="";
		
			if(selected.length!=0)
				for(int i=0; i<selected.length;i++){
					selectedStrValue = String.valueOf(selected[i]).replaceAll("\\[|\\]", "");
					selectedItems.add(selectedStrValue.trim());
					System.out.println(selectedStrValue.trim());
					//System.out.println(selectedItems.size());
					addDestinationElements(selected);
					clearSourceSelected();
				}
		}
	}
	
	private void clearAllSelectedSource() {
		Object selected[] = sourceList.getSelectedValues();
		for (int i = initialVal.length - 1; i >= 0; --i) {
			sourceList.setSelectedIndex(i);
			sourceListModel.removeElement(initialVal[i]);
			
		}
		sourceList.getSelectionModel().clearSelection();
		
		
	}
	/**
	 * ExecutionerListener initiate process for creating new feature file and trigger the test.xml file
	 * @author Prabhuling Kalshetti
	 * 
	 */	
	
	public class ExecutionerListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Scanner sc = null;
			List<String> testCasesList = new ArrayList<>();
			File updatedFileDir = new File("UpdatedFiles");
			if(!updatedFileDir.exists()){
				updatedFileDir.mkdir();
			}
			File updatedTCsFile = new File(updatedFileDir+"/updatedTCsFile.txt");
			PrintWriter printWriter = null;
			System.out.println(selectedItems.size());
			try {
				printWriter = new PrintWriter (updatedTCsFile);
			for(String testCase:selectedItems){
		    	printWriter.println (testCase);
			}
		
			FileInputStream inputStream = new FileInputStream(updatedTCsFile);
			    sc = new Scanner(inputStream, "UTF-8");
			    while (sc.hasNextLine()) {
			        String line = sc.nextLine();
			        testCasesList.add(line.trim());
			    }
			    printWriter.close (); 
				sc.close();
				
				new FeatureFileUpdate().newFeatureFile("./src/test/resources/MasterFeature");
		    
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally{
				printWriter.close (); 
				sc.close();
			}

		//Execute Test cases 
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		File testNGFile = new File("testng.xml");
		String testngFilePath=testNGFile.getAbsolutePath();//.replace(".xml", "");
		//System.out.println(testngFilePath);
		suites.add(testngFilePath);
		testng.setTestSuites(suites);
		testng.run();
		}
	}
	public class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearDestinationListModel();
			clearSourceSelected();
			loadIntialValues();
			
		}
	}
	
	
	public class SaveTestCaseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			Scanner sc = null;
		//	Set<String> testCasesSet = new TreeSet<String>(Comparator.comparing(Integer::valueOf));
			List<String> testCasesList = new ArrayList<>();
			File updatedFileDir = new File("UpdatedFiles");
			if(!updatedFileDir.exists()){
				updatedFileDir.mkdir();
			}
			System.out.println();
			File updatedTCsFile = new File(updatedFileDir+"/updatedTCsFile.txt");
			PrintWriter printWriter = null;
			System.out.println(selectedItems.size());
			try {
				printWriter = new PrintWriter (updatedTCsFile);
			for(String testCase:selectedItems){
		    	printWriter.println (testCase);
			}
		
			FileInputStream inputStream = new FileInputStream(updatedTCsFile);
		
			
			    //inputStream = new FileInputStream(path);
			    sc = new Scanner(inputStream, "UTF-8");
			    while (sc.hasNextLine()) {
			        String line = sc.nextLine();
			        // System.out.println(line);
			        testCasesList.add(line.trim());
			    }
			    printWriter.close (); 
				sc.close();
				
				new FeatureFileUpdate().newFeatureFile("./src/test/resources/MasterFeature");
		    
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally{
				printWriter.close (); 
				sc.close();
			}
			
			
		}
		
	}
	public class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object selected[] = destList.getSelectedValues();
			addSourceElements(selected);
			clearDestinationSelected();
		String selectedStrValue="";
			if(selected.length!=0){
			 selectedStrValue = String.valueOf(selected[0]).replaceAll("\\[|\\]", "");
			//System.out.println("Selected Values :"+selected+"--:--"+selected.length);
			if(selected.length!=0)
				for(int i=0; i<selected.length;i++){
					selectedStrValue = String.valueOf(selected[i]).replaceAll("\\[|\\]", "");
					for(int j=0; j<selectedItems.size(); j++){
						if(selectedItems.get(j).equals(selectedStrValue)){
							selectedItems.remove(j);
							System.out.println(selectedItems.size());
							break;
						}
					}
				}
			}
		}
	}
}



class SortedListModel extends AbstractListModel {

	SortedSet model;

	public SortedListModel() {
		model = new TreeSet();
	}

	public int getSize() {
		return model.size();
	}

	public Object getElementAt(int index) {
		return model.toArray()[index];
	}

	public void add(Object element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(Object elements[]) {
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}

}


