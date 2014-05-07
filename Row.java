import java.awt.*;
import java.util.*;
import javax.swing.*;


public class Row extends JPanel {

	private int w = 26,
				h = w;

	private boolean middleRow = false; // Rows that belongs to PanelOfRows except for the first row.
	private String	rownr = "";
	private String places = "";
	private String colors = "";
	private Mastermind gameFrame;
	private ArrayList<Color> code  = new ArrayList<Color>();	// The code that should be presented to the player.
	private boolean answerRow = false;
	
	public static final Color activeColor = Color.gray;
	public static final Color inactiveColor = new Color(50, 50, 50);
	public static final Color backColor = Color.black;
	public static final Color answerColor = Color.gray;

	public Row() {
		this.setBackground(backColor);
	}

	/**
	 * This constructor is used to create a Answer Row.
	 * @param gf the parent window(Mastermind).
	 */
	public Row(Mastermind gf) {
		this();
		gameFrame = gf;
		code = new ArrayList<Color>(gf.makeEmptyCode());
		answerRow = true;
	}

	/**
	 * This constructor is used to create a (PanelOfRows) Row.
	 * @param gm the parent window(Mastermind).
	 * @param num the number of this row.
	 */
	public Row(Mastermind gm, int num) {
		this();
		gameFrame = gm;
		rownr = (num + 1) + ".";
		if (num < gm.getMaxGuesses() - 1) {	// Rows that belongs to PanelOfRows except for the last row.
			middleRow = true;
		}
		this.setPreferredSize(new Dimension(460, 50));
	}

	/**
	 * giveFeedback() sets the labels of the feedback in this Row.
	 * @param places amount of correct colors in the <b>correct</b> place.
	 * @param color amount of correct colors in the <b>wrong</b> place.
	 */
	public void giveFeedback(int places, int color) {
		this.places = "Correct place and color: " + places;
		this.colors = "Correct color but wrong place: " + color;
		System.out.println("HERES THE FEEDBACK YOU REQUESTED SIR!"); // TESTRAAAAAAAD <-----------------------
		repaint();
	}

	public void setActive(boolean active) {

		if (active) {
			this.setBackground(activeColor);
		} else {
			this.setBackground(inactiveColor);
		}
	}
	
	/**
	 * Reset row
	 */
	public void resetRow() {
		places = "";
		colors = "";
		if (answerRow) {
			setBackground(answerColor);
		} else {
			setBackground(backColor);	
		}
		setCode(gameFrame.makeEmptyCode());
	}
	
	
	/**
	 * setCode(code) sets the code that this Row will contain.
	 * @param gameCode is the code to be showed in this Row.
	 */
	public void setCode(ArrayList<Color> gameCode) {
		code = new ArrayList<Color>(gameCode);
		this.repaint();
	}

	/**
	 * paint() draws this Row onto the JPanel "canvas".
	 */
	public void paint(Graphics g) {
		super.paint(g);
		// Draw the row number:
		g.setColor(Color.white);
		g.setFont(new Font("Serif", Font.BOLD, 20));
		g.drawString(rownr, 10, 30);
		// Draw the code:
		for (int i = 0; i < gameFrame.getNumberOfColors(); i++) {
			g.setColor(code.get(i));
			g.fillOval((70 + 35 * i), 11, w, h);	
		}
		// Draw the feedback:
		g.setFont(new Font("Serif", Font.BOLD, 14));
		g.setColor(Color.red);
		g.drawString(places, 250, 20);
		g.setColor(Color.white);
		g.drawString(colors, 250, 36);
	}
}