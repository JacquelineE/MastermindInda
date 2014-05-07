import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JOptionPane;


public class Mastermind extends JFrame implements ActionListener {
	// The colors available in this game.
	private Color[]				colorArray = {	Color.blue,				// 0
												Color.red,				// 1
												Color.yellow,			// 2
												Color.green,			// 3
												Color.pink,				// 4
												new Color(200, 50, 200),// 5
												Color.cyan,				// 6
												new Color(100, 0, 100)	// 7
												};
	private ArrayList<Color> 	colors = new ArrayList<Color>();

	private int					numColors = 4;						// Amount of colors a code will consist of.
	private int					indexOfGuess = 0;					// Holds the index of the color we are trying to add to our guess.
	private ArrayList<Color>	theCode  = new ArrayList<Color>();	// The secret code, hidden from the player.
	private ArrayList<Color>	theGuess = new ArrayList<Color>();	// The most recent guess of the player.
	private int					currentGuess = 0;					// The current guess index.
	private int					maxGuesses = 10;					// Maximum amount of guesses allowed.
	public static final Color	emptyColor = Color.darkGray;

	// Menus
	private JMenuBar			menu = new JMenuBar();
	private JMenu				mGame = new JMenu("Game"),
								mSettings = new JMenu("Settings");
	private JMenuItem			miExit = new JMenuItem("Exit game"),
								miNewGame = new JMenuItem("New game");

	// Frame
	private Row					answer = new Row(this);		// Contains the secret code.
	private JPanel				panelOfRows = new JPanel(),	// Contains the rows of guesses.
								play = new JPanel();		// Contains the interactive parts of the game.

	// Answer


	// Rows
	private Row[]				rows;					// Contains the rows with guesses

	// Play
	private JPanel 				westPanelPlay;
	private JPanel				eastPanelPlay;
	private JButton[]			butColor;
	private JButton 			submit;
	private JButton 			undo;

	public Mastermind() {
		// Add the colors that will be available in this game to 'colors'.
		for (int i = 0; i < colorArray.length; i++) {
			colors.add(colorArray[i]);
		}

		///////////////////////////////////////////////////////////////////////////////
		// GUI Stuff --->															 //
		///////////////////////////////////////////////////////////////////////////////
		setTitle("Jacpue's Mastermind");	// The title of our frame.

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// MENUS
		menu.add(mGame);
		menu.add(mSettings);
		setJMenuBar(menu);
		mGame.add(miNewGame);
		mGame.add(miExit);
		miExit.addActionListener(this);
		miNewGame.addActionListener(this);
		
		// ANSWER
		answer.setBackground(Color.gray);
		answer.setPreferredSize(new Dimension(300, 50));

		// ROWS
		panelOfRows.setBackground(Color.darkGray);
		panelOfRows.setLayout(new GridLayout(maxGuesses, 1));
		rows = new Row[maxGuesses];	// Panel of rows
		for (int i = 0; i < rows.length; i++)  {
			rows[i] = new Row(this, i);
		}

		for (int i = maxGuesses-1; i >=0; i--) {
			panelOfRows.add(rows[i]);	
		}

		// PLAY
		Color colorPlay = Color.darkGray;
		play.setBackground(colorPlay);

		westPanelPlay = new JPanel();
		eastPanelPlay = new JPanel();
		westPanelPlay.setBackground(colorPlay);
		eastPanelPlay.setBackground(colorPlay);
		
		// Color buttons
		butColor = new JButton[colors.size()];
		for (int i = 0; i < colors.size(); i++) {
			butColor[i] = new JButton();
			butColor[i].setBackground(colors.get(i)); 
			butColor[i].setMargin(new Insets(0,0,0,0));
			butColor[i].setText(i + 1 + "");
			butColor[i].setPreferredSize(new Dimension(30, 30));
			butColor[i].addActionListener(this);
			//butColor[i].setBorder(null);
			play.add(butColor[i]);
		}


		// Submit button
		submit = new JButton();
		//submit.setMnemonic(KeyEvent.VK_S); //alt -enter is a shortcut for submit
		submit.setBackground(Color.white);
		submit.setPreferredSize(new Dimension(80, 30));
		submit.setText("Submit");
		//submit.setBorder(null);
		submit.addActionListener(this);
		submit.setEnabled(false);
		submit.setToolTipText("Click this button to submit your guess.");
		play.add(submit);

		// Undo button
		undo = new JButton();
		//undo.setMnemonic(KeyEvent.VK_U);
		undo.setBackground(Color.white);
		undo.setPreferredSize(new Dimension(70, 30));
		undo.setText("Undo");
		undo.addActionListener(this);
		undo.setEnabled(false);
		undo.setToolTipText("Click this button to undo your most recent color input.");
		play.add(undo);


		//play.add(westPanelPlay, BorderLayout.WEST);
		//play.add(eastPanelPlay, BorderLayout.EAST);

		this.add(answer, BorderLayout.NORTH);
		this.add(panelOfRows, BorderLayout.CENTER);
		this.add(play, 	BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(null); // Center frame.
		this.setResizable(false);
		this.setVisible(true);
		///////////////////////////////////////////////////////////////////////////////
		// <--- End of GUI stuff													 //
		///////////////////////////////////////////////////////////////////////////////
		
		// Allow keyboard input
		this.addKeyListener(kl);
		this.requestFocus();

		resetGame(); // Create a random code.
		//answer.setCode(theCode); // Show the hidden code (for testing purposes) <----------------
		System.out.println(theCode);
		}

	/**
	 * Reset the game 
	 */
	private void resetGame() {
		for (int i = 0; i < rows.length; i++) {
			rows[i].resetRow();
		}
		answer.resetRow();
		currentGuess = 0;
		indexOfGuess = 0;
		enableColorButtons();
		submit.setEnabled(false);
		undo.setEnabled(false);
		newGuess();
		createRandomCode();
		rows[0].setActive(true);
	}
	
	/**
	 * addColor(int i) Adds a color to the players' guess.
	 * @param i is the index of the color to be added to the guess.
	 */
	private void addColor(int i) {
//		for(int j = 0; j < theGuess.size(); j++) {
//		System.out.println(theGuess.get(j));
//		
//		}
//	System.out.println("ft " +indexOfGuess);
		if (indexOfGuess < numColors) {
			theGuess.set(indexOfGuess, colorArray[i]);
			if (indexOfGuess < numColors) {
				indexOfGuess++;
				//System.out.println("et " +indexOfGuess);
			}
		}
		rows[currentGuess].setCode(theGuess);
	}


	private void disableColorButtons() {
		for (int i = 0; i < butColor.length; i++) {
			butColor[i].setEnabled(false);
		}
	}

	private void enableColorButtons() {
		for (int i = 0; i < butColor.length; i++) {
			butColor[i].setEnabled(true);
		}
	}

	/**
	 * compareCode() will compare two codes.
	 * @param code1 The first code.
	 * @param code2 The second code.
	 * @return Returns true if the codes are equal.
	 */
	private boolean compareCode(ArrayList<Color> code1, ArrayList<Color> code2){
		for (int i = 0; i < code1.size(); i++) {
			if (code1.get(i) != code2.get(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * checkGuess() evaluates the guess, and checks if the player was right. If
	 * not, let the player know how many colors were correct, and how many
	 * colors were correctly placed.
	 */
	private void checkGuess() {
		int 	color = 0, 			// correct color but wrong place.
				placeAndColor = 0; 	// correct color AND place.
		// if the guess is right, you win!
		if (compareCode(theGuess, theCode)) {
			win();
		// otherwise, check how many colors were right.
		} else if (currentGuess < maxGuesses - 1) {
			// i = the index of theCode.
			for (int i = 0; i < numColors; i++) {
				// if the color and place is correct:
				if (theGuess.get(i) == theCode.get(i)) {
					placeAndColor++;
				// if the place is wrong, check if the color is correct:
				} else {
					if (theCode.contains(theGuess.get(i))) {
						color++;
					}
				}
			}
			System.out.println(currentGuess);
			rows[currentGuess].giveFeedback(placeAndColor, color);
			rows[currentGuess].setActive(false);
			newGuess();
			currentGuess++;
			if (currentGuess < maxGuesses - 1) {
				rows[currentGuess].setActive(true);	
			}
		// if you have no tries left, you lose!
		} else {
			lose();
		}
	}

	/**
	 * createRandomCode() will create a new random code and store it in 'theCode'.
	 * TODO: Fix so that you cannot have two of the same number.
	 */
	private void createRandomCode() {
		theCode.clear();
		theCode = new ArrayList<Color>(colors);
		Collections.shuffle(theCode);
		while (theCode.size() > numColors) {
			theCode.remove(0);
		}
		// System.out.println(theCode);
	}

	/**
	 * exitGame() asks the user whether or not he or she actually wants to quit the game.
	 * If the answer is yes, exit the game.
	 */
	private void exitGame() {
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit game", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	/**
	 * Lose is called when you've lost.
	 * Notifies the player and resets the game.
	 */
	private void lose() {
		disableColorButtons();
		submit.setEnabled(false);
		undo.setEnabled(false);
		JOptionPane.showMessageDialog(this, "Game Over!\nYou've lost. Better luck next time.");
		System.out.println("Game Over!\n Better luck next time."); // TESTRAAAAAAAAAAAAAAAAAAAAAD <----------------------
	}

	/**
	 * getMaxGuesses() returns the maximum amount of guesses allowed.
	 * @return <b>int</b> maxGuesses: max amount of guesses allowed.
	 */
	public int getMaxGuesses() {
		return maxGuesses;
	}

	/**
	 * getNumberOfColors() returns the number of colors the code is made of.
	 * @return Returns the number of colors the hidden code consists of.
	 */
	public int getNumberOfColors() {
		return numColors;
	}

	/**
	 *  newGuess() clears theGuess.
	 */
	public void newGuess() {
		theGuess.clear();
		theGuess = new ArrayList<Color>(makeEmptyCode());
	}

	/**
	 * Make a code without colors
	 * @return an empty code
	 */
	
	public ArrayList<Color> makeEmptyCode() {
		ArrayList<Color> array = new ArrayList<Color>();
		for (int i = 0; i < numColors; i++) {
			array.add(emptyColor);			
		}
		return array;
	}
	
	/**
	 * setNumberOfColors() will set the amount of colors in each code.
	 * 
	 * @param n The total number of colors a code will consist of. <br> n must be between 1 and the length of color.
	 */
	private void setNumberOfColors(int n) {
		if (numColors > 0 && numColors <= colors.size()) {
			numColors = n;
		} else {
			numColors = 4; //WE COULD GIVE A WARNING IN A DIALOGBOX INSTEAD!?!?!
		}
	}

	/**
	 * Win is called when you've won.
	 * Congratulates the player and resets the game.
	 */
	private void win() {
		disableColorButtons();
		submit.setEnabled(false);
		undo.setEnabled(false);
		answer.setCode(theCode);
		JOptionPane.showMessageDialog(this, "Congratulations!\nYou've won!");
		System.out.println("Congratulations! You've won!"); // TESTRAAAAAAAAAAAAAAAAAAAAAD <----------------------
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Mastermind();	// Start the game.
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// If a color button is pushed, add that color to the users' guess.
		for (int i = 0; i < butColor.length; i++) {
			if (e.getSource() == butColor[i]) {
				butColor[i].setEnabled(false);
				addColor(i);
				if (indexOfGuess == numColors) { 
					disableColorButtons();  
					submit.setEnabled(true);
				}
				if(indexOfGuess > 0) {
					undo.setEnabled(true);
				}
			}
		}
		
		// When the submit button is pushed, check if the answer is correct, and go to the next guess.
		if (e.getSource() == submit) {
			enableColorButtons();
			checkGuess();
			indexOfGuess = 0;
			undo.setEnabled(false);
			submit.setEnabled(false);

		}

		// When the undo button is pushed, remove the last color from the guess and replace it with 'emptyColor'.
		if (e.getSource() == undo) {
			enableColorButtons();
			for (int i = numColors - 1; i >= 0; i--) {
				if (theGuess.get(i) != emptyColor) {
					theGuess.set(i, emptyColor);
					rows[currentGuess].setCode(theGuess);	
					indexOfGuess--;
					if(indexOfGuess < 1) {
						undo.setEnabled(false);
					}
					submit.setEnabled(false);
					for (int j = 0; j < butColor.length; j++) {
						if(theGuess.contains(butColor[j])) {
							System.out.println("hej");		
							butColor[j].setEnabled(false);
						}
					}
					break;
				}			
			}
		}
		
		// MENU OPTIONS:
		
		// If the exit button is pushed, exit the game.
		if (e.getSource() == miExit) {
			exitGame();
		}
		
		// If the New game button is pushed, start a new game.
		if (e.getSource() == miNewGame) {
			resetGame();
		}
		this.requestFocus();
	}

	/**
	 * Listen to the keyboard
	 */
	KeyListener kl = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case(KeyEvent.VK_ENTER): {
					System.out.println("du tryckte på enter");
					submit.doClick();
					
					break;
				}
				case(KeyEvent.VK_BACK_SPACE): {
					undo.doClick();
					break;
				}
				case(KeyEvent.VK_1): {
					butColor[0].doClick();
					break;
				}
				case(KeyEvent.VK_2): {
					butColor[1].doClick();
					break;
				}
				case(KeyEvent.VK_3): {
					butColor[2].doClick();
					break;
				}
				case(KeyEvent.VK_4): {
					butColor[3].doClick();
					break;
				}
				case(KeyEvent.VK_5): {
					butColor[4].doClick();
					break;
				}
				case(KeyEvent.VK_6): {
					butColor[5].doClick();
					break;
				}
				case(KeyEvent.VK_7): {
					butColor[6].doClick();
					break;
				}
				case(KeyEvent.VK_8): {
					butColor[7].doClick();
					break;
				}
				case(KeyEvent.VK_Q): {
					if (e.isControlDown()) {
						exitGame();
					}
					break;
				}
				default: {
					System.out.println("Det var en annan knapp som trycktes.");
					break;
				}
			}
		}
	};
}
