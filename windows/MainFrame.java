package windows;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Champion;
import data.ChampionData;
import fileManager.FileManager;

public class MainFrame extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;

	private final JButton addDataButton;
	
	private final Canvas canvas;
	
	private boolean running = true;
	
	private HashMap<Champion, ArrayList<Integer>> data;
	private int horHatches = 0;
	private int verHatches = 0;
	private int minCs = 1000;
	
	private static final Color LIGHT_GRAY = new Color(150, 150, 150);

	private static final int FPS = 60;
	private static final int FRAME_DURATION =(int)(1_000_000_000f / FPS);
	private static final int SYNC_DELAY = 500_000;
	
	private static final int CANVAS_WIDTH = 1280;
	private static final int CANVAS_HEIGHT = 720;
	private static final int AXIS_OFFSET = 30;
	private static final int HATCH_LENGHT = 10;
	private static final int NUMBER_Y_OFFSET = 10;
	
	public MainFrame()
	{
		super();
		
		loadData();
		
		setTitle("CS Recorder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setResizable(false);
		
		addDataButton = new JButton("Add CS Record");
		canvas = new Canvas();
		canvas.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		JLabel credits1 = new JLabel("Created By Cristian Camillo");
		JLabel credits2 = new JLabel(" (cristian.camillo@yahoo.it)");
		
		JPanel labelPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel generalPanel = new JPanel(new BorderLayout());
		
		labelPanel.add(credits1, BorderLayout.NORTH);
		labelPanel.add(credits2, BorderLayout.SOUTH);
		
		topPanel.add(addDataButton, BorderLayout.WEST);
		topPanel.add(labelPanel, BorderLayout.EAST);
		
		centerPanel.add(topPanel, BorderLayout.NORTH);
		centerPanel.add(new JLabel(" "), BorderLayout.CENTER);
		centerPanel.add(canvas, BorderLayout.SOUTH);
		
		generalPanel.add(new JLabel(" "), BorderLayout.NORTH);
		generalPanel.add(new JLabel("     "), BorderLayout.WEST);
		generalPanel.add(centerPanel, BorderLayout.CENTER);
		generalPanel.add(new JLabel("     "), BorderLayout.EAST);
		generalPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		
		add(generalPanel);
		
		pack();
		setLocationRelativeTo(null);
		
		/*
		addDataButton = new JButton("Add CS Record");
		addDataButton.setBounds(OFFSET, OFFSET, 160, OFFSET);
		add(addDataButton);
		
		canvas = new Canvas();
		canvas.setBounds(OFFSET, OFFSET * 3, CANVAS_WIDTH, CANVAS_HEIGHT);
		add(canvas);		
		
		JLabel credits1 = new JLabel("Created By Cristian Camillo");
		credits1.setBounds(WIDTH - 191, OFFSET, 300, 20);
		add(credits1);
		
		JLabel credits2 = new JLabel("(cristian.camillo@yahoo.it)");
		credits2.setBounds(WIDTH - 187, OFFSET + 15, 300, 20);
		add(credits2);
		 */
		
		addDataButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {	openAddCsFrame(); } });
		
		addKeyListener(this);
		addDataButton.addKeyListener(this);
		canvas.addKeyListener(this);
		
		setVisible(true);
		
		handleCanvas();		
	}
	
	public void loadData()
	{
		try
		{
			data = FileManager.loadData();
			horHatches = getHorHatCount();
			verHatches = getVerHatCount();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Couldn't read the data!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	private void handleCanvas()
	{
		canvas.createBufferStrategy(2);
		BufferStrategy bs = canvas.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();

		while(running)
		{
			long start = System.nanoTime();
			
			drawOutline(g);			
			drawBackground(g);			
			drawAxis(g);			
			drawHorizontalHatchMarks(g);
			drawVerticalHatchMarks(g);
			
			drawLines(g);
			
			bs.show();							
			
			long timeLeft = FRAME_DURATION - (System.nanoTime() - start) - SYNC_DELAY;
			if(timeLeft > 0)
				try{ Thread.sleep(timeLeft / 1_000_000); } catch(Exception e) {}				
			while(FRAME_DURATION > System.nanoTime() - start);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int getHorHatCount()
	{
		int minCs = 1000;
		int maxCs = 0;

		Iterator<Entry<Champion, ArrayList<Integer>>> it = data.entrySet().iterator();
	    while(it.hasNext())
	    {
	        Map.Entry pair = (Map.Entry)it.next();
	        ArrayList<Integer> cs = (ArrayList<Integer>)pair.getValue();

	        for(int i = 0; i < cs.size(); i++)
	        {
	        	if(cs.get(i) < minCs)
	        		minCs = cs.get(i);
	        	if(cs.get(i) > maxCs)
	        		maxCs = cs.get(i);
	        }
	    }
	    
	    this.minCs = minCs;
	    
	    return maxCs - minCs + 1;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int getVerHatCount()
	{
		int max = 0;
		
		Iterator<Entry<Champion, ArrayList<Integer>>> it = data.entrySet().iterator();
	    while(it.hasNext())
	    {
	        Map.Entry pair = (Map.Entry)it.next();
	        int value = ((ArrayList<Integer>)pair.getValue()).size();

	        if(value > max)
	        	max = value;
	    }
	    
	    return max;
	}
	
	private void drawOutline(Graphics g)
	{
		g.setColor(LIGHT_GRAY);
		g.drawRect(0, 0, CANVAS_WIDTH - 1, CANVAS_HEIGHT - 1);
	}
	
	private void drawBackground(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(1, 1, CANVAS_WIDTH - 2, CANVAS_HEIGHT - 2);
	}
	
	private void drawAxis(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.drawLine(AXIS_OFFSET, AXIS_OFFSET, AXIS_OFFSET, CANVAS_HEIGHT - AXIS_OFFSET);
		g.drawLine(AXIS_OFFSET, CANVAS_HEIGHT - AXIS_OFFSET, CANVAS_WIDTH - AXIS_OFFSET, CANVAS_HEIGHT - AXIS_OFFSET);
	}
	
	private void drawHorizontalHatchMarks(Graphics g)
	{
		if(horHatches == 0) return;

		g.setColor(Color.BLACK);
		
		int x1 = AXIS_OFFSET - HATCH_LENGHT / 2;
		int x2 = x1 + HATCH_LENGHT;
		int yOffset = CANVAS_HEIGHT - AXIS_OFFSET;
		
		if(horHatches == 1)
		{
			g.drawLine(x1, yOffset, x2, yOffset);
			g.drawString(minCs + "", AXIS_OFFSET - 21, yOffset + 5);
			return;
		}
		
		int step = (CANVAS_HEIGHT - AXIS_OFFSET * 2) / (horHatches - 1);		
		
		for(int i = 0; i < horHatches; i++)
		{			
			int y1 = yOffset - step * i;
			
			g.drawLine(x1, y1, x2, y1);
			g.drawString(minCs + i + "", AXIS_OFFSET - 21, y1 + 5);
		}
	}
	
	private void drawVerticalHatchMarks(Graphics g)
	{
		if(verHatches == 0) return;
		
		g.setColor(Color.BLACK);
		
		int y1 = CANVAS_HEIGHT - AXIS_OFFSET - HATCH_LENGHT / 2;
		int y2 = y1 + HATCH_LENGHT;
		
		if(verHatches == 1)
		{
			g.drawLine(AXIS_OFFSET, y1, AXIS_OFFSET, y2);
			g.drawString("1", AXIS_OFFSET - 3, y2 + 14);
			return;
		}
		
		int step = (CANVAS_WIDTH - AXIS_OFFSET * 2) / (verHatches - 1);
		
		for(int i = 0; i < verHatches; i++)
		{
			int x1 = AXIS_OFFSET + step * i;
			g.drawLine(x1, y1, x1, y2);
			g.drawString(i + 1 + "", x1 - 3, y2 + 14);
		}
	}
	
	private void drawLines(Graphics g)
	{
		int xStep = (CANVAS_WIDTH - AXIS_OFFSET * 2) / (verHatches- 1);
		int yStep = (CANVAS_HEIGHT - AXIS_OFFSET * 2) / (horHatches - 1);
		
		Champion[] allChampions = Champion.values();
		for(int i = 0, l = allChampions.length; i < l; i++)
		{			
			ArrayList<Integer> cs = data.get(allChampions[i]);
			if(cs != null)
			{
				g.setColor(ChampionData.getColor(allChampions[i]));				
				
				int oldX = -1;
				int oldY = -1;
				
				for(int j = 0, k = cs.size(); j < k; j++)
				{
					int yPosition = CANVAS_HEIGHT - AXIS_OFFSET - (cs.get(j) - minCs) * yStep;
					int xPosition = AXIS_OFFSET + xStep * j;
					
					if(oldX != -1)
					{
						g.drawLine(oldX, oldY - 1, xPosition, yPosition - 1);
						g.drawLine(oldX, oldY, xPosition, yPosition);
					}
					
					oldX = xPosition;
					oldY = yPosition;
					
					g.fillOval(xPosition - 5, yPosition - 5, 10, 10);
				}				
			}
		}
	}
	
	private void openAddCsFrame()
	{
		setEnabled(false);
		new AddCsFrame(this);		
	}
	
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_ENTER: 
					if(getFocusOwner().equals(addDataButton))
							openAddCsFrame();
					break;
			case KeyEvent.VK_ESCAPE:
					running = false;
					dispose();
					break;
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}