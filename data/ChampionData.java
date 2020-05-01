package data;

import java.awt.Color;

public class ChampionData
{
	public static Color getColor(Champion champion)
	{
		switch(champion)
		{
			case KAI_SA: return new Color(75, 0, 130);
			case LUCIAN: return new Color(30, 30, 30);
			case MISS_FORTUNE: return new Color(255, 60, 30);
			case XAYAH: return new Color(190, 30, 150);
			default: return new Color(127, 127, 127);
		}
	}
	
	public static Champion getFromTag(String tag)
	{
		switch(tag)
		{
			case "Kai'Sa": return Champion.KAI_SA;
			case "Lucian": return Champion.LUCIAN;
			case "Miss Fortune": return Champion.MISS_FORTUNE;
			case "Xayah": return Champion.XAYAH;
			default: return null;
		}
	}
	
	public static String getName(Champion champion)
	{
		switch(champion)
		{
			case KAI_SA: return "Kai'Sa";
			case LUCIAN: return "Lucian";
			case MISS_FORTUNE: return "Miss Fortune";
			case XAYAH: return "Xayah";
			default: return "NO_MATCH";
		}
	}
}