import java.applet.*; // Sound
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/*
	Authors: Jacqueline Ericsson Shapour Jahanshahi
*/


public class Mastermind extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// The colors available in this game.
	private Color[]	colorArray = {	new Color(200, 0, 0),	// 0, Red
									new Color(255, 140, 0), // 1, Orange
									new Color(255, 230, 0),	// 2, Yellow
									new Color(50, 100, 0),	// 3, Green
									new Color(17, 255, 243),// 4, Teal
									new Color(50, 50, 255),	// 5, Blue
									new Color(128, 0, 194),	// 6, Purple
									new Color(255, 50, 255),// 7, Pink
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
								mSettings = new JMenu("Settings"),
								mHelp = new JMenu("Help");
	//mGame menu items.
	private JMenuItem			miExit = new JMenuItem("Exit game"),
								miNewGame = new JMenuItem("New game");
	//mSettings menu items.
	private JMenuItem			miSetNumColors = new JMenuItem("Code size"),
								miSetAudio = new JMenuItem("Disable Audio");
	//mHelp menu items.
	private JMenuItem			miHelp = new JMenuItem("How to play"),
								miKeys = new JMenuItem("Keyboard Shortcuts");

	// Frame
	private JPanel				top = new JPanel(new BorderLayout()),	// Contains the secret code.
								panelOfRows = new JPanel(),	// Contains the rows of guesses.
								play = new JPanel();		// Contains the interactive parts of the game.
	private ImageIcon			ball = new ImageIcon(Mastermind.class.getResource("/img/ball.png"));

	// Top
	private Row 				answer = new Row(this);
	
	// Rows
	private Row[]				rows;	// Contains the rows with guesses

	// Play
	private JButton[]			butColor;	// Contains the Color buttons.
	private JButton 			submit,		// Submit button
								undo;		// Undo button
	private static final Color	colorPlay = Color.darkGray;
	
	// Help window
	private JFrame		frameHelp	= new JFrame("Instructions for Mastermind");
	private JPanel		helpTop		= new JPanel(new BorderLayout()),
						helpBot		= new JPanel();
	private JLabel		labelHelp	= new JLabel();
	private ImageIcon	icon		= new ImageIcon(Mastermind.class.getResource("/img/question.png"));
	private JButton		help		= new JButton(icon),
						helpBack	= new JButton("Back"),
						helpHelp	= new JButton("How to Play"),
						helpKeys	= new JButton("Keyboard Shortcuts");
	
	private String		helpText;
	
	
	// Sound
	private AudioClip 	aComplete	= Applet.newAudioClip(Mastermind.class.getResource("/sound/complete.wav")),
						aError 		= Applet.newAudioClip(Mastermind.class.getResource("/sound/error.wav")),
						aWinner		= Applet.newAudioClip(Mastermind.class.getResource("/sound/winner.wav"));
	private boolean   	soundEnabled = true;
	
	
	public Mastermind() {
		// Add the colors that will be available in this game to 'colors'.
		for (int i = 0; i < colorArray.length; i++) {
			colors.add(colorArray[i]);
		}

		// Help window stuff
		frameHelp.setLayout(new BorderLayout());
		frameHelp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		helpTop.add(new JLabel(icon));
		helpTop.setBackground(Row.answerColor);
		helpTop.setPreferredSize(new Dimension(300,50));
		
		labelHelp.setBackground(Color.white);
		labelHelp.setOpaque(true);
		labelHelp.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
		labelHelp.setText(helpText);
		
		helpHelp.addActionListener(this);
		helpHelp.setBackground(Color.white);
		helpHelp.setPreferredSize(new Dimension(100,30));
		helpKeys.addActionListener(this);
		helpKeys.setBackground(Color.white);
		helpKeys.setPreferredSize(new Dimension(160,30));
		helpBack.addActionListener(this);
		helpBack.setBackground(Color.white);
		helpBack.setPreferredSize(new Dimension(70,30));
		helpBot.setBackground(colorPlay);
		helpBot.add(helpHelp);
		helpBot.add(helpKeys);
		helpBot.add(helpBack);
		helpBot.setPreferredSize(new Dimension(460,50));
		
		frameHelp.add(helpTop, BorderLayout.NORTH);
		frameHelp.add(labelHelp, BorderLayout.CENTER);
		frameHelp.add(helpBot, BorderLayout.SOUTH);
		
		frameHelp.setIconImage(ball.getImage());
		frameHelp.setLocationRelativeTo(null); // Center frame.
		frameHelp.setPreferredSize(new Dimension(460,650));
		frameHelp.setResizable(false);
		frameHelp.pack();
		
		///////////////////////////////////////////////////////////////////////////////
		// GUI Stuff --->															 //
		///////////////////////////////////////////////////////////////////////////////
		setTitle("Jacqpue's Mastermind");	// The title of our frame.
		setIconImage(ball.getImage());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// MENUS
		menu.add(mGame);
		menu.add(mSettings);
		menu.add(mHelp);
		setJMenuBar(menu);
		
		// mGame
		mGame.setMnemonic('g');
		mGame.add(miNewGame);
		miNewGame.setMnemonic('n');
		miNewGame.addActionListener(this);
		mGame.add(miExit);
		miExit.setMnemonic('e');
		miExit.addActionListener(this);
		
		// mSettings
		mSettings.setMnemonic('s');
		mSettings.add(miSetNumColors);
		miSetNumColors.setMnemonic('c');
		miSetNumColors.addActionListener(this);
		mSettings.add(miSetAudio);
		miSetAudio.setMnemonic('a');
		miSetAudio.addActionListener(this);
		
		// mHelp
		mHelp.setMnemonic('h');
		mHelp.add(miHelp);
		miHelp.setMnemonic('h');
		miHelp.addActionListener(this);
		mHelp.add(miKeys);
		miKeys.setMnemonic('k');
		miKeys.addActionListener(this);
		
		// TOP
		answer.setPreferredSize(new Dimension(300, 50));
		help.setBackground(Row.answerColor);
		help.setBorder(null);
		help.setFont(new Font(Font.DIALOG, Font.BOLD, 46));
		help.setPreferredSize(new Dimension(50,50));
		help.setForeground(Color.white);
		help.addActionListener(this);
		top.setBackground(Row.answerColor);
		top.add(help, BorderLayout.WEST);
		top.add(answer, BorderLayout.CENTER);

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
		play.setBackground(colorPlay);
		
		// Color buttons
		butColor = new JButton[colors.size()];
		for (int i = 0; i < colors.size(); i++) {
			butColor[i] = new JButton();
			butColor[i].setBackground(colors.get(i)); 
			butColor[i].setMargin(new Insets(0,0,0,0));
			butColor[i].setText((i + 1) + "");
			butColor[i].setPreferredSize(new Dimension(30, 30));
			butColor[i].addActionListener(this);
			play.add(butColor[i]);
		}


		// Submit button
		submit = new JButton();
		submit.setBackground(Color.white);
		submit.setPreferredSize(new Dimension(80, 30));
		submit.setText("Submit");
		submit.setMnemonic('s');
		submit.addActionListener(this);
		submit.setEnabled(false);
		submit.setToolTipText("Click this button to submit your guess.");
		play.add(submit);

		// Undo button
		undo = new JButton();
		undo.setBackground(Color.white);
		undo.setPreferredSize(new Dimension(70, 30));
		undo.setText("Undo");
		undo.setMnemonic('u');
		undo.addActionListener(this);
		undo.setEnabled(false);
		undo.setToolTipText("Click this button to undo your most recent color input.");
		play.add(undo);

		this.add(top, BorderLayout.NORTH);
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
		
		setHelpText();
		resetGame(); // Start a new game.
		
		}

	/**
	 * addColor(int i) Adds a color to the players' guess.
	 * @param i is the index of the color to be added to the guess.
	 */
	private void addColor(int i) {
		if (indexOfGuess < numColors) {
			theGuess.set(indexOfGuess, colorArray[i]);
			if (indexOfGuess < numColors) {
				indexOfGuess++;
			}
		}
		rows[currentGuess].setCode(theGuess);
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
	 * createRandomCode() will create a new random code and store it in 'theCode'.
	 */
	private void createRandomCode() {
		theCode.clear();
		theCode = new ArrayList<Color>(colors);
		Collections.shuffle(theCode);
		while (theCode.size() > numColors) {
			theCode.remove(0);
		}
	}
	
	/**
	 * Disables all color buttons.
	 */
	private void disableColorButtons() {
		for (int i = 0; i < butColor.length; i++) {
			butColor[i].setEnabled(false);
		}
	}

	/**
	 * Re-enables all color buttons.
	 */
	private void enableColorButtons() {
		for (int i = 0; i < butColor.length; i++) {
			butColor[i].setEnabled(true);
		}
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
	 * inputNumbers() asks the user to input some numbers.
	 * @param message A message to the user.
	 * @param title The name of the window.
	 * @return The number the user enters or -1 if no input was given.
	 */
	private int inputNumbers(String message, String title) {
		String input;
		// Only allow correct input
		while (true) {
			input = JOptionPane.showInputDialog(null,
				message,
		        title, 
		        JOptionPane.QUESTION_MESSAGE
		        );
			// If user presses cancel, go back to the game.
			if (input == null) {
				this.requestFocus();
				return -1;
			}
			if (input.matches("[0-9]+")) {
				break;
			}
		}
		return Integer.parseInt(input.trim());
	}
	
	/**
	 * Lose is called when you've lost.
	 * Notifies the player and resets the game.
	 */
	private void lose() {
		disableColorButtons();
		submit.setEnabled(false);
		undo.setEnabled(false);
		answer.setCode(theCode);
		playSound(aError);
		if (JOptionPane.showConfirmDialog(null, "You have lost!\n" +
												"Do you wish to play again?", 
												"GAME OVER!", 
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			resetGame();
		}
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
	 *  newGuess() clears theGuess and replaces it with an empty code.
	 */
	public void newGuess() {
		theGuess.clear();
		theGuess = new ArrayList<Color>(makeEmptyCode());
	}
	
	/**
	 * Plays an AudioClip.
	 * @param clip = the AudioClip to play.
	 */
	public void playSound(AudioClip clip) {
		if (soundEnabled) {
			clip.play();
		}
	}
	
	/**
	 * Reset the game board and allows the player to play from the beginning. 
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
		this.requestFocus();
	}
	
	/**
	 *  Sets the help frame text to the How to play text.
	 */
	private void setHelpText() {
		helpText	= 	("<html><body>" +
				"<h1>How to play Mastermind?</h1>" +
				
				"<h2>Break the code!</h2><br>" +
				"<h3> Introduction</h3>" +
				
				"As you might know, this game is about cracking the secret code. The code consists of " 
				+ numColors + " colors." + "You have a set of " + colors.size() + " colors to pick from." +
				"The code consists of <i>unique colors</i> - there can't be two pegs of the same color in the code." +
				"Also, you must place the pegs in the correct position for victory. <br>" +
				"<b>Order matters!</b><br>" +
				"You have a maximum of " + maxGuesses + " guesses, so choose wisely!<br><br>" +
				
				"<h3>How to navigate</h3>" +
				
				"To start, simply press the colored buttons at the bottom of the screen. " +
				"If you want to, you can use the keyboard shortcuts 1 through " + 
				numColors + " instead of clicking the buttons." + 
				"You need to fill the entire code before you can submit your guess.<br>" +
				
				"When you have submitted your guess, you will be supplied with some feedback." +
				"The feedback lets you know how many of the pegs are of the" +
				"<br><b>correct color and in the correct place</b>, as well as how many pegs are " +
				"of the <br><b>correct color but in the wrong place.</b>" +
				
				"<br><p>That's all there is to it. Good luck out there!" +
				"</body></html>");
	}
	
	/**
	 *  Sets the help frame text to the Keyboard Shortcut text.
	 */
	private void setKbShortcutText() {
		helpText	= 	("<html>" +
				"<head> <style> th {border:1px solid black;}</style></head>" +
				"<body>" +
				"<h1> Keyboard Shortcuts</h1>" +
				
				"<h2> Navigate using the keyboard!</h2><br>" +
				" Did you know that you can use the keyboard to play this game? Well you can!<br>" +
				
				"<table>" +
				"<tr>" +
					"<th><b> Command </b></td>" +
					"<th> Shortcut </td>" +
				"</tr>" +
				
				"<tr>" +
					"<td><b> Help screen </b></td>" +
					"<td> F1 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> New Game </b></td>" +
					"<td> F2 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Submit Code </b></td>" +
					"<td> ENTER </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Undo Color </b></td>" +
					"<td> BACKSPACE </td>" +
				"</tr>" +
				
				"<tr>" +
					"<td><b> Color 1 </b></td>" +
					"<td> 1 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 2 </b></td>" +
					"<td> 2 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 3 </b></td>" +
					"<td> 3 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 4 </b></td>" +
					"<td> 4 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 5 </b></td>" +
					"<td> 5 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 6 </b></td>" +
					"<td> 6 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 7 </b></td>" +
					"<td> 7 </td>" +
				"</tr>" +
					
				"<tr>" +
					"<td><b> Color 8 </b></td>" +
					"<td> 8 </td>" +
				"</tr>" +
					
				"</table>" +
				
				"<p> That's all there is to it. Good luck out there!<br><br>" +
				"</body></html>");
	}
	
	/**
	 * setNumberOfColors() will set the amount of colors in each code.
	 * 
	 * @param num The total number of colors a code will consist of. <br> n must be between 1 and the length of color.
	 */
	private void setNumberOfColors(int num) {
		if (num > 1 && num <= colors.size()) {
			numColors = num;
		} else {
			numColors = 4; //WE COULD GIVE A WARNING IN A DIALOGBOX INSTEAD!?!?!
		}
	}
	
	/**
	 *  Pops up the help screen.
	 * @param KeyboardShortcuts <br>If <b>true</b>, show the keyboard shortcuts screen,<br>
	 * 		  If <b>false</b>, show the help screen.
	 */
	private void showHelpScreen(boolean KeyboardShortcuts) {
		// Should I go to keyboard shortcuts?
		if (KeyboardShortcuts) {
			setKbShortcutText();
			helpKeys.setEnabled(false);
			helpHelp.setEnabled(true);
		} else { // Or to help text?
			setHelpText();
			helpKeys.setEnabled(true);
			helpHelp.setEnabled(false);
		}
		labelHelp.setText(helpText);
		frameHelp.setVisible(true);
		helpBack.requestFocusInWindow();
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
		playSound(aWinner);
		if (JOptionPane.showConfirmDialog(null, "Good job!\nYou cracked the code in " + (currentGuess + 1) + " tries.\n" +
												"Do you wish to play again?", 
												"CONGRATULATIONS!", 
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			resetGame();
		}
	}

	/**
	 * Starts the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new Mastermind();	// Start the game.
	}
	
	/**
	 *  Handles events triggered by pressing buttons (JButtons and Menu options).
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// If the help button is pressed, open the help window.
		if (e.getSource() == help) {
			showHelpScreen(false);
			return;
		}
		
		// If a color button is pressed, add that color to the users' guess.
		for (int i = 0; i < butColor.length; i++) {
			if (e.getSource() == butColor[i]) {
				butColor[i].setEnabled(false);
				addColor(i);
				// Only allow the user to submit the guess when the code is full.
				if (indexOfGuess == numColors) { 
					disableColorButtons();  
					submit.setEnabled(true);
				}
				// If there's at least one color in the guess, enable Undo.
				if(indexOfGuess > 0) {
					undo.setEnabled(true);
				}
			}
		}
		
		// When the submit button is pressed, check if the answer is correct, and go to the next guess.
		if (e.getSource() == submit) {
			enableColorButtons();
			checkGuess();
			indexOfGuess = 0;
			undo.setEnabled(false);
			submit.setEnabled(false);
			playSound(aComplete);
		}

		// When the undo button is pressed, remove the last color from the guess and replace it with 'emptyColor'.
		if (e.getSource() == undo) {
			enableColorButtons();
			// Loop through theGuess backwards:
			for (int i = numColors - 1; i >= 0; i--) {
				// Go to the last non-empty color and remove it.
				if (theGuess.get(i) != emptyColor) {
					theGuess.set(i, emptyColor);
					rows[currentGuess].setCode(theGuess);	
					indexOfGuess--;
					// Don't allow the player to press undo if there's no color in the code.
					if(indexOfGuess < 1) {
						undo.setEnabled(false);
					}
					// Disable the color buttons of the colors in the code.
					for (int j = 0; j < i; j++) {
						butColor[colors.indexOf(theGuess.get(j))].setEnabled(false);
					}
					submit.setEnabled(false);
					break;
				}			
			}
		}
		
		// HELP SCREEN:
		
		// If the How to play button is pressed, show the Instructions.
		if (e.getSource() == helpHelp) {
			showHelpScreen(false);
			return;
		}
		
		// If the Keyboard Shortcuts button is pressed, show the shortcuts.
		if (e.getSource() == helpKeys) {
			showHelpScreen(true);
			return;
		}
		
		// If the Back button is pressed, close the help window.
		if (e.getSource() == helpBack) {
			frameHelp.dispose();
		}
		
		// MENU OPTIONS:
		
		// If the New game button is pressed, start a new game.
		if (e.getSource() == miNewGame) {
			resetGame();
		}
		
		// If the exit button is pressed, exit the game.
		if (e.getSource() == miExit) {
			exitGame();
		}
		// If the help button is pressed, go to help screen.
		if (e.getSource() == miHelp) {
			setHelpText();
			help.doClick();
		}
		
		// If the keyboard shortcuts button is pressed, go to keyboard shortcuts screen.
		if (e.getSource() == miKeys) {
			showHelpScreen(true);
		}
		
		// If the NumColors button is pressed, set number of colors in each code.
		if (e.getSource() == miSetNumColors) {
			int num = inputNumbers("How many colors should each code contain?", "Colors per code");
			if (num != -1) {
				setNumberOfColors(num);
				resetGame();
			}
		}
		
		// If the audio is on, disable it. Otherwise enable it.
		if (e.getSource() == miSetAudio) {
			if (soundEnabled) {
				miSetAudio.setText("Enable Sound");
				soundEnabled = false;
			} else {
				miSetAudio.setText("Disable Sound");
				soundEnabled = true;
			}
		}
		
		this.requestFocus();
	}

	/**
	 *  Handles keyboard input.
	 */
	KeyListener kl = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			// If a keyboard button is pressed, click the correlating JButton(if correct key is pressed).
			switch (e.getKeyCode()) {
				// Enter is bound to the Submit button.
				case(KeyEvent.VK_ENTER): {
					submit.doClick();
					break;
				}
				// Backspace is bound to the Undo button.
				case(KeyEvent.VK_BACK_SPACE): {
					undo.doClick();
					break;
				}
				// F1 is bound to the Help button.
				case(KeyEvent.VK_F1): {
					setHelpText();
					help.doClick();
					break;
				}
				// F2 is bound to starting a new game.
				case(KeyEvent.VK_F2): {
					resetGame();
					break;
				}
				//Keyboard keys 1-8 are bound to the corresponding Color button.
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
				// Keyboard button Q is bound to quit(exitGame()).
				case(KeyEvent.VK_Q): {
					if (e.isControlDown()) {
						exitGame();
					}
					break;
				}
				// If another keyboard key is pressed, do nothing.
				default: {
					break;
				}
			}
		}
	};
}
