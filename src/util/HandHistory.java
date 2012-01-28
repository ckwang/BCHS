package util;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class HandHistory {
	private static List<List<String>> handSeparator(List<String> lines) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		List<String> hand = null;
		for (String line : lines) {
			if (line.startsWith("Hand #")) {
				if (hand != null)	result.add(hand);
				hand = new ArrayList<String>();
			}
			
			if (hand != null)	hand.add(line);
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		try {
			List<String> lines = new ArrayList<String>();
			
			FileInputStream fstream = new FileInputStream("test.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
//				System.out.println(strLine);

			}
			
			List<List<String>> hands = handSeparator(lines);
			for (String line : hands.get(0)) {
				System.out.println(line);
			}
			
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
