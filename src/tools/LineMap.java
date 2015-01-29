package tools;

import java.awt.*;
import java.io.*;
import java.io.InputStream;
import java.util.*;

import org.xml.sax.InputSource;

public class LineMap {
	public dot[] dots;

	public LineMap() {
		initializeBox();
	}

	public LineMap(String rawstring) {
		if (rawstring.contains("LM")) {
			initializeFromSource(rawstring);
		} else {
			initializeFromString(rawstring);
		}

		/**
		 * String[] splitstring = rawstring.split("\\s"); dots = new
		 * dot[splitstring.length]; for (int i = 0; i < splitstring.length; i++)
		 * { String[] splitline = splitstring[i].split(","); if
		 * (splitline.length < 2) { dots[i] = new dot(0, 0); } else { int x =
		 * Integer.valueOf(splitline[0]); int y = Integer.valueOf(splitline[1]);
		 * Integer[] dotindex = new Integer[splitline.length - 2]; dot[]
		 * targetedDots = new dot[splitline.length - 2]; for (int j = 0; j <
		 * splitline.length - 2; j++) { dotindex[j] =
		 * Integer.valueOf(splitline[j + 2]); targetedDots[j] =
		 * dots[dotindex[j]]; }
		 * 
		 * dots[i] = new dot(x, y, targetedDots); } }
		 **/
	}

	private void initializeFromSource(String input) {
		//String newfilename = "C:\\Documents and Settings\\Dan\\Desktop\\race\\"
		//		+ input;
		String debug = "";
		// first pass
		ArrayList outerlist = new ArrayList();
		try {
			//Scanner fileRead = new Scanner(new File(newfilename));
			//getClass().getResourceAsStream("");
			Scanner fileRead = new Scanner((getClass().getResourceAsStream("/data/" + input + ".txt")));
			while (fileRead.hasNextLine()) {
				ArrayList innerlist = new ArrayList();
				Scanner parse = new Scanner(fileRead.nextLine())
						.useDelimiter(",");
				debug += "w1 ";
				while (parse.hasNextInt()) {
					innerlist.add((Integer) parse.nextInt());
					debug += "w2 ";
				}
				outerlist.add(innerlist);
				parse.close();
				debug += "\r";
			}
			for (int i = 0; i < outerlist.size(); i++) {
				debug += "i1 ";
				ArrayList temp = (ArrayList) outerlist.get(i);
				if (temp.size() < 2) {
					outerlist.remove(i);
				}
			}
			debug += "\r";
			dots = new dot[outerlist.size()];
			for (int i = 0; i < outerlist.size(); i++) {
				debug += "i2 ";
				ArrayList temp = (ArrayList) outerlist.get(i);
				Integer xtemp = (Integer) temp.get(0);
				Integer ytemp = (Integer) temp.get(1);
				Integer[] dotindex = new Integer[temp.size()];

				for (int j = 0; j < temp.size() - 2; j++) {
					dotindex[j] = (Integer) temp.get(j + 2);
					debug += "j1 ";
				}

				dot[] tempdots = new dot[temp.size() - 2];
				for (int j = 0; j < tempdots.length; j++) {
					tempdots[j] = dots[dotindex[j]];
					debug += "j2 ";
				}
				if (temp.size() == 2) {
					tempdots = null;
				}
				dots[i] = new dot(xtemp, ytemp, tempdots);
			}
			fileRead.close();
		} catch (Exception IOException) {
			System.out.println(input);
			System.out.println(debug);
			System.out.println(dots.length);
			for (int i = 0; i < dots.length; i++) {
				if (dots[i] == null) {
					System.out.println("null");
				} else {
					System.out.println(dots[i].drawto.length);
				}
			}
		}
	}

	private void initializeFromString(String input) {
		String[] splitstring = input.split("\\s");
		dots = new dot[splitstring.length];
		for (int i = 0; i < splitstring.length; i++) {
			String[] splitline = splitstring[i].split(",");
			if (splitline.length < 2) {
				dots[i] = new dot(0, 0);
			} else {
				int x = Integer.valueOf(splitline[0]);
				int y = Integer.valueOf(splitline[1]);
				Integer[] dotindex = new Integer[splitline.length - 2];
				dot[] targetedDots = new dot[splitline.length - 2];
				for (int j = 0; j < splitline.length - 2; j++) {
					dotindex[j] = Integer.valueOf(splitline[j + 2]);
					targetedDots[j] = dots[dotindex[j]];
				}

				dots[i] = new dot(x, y, targetedDots);
			}
		}
	}
	private void initializeBox(){
		dots = new dot[4];
		dots[0] = new dot(-10, -10);
		dots[1] = new dot(-10, 10, dots[0]);
		dots[2] = new dot(10, 10, dots[1]);
		dots[3] = new dot(10, -10, dots[2], dots[0]);
	}

	private class dot {
		private dot[] drawto;
		private int x, y; // non-rotated coordinates relative to center
		private int xnew, ynew; // coordinates after rotational translation

		public dot(int x, int y, dot... drawto) {
			this.x = -x;
			this.y = y;
			this.drawto = drawto;
		}

		public dot(int x, int y) {
			this.x = -x;
			this.y = y;
			this.drawto = null;
		}
	}

	public void draw(int angle, int x, int y, Graphics g) {
		for (int i = 0; i < dots.length; i++) {
			int length = (int) Math.sqrt(Math.pow(dots[i].x, 2)
					+ Math.pow(dots[i].y, 2));
			double degrees = MyMath.aTan(-dots[i].y, dots[i].x);
			dots[i].xnew = -(int) (MyMath.Cos(angle + degrees - 90) * length)
					+ x;
			dots[i].ynew = (int) (MyMath.Sin(angle + degrees - 90) * length)
					+ y;
			// g.drawOval(dots[i].xnew -5, dots[i].ynew -5, 10, 10);
		}
		for (int i = 0; i < dots.length; i++) {
			if (dots[i].drawto != null) {
				for (int j = 0; j < dots[i].drawto.length; j++) {
					g.drawLine(dots[i].xnew, dots[i].ynew,
							dots[i].drawto[j].xnew, dots[i].drawto[j].ynew);
				}
			}
		}
	}
}