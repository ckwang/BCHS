package util;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import util.*;
import util.Action;

public class HandHistory {

	public static List<List<String>> player1 = new ArrayList<List<String>>();
	public static List<List<String>> player2 = new ArrayList<List<String>>();;
	public static List<List<String>> player3 = new ArrayList<List<String>>();;
	public static List<List<String>> all = new ArrayList<List<String>>();;
	public static String[] names = null;

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

	public static void parseHand(BufferedReader br) throws IOException {
		String[] dealer = br.readLine().split(" ");
		String[] sb = br.readLine().split(" ");
		String[] bb = br.readLine().split(" ");
		assert dealer.length == 5;
		if (names == null) {
			names = new String[3];
			names[0] = dealer[3];
			names[1] = sb[3];
			names[2] = bb[3];
		}

		List<String> p1 = new ArrayList<String>();
		List<String> p2 = new ArrayList<String>();
		List<String> p3 = new ArrayList<String>();
		List<String> hand = new ArrayList<String>();

		String[] tokens;
		int index;
		String line, action, name;
		while (!(line = br.readLine()).equals("")) {
			tokens = line.split(" ");
			action = null;
			name = tokens[0];
			if (tokens[0].equals("Dealt")) {
				action = tokens[3] + " " + tokens[4];
				name = tokens[2];
			}
			else if (tokens[1].equals("raises")) {
				action = tokens[1] + " " + tokens[3];
			}
			else if (tokens[1].equals("folds")) {
				action = tokens[1];
			}
			else if (tokens[1].equals("bets")) {
				action = tokens[1] + " " + tokens[2];
			}
			else if (tokens[1].equals("calls")) {
				action = tokens[1];
			}
			else if (tokens[1].equals("checks")) {
				action = tokens[1];
			}
			if (action != null) {
				index = nameToInt(name);
				switch (index) {
					case 1:
						p1.add(action);
						break;
					case 2:
						p2.add(action);
						break;
					case 3:
						p3.add(action);
						break;
				}
				hand.add(name + ":\t" + action);
			}
		}
		player1.add(p1);
		player2.add(p2);
		player3.add(p3);
		all.add(hand);
	}

	public static int nameToInt(String name) {
		if (name.equals(names[0]))
			return 1;
		else if (name.equals(names[1]))
			return 2;
		else
			return 3;
	}
	
	public static void main(String[] args) {

		try {
//			List<String> lines = new ArrayList<String>();
			FileInputStream fstream = new FileInputStream("../test.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
//				lines.add(strLine);
				if (strLine.startsWith("Hand #")) {
					assert strLine.endsWith(Integer.toString(player1.size() + 1));
					parseHand(br);
				}
				else {
					continue;
				}
			}
			
//			List<List<String>> hands = handSeparator(lines);
//			for (String line : hands.get(0)) {
//				System.out.println(line);
//			}
			for (int i = 0; i < all.size(); ++i) {
				System.out.println("\n" + (i + 1));
				for (int j = 0; j < player1.get(i).size(); ++j) {
					System.out.println(player1.get(i).get(j));
				}
			}

			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
