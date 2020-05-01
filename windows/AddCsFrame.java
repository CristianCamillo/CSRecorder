package windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import data.Champion;
import data.ChampionData;
import fileManager.FileManager;

public class AddCsFrame extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private final JFrame father;
	
	private final JTextField csField;
	private final JComboBox<String> championBox;
	
	private final JButton addRecordButton;
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 130;
	
	public AddCsFrame(JFrame father)
	{
		super();
		
		this.father = father;
		
		setTitle("Add CS Record");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		/*
		 * 
		 * 
		JLabel csLabel = new JLabel("CS:");
		csField = new JTextField(12);
		JLabel championLabel = new JLabel("Champion:");
		String[] championNames = new String[Champion.values().length];		
		for(int i = 0, l = Champion.values().length; i < l; i++)
			championNames[i] = ChampionData.getName(Champion.values()[i]);		
		championBox = new JComboBox<String>(championNames);
		
		JPanel centerPanel = new JPanel(new SpringLayout());
		*/
		setSize(WIDTH + 16, HEIGHT + 62);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		
		JLabel csLabel = new JLabel("CS:");
		csLabel.setBounds(20, 20, 40, 25);
		add(csLabel);

		JLabel championLabel = new JLabel("Champion:");
		championLabel.setBounds(20, 50, 100, 25);
		add(championLabel);

		csField = new JTextField();
		csField.setBounds(90, 20, 28, 25);
		add(csField);
		
		String[] championNames = new String[Champion.values().length];
		
		for(int i = 0, l = Champion.values().length; i < l; i++)
			championNames[i] = ChampionData.getName(Champion.values()[i]);
		
		championBox = new JComboBox<String>(championNames);
		championBox.setBounds(90, 50, 190, 25);
		add(championBox);
		
		addRecordButton = new JButton("Add");
		addRecordButton.setBounds(121, 100, 57, 30);
		add(addRecordButton);
		
		addWindowListener((WindowListener) new WindowAdapter() { public void windowClosing(WindowEvent e) { close(); } });
		addKeyListener(this);
		csField.addKeyListener(this);
		championBox.addKeyListener(this);
		addRecordButton.addKeyListener(this);
		
		addRecordButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e)	{ addCsRecord(); } });
		
		setVisible(true);
	}
	
	private void addCsRecord()
	{
		try
		{
			int cs = Integer.parseInt(csField.getText());
			
			if(cs < 0)
				throw new NumberFormatException();
			
			FileManager.addData(Champion.values()[championBox.getSelectedIndex()], cs);
			
			JOptionPane.showMessageDialog(null, "CS Record memorized!", "Info", JOptionPane.INFORMATION_MESSAGE);
			close();
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Input a proper cs score!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Couldn't write the data!", "Error", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	private void close()
	{
		((MainFrame)father).loadData();
		father.setEnabled(true);		
		father.toFront();		
		dispose();
	}


	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER && getFocusOwner().equals(addRecordButton))
			addCsRecord();
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			close();
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}	
}