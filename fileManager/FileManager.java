package fileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import data.Champion;
import data.ChampionData;

public class FileManager
{
	private static final String DATA_PATH = "data/cs.txt";
	private static final String SEPARATOR = "-";
	
	public static HashMap<Champion, ArrayList<Integer>> loadData() throws IOException
	{
		File file = new File(DATA_PATH);
		HashMap<Champion, ArrayList<Integer>> data = new HashMap<Champion, ArrayList<Integer>>();
		
		if(!file.exists())
		{
			file.createNewFile();
			return data;
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line; 
		while((line = reader.readLine()) != null) 
		{ 
			String[] readData = line.split(SEPARATOR);
			Champion readChampion = ChampionData.getFromTag(readData[0].trim());
			
			if(!data.containsKey(readChampion))
			{
				ArrayList<Integer> cs = new ArrayList<Integer>();
				cs.add(Integer.parseInt(readData[1].trim()));
				data.put(readChampion, cs);
			}
			else
			{
				ArrayList<Integer> cs = data.get(readChampion);
				cs.add(Integer.parseInt(readData[1].trim()));
			}
		}
		
		reader.close();
		
		return data;
	}
	
	public static void addData(Champion champion, int cs) throws IOException
	{
		File file = new File(DATA_PATH);
		
		if(!file.exists())
			file.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		
		writer.append("\n" + ChampionData.getName(champion) + " " + SEPARATOR + " " + cs);
		writer.flush();
		
		writer.close();
	}
}