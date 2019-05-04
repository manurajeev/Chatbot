package com.webapp.utilities;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
/**
 *  <code>ComponentsLoader</code> is a loader class, loads user and execution details i.e. text field, dropdown and button to the Form.
 * this class.
 * @author Prabhuling Kalshetti
 *  */
public abstract class ComponentsLoader {

	JButton getCase,getFailedCase;
	JButton loadProjectInfo;
	JFrame frame;
	List<String> testCasesList = new ArrayList<String>();
	final ActionListeners obj = new ActionListeners();
	private static String releaseNames="";
	private static String selectedEnvValue ="";
	private static String selectedRegionValue ="";
	Vector v = new Vector();


	public ComponentsLoader() {
		getCase = new JButton("Load Test Cases");
		getFailedCase = new JButton("Load Failed Cases");
	}

	public void buildForm () {
		JLabel l1, l2, releaseName, projectName, environment,region,features,testCycle;
		final JTextField userText = new JTextField(20);


		// Project label and dropdown
		projectName = new JLabel("Project : ");
		projectName.setBounds(300, 50, 100, 30);

		String sectionList[] = { "Proj Name"};
		final JComboBox<String> section = new JComboBox<String>(sectionList);
		section.setBounds(375, 55, 200, 20);

		// Environment label and dropdown
		environment = new JLabel("Environment : ");
		environment.setBounds(50, 98, 100, 30);

		//
		/*region = new JLabel("Region : ");
		region.setBounds(50, 98, 100, 30);*/

		features = new JLabel("Features :");
		features.setBounds(50,150,100,30);


		File f = new File("Config/Sys.properties");
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream("Config/Sys.properties");

			properties.load(fis);
		} catch (Exception e1) {
			e1.printStackTrace();
		}


				String environmentList[] = { "QA","UAT","Dev","SandBox","Production" };
				final JComboBox<String> environments = new JComboBox<String>(environmentList);
				environments.setBounds(150, 105, 150, 20);


/*				String regionList[] = properties.getProperty("RegionList").split(",");
				final JComboBox<String> regions = new JComboBox<String>(regionList);*/
				//regions.setBounds(120, 105, 150, 20);

				FeatureFileUpdate featureFileUpdate = new FeatureFileUpdate();
				List<File> listOfFeatureFiles = featureFileUpdate.listOfFeatureFiles(new File("./src/test/resources/MasterFeature"));
				List<String> allFeatureNames = new ArrayList<String>();
				//allFeatureNames.add("All");
				for(File files : listOfFeatureFiles) {
					allFeatureNames.add(files.getName().replaceAll(".feature", ""));
				}

				v.add("All");
				for(String s : allFeatureNames) {
					v.add(new JCheckBox(s,false));
				}
				JComboCheckBox featureCheckBoxes = new JComboCheckBox(v);
				featureCheckBoxes.setBounds(120, 155, 250, 20);

		getCase.setBounds(150, 200, 200, 30);
		getFailedCase.setBounds(400, 200, 150, 30);
		// Email id lable and text
		/*l2 = new JLabel("Email ID : ");
		l2.setBounds(20, 100, 100, 30);

		final JTextField emailIdText = new JTextField(20);
		emailIdText.setBounds(100, 105, 150, 20);*/

		// Release name and Text
		releaseName = new JLabel("Release : ");
		releaseName.setBounds(50, 50, 100, 30);

		final JTextField releaseNameText = new JTextField(20);
		releaseNameText.setBounds(125, 55, 150, 20);

		//frame.add(l1);
		//frame.add(l2);
		frame.add(projectName);
		frame.add(releaseName);
		frame.add(releaseNameText);
		frame.add(environments);
		frame.add(environment);
		//frame.add(regions);
		//frame.add(region);
		frame.add(features);
		frame.add(featureCheckBoxes);
		//frame.add(userText);
		//frame.add(emailIdText);
		frame.add(getCase);
		frame.add(getFailedCase);
		frame.add(section);

/**
 * AddActionListener loads user and build details entered in the UI and udpate the config files
 * @author Prabhuling Kalshetti
 */
		getCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent getCase) {

				Object[] projectName = section.getSelectedObjects();
				try {

				File f = new File("Config/Sys.properties");
				FileOutputStream fos = new FileOutputStream("Config/Sys.properties",true);
				Properties properties = new Properties();

				String projectVal = String.valueOf(section.getSelectedItem());
				System.out.println("projectVal : "+ projectVal);
				String userName = userText.getText();
				System.out.println("User : "+ userName);
				String releaseName = releaseNameText.getText();
				releaseNames =releaseName;
				System.out.println("releaseName : "+ releaseName);
				properties.setProperty("ReleaseName",releaseNames );
				String selectedEnv = String.valueOf(environments.getSelectedItem());
				selectedEnvValue =selectedEnv;
				System.out.println("Selected Env : "+ selectedEnv);
				properties.setProperty("Execution_Environment",selectedEnvValue );

				//String selectedRegion = String.valueOf(regions.getSelectedItem());
				//selectedRegionValue =selectedRegion;
				//System.out.println("Selected Region : "+ selectedRegion);
				//properties.setProperty("Execution_Region",selectedRegionValue );
				/*//String emailID = emailIdText.getText();
				System.out.println("emailID : "+ emailID);
				properties.setProperty("User",userName );
				properties.setProperty("UserEmailID", emailID);*/

				

				String selectedFeatures = "";
				for(int i=0;i<v.size();i++) {
					if(v.get(i) instanceof JCheckBox) {
						JCheckBox j = (JCheckBox)v.get(i);
						if(j.isSelected()) {
							selectedFeatures = j.getText()+","+selectedFeatures;
						}
					}
				}
				properties.setProperty("SelectedFeatures", selectedFeatures);
				properties.store(fos, null);
				fos.close();
				FileInputStream fis = new FileInputStream("Config/Sys.properties");
				properties.load(fis);
				System.out.println(properties.get("UserName"));
				System.out.println(properties.get("User"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				testCasesList = new FeatureFileUpdate().testCaseList();
				String [] testCases = new String [testCasesList.size()];
				testCases = testCasesList.toArray(testCases);
				obj.LoadListValues(testCases, frame);
				frame.revalidate();
				frame.repaint();
			}
		});

		getFailedCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent getCase) {

				Object[] projectName = section.getSelectedObjects();
				try {

				File f = new File("Config/Sys.properties");
				FileOutputStream fos = new FileOutputStream("Config/Sys.properties",true);
				Properties properties = new Properties();

				String projectVal = String.valueOf(section.getSelectedItem());
				System.out.println("projectVal : "+ projectVal);
				String userName = userText.getText();
				System.out.println("User : "+ userName);
				String releaseName = releaseNameText.getText();
				releaseNames =releaseName;
				System.out.println("releaseName : "+ releaseName);
				properties.setProperty("ReleaseName",releaseNames );
				String selectedEnv = String.valueOf(environments.getSelectedItem());
				selectedEnvValue =selectedEnv;
				System.out.println("Selected Env : "+ selectedEnv);
				properties.setProperty("Execution_Environment",selectedEnvValue );
				properties.setProperty("SelectedFeatures", "");

				properties.store(fos, null);
				fos.close();
				new FeatureFileUpdate().testCaseList();
				Scanner sc = null;
				FileInputStream inputStream = new FileInputStream("Config/FailedTC.txt");


			    //inputStream = new FileInputStream(path);
			    sc = new Scanner(inputStream, "UTF-8");
			    while (sc.hasNextLine()) {
			        String line = sc.nextLine();
			        // System.out.println(line);
			        if(testCasesList.contains(line.trim()))
			        	System.out.println("Duplicate ---- "+line.trim());
			        testCasesList.add(line.trim());
			    }
				sc.close();

				FileInputStream fis = new FileInputStream("Config/Sys.properties");
				properties.load(fis);
				System.out.println(properties.get("UserName"));
				System.out.println(properties.get("User"));

				//Failed TC File
				String ProjectPath = new File("").getAbsolutePath();
				String filePath = ProjectPath + "\\config\\FailedTC.txt";
				PrintWriter writer = new PrintWriter(filePath);
				writer.print("");
				writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				String [] testCases = new String [testCasesList.size()];
				testCases = testCasesList.toArray(testCases);
				obj.LoadListValues(testCases, frame);
				frame.revalidate();
				frame.repaint();

			}
		});
	}


	public void loadTestCases(List<String> testCaseList){

		String [] testCases = new String [testCasesList.size()];
		testCases = testCasesList.toArray(testCases);
		obj.LoadListValues(testCases, frame);
		frame.revalidate();
		frame.repaint();
	}

	public JButton getGetCase() {
		return getCase;
	}
	public void setGetCase(JButton getCase) {
		this.getCase = getCase;
	}
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public static String getReleaseName(){
		return releaseNames;
	}

	}




class JComboCheckBox extends JComboBox
{
	Vector v = new Vector();
	 public JComboCheckBox(Vector items) {
	     super(items);
	     v = items;
	     init();
	  }

	  public void init() {
	     setRenderer(new ComboBoxRenderer());
	     addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	           itemSelected();
	        }
	     });
	  }

	  private void itemSelected() {
	     if (getSelectedItem() instanceof JCheckBox) {
	        JCheckBox jcb = (JCheckBox)getSelectedItem();
	        jcb.setSelected(!jcb.isSelected());
			}
	     //Deselect all checkboxes if All option is selected
	     if(getSelectedItem() instanceof String) {
	    	 String all = (String) getSelectedItem();
	    	 System.out.println("Selected : "+all);
	       /* Object[] selections = jcb.getSelectedObjects();
				if(selections!=null) {
					for(Object lastItem : selections) {
						//System.out.println("lastItem: "+lastItem.toString());
						//JOptionPane.showMessageDialog(null, lastItem.toString());
					}
				}*/
	    	 for(int i=0;i<v.size();i++) {
					if(v.get(i) instanceof JCheckBox) {
						JCheckBox j = (JCheckBox)v.get(i);
						j.setSelected(false);
					}
				}
	     }
	  }
class ComboBoxRenderer implements ListCellRenderer {
     private JLabel label;

     public ComboBoxRenderer() {
    	 setOpaque(true);
     }

     public Component getListCellRendererComponent(JList list, Object value, int index,
                                                   boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Component) {
           Component c = (Component)value;
           if (isSelected) {
              c.setBackground(list.getSelectionBackground());
              c.setForeground(list.getSelectionForeground());
           } else {
              c.setBackground(list.getBackground());
              c.setForeground(list.getForeground());
           }

           return c;
        } else {
           if (label ==null) {
              label = new JLabel(value.toString());
           }
           else {
              label.setText(value.toString());
           }

           return label;
        }
     }
  }
}