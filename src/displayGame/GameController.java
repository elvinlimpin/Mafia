package displayGame;

import logic.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * Class GameController
 * 
 * 
 * 
 * 
 * 
 * @author Pierce de Jong 30006609
 *
 */
public class GameController {
	
	private static GameController instance = null;
	
	private JFrame frame;
	
	private Game g;
	private DayPanel dp;
	private NightPanel np;
	private CheckPlayerPanel cpp;
	private StoryPanel sp;
	private ViewAllPlayersPanel vapp;
	private ViewPlayerPanel vpp;
	private Listener listener;
	
	//All of the possible panels to be displayed on the frame
	private JPanel panelDay;
	private JPanel panelNight;
	private JPanel panelCheck;
	private JPanel panelStory;
	private JPanel panelViewAllPlayers;
	private JPanel panelViewPlayer;
	//Location inside the list of players for the night cycle
	private int position = 0;
	
	//Must be private. so only one instance can be made
	private GameController(JFrame frame){
		this.frame = frame;
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
		
		listener = new Listener();
	}
	/**
	 * This method ensures only one instance of the GameController Class can be made
	 * @param frame
	 */
	public static void createInstance(JFrame frame){
		if(instance==null){
			instance = new GameController(frame);
		}
	}
	
	public static GameController getInstance(){
		if(instance!=null){
			return instance;
		}else{
			return null;
		}
	}
	
	public void start(List<Player> playerInfo, int lynchTarget){
		g = new Game(playerInfo,lynchTarget);
		cpp = new CheckPlayerPanel(listener);
		dp = new DayPanel(listener,g.getPlayerInfo());
		np = new NightPanel(listener,g.getPlayerInfo(),g.getMafiaMember());
		sp = new StoryPanel(listener);
		
		vapp = new ViewAllPlayersPanel(listener,g.getPlayerInfo());
		//vpp = new ViewPlayerPanel(g.getMafiaMember());
		
		switchDay();
	}
	
	public void switchViewAllPlayers(){
		//Hides the current content Pane
		frame.getContentPane().setVisible(false);
		//
		panelViewAllPlayers = vapp.getContentPane();
		//sets the frame's content pane to day screen
		frame.setContentPane(panelViewAllPlayers);
		//sets the current content pane to visible
		panelViewAllPlayers.setVisible(true);
	}
	
	public void switchViewPlayer(int i){
		vpp.setPlayer(g.getPlayerInfo().get(i));
		panelViewPlayer = vpp.getContentPane();
		switchPanel(panelViewPlayer);
	}
	
	/**
	 * switches the content panel to the dayCycle page
	 */
	public void switchDay(){
		System.out.println("Day Panel");
		dp.resetTarget();
		panelDay = dp.getContentPane();
		switchPanel(panelDay);
	}

	
	/**
	 * Updates the north panel of the Night Panel text with the information of the next player
	 * Hide the current view
	 * set the nightPanel to the update ContentPane
	 * update the frame with new content pane
	 */
	public void switchNight(){
		System.out.println("Night Panel");
		np.setDisplay(position);//Sets the display for current player to select his/her target
		panelNight = np.getContentPane();//Refreshes the content pane to adjust for updates
		switchPanel(panelNight);
	}
	/**
	 * Updates the Check Player panel text with the next player
	 * Switch the frame to the CheckPlayerPanel
	 */
	public void switchCheckPlayer(){
		System.out.println("Check Player Panel");
		cpp.setPlayerName(g.getPlayerInfo().get(position).getName());
		panelCheck = cpp.getContentPane();
		switchPanel(panelCheck);
	}
	/**
	 * Switches the frame to the Story Panel to display the death or savior of a player
	 * @param name
	 * @param dead
	 */
	public void switchStory(String name, boolean dead){
		System.out.println("Story Panel");
		sp.setStory(name,dead);
		panelStory = sp.getContentPane(); 
		switchPanel(panelStory);
	}
	
	private void switchPanel(JPanel panel){
		frame.getContentPane().setVisible(false);
		frame.setContentPane(panel);
		frame.getContentPane().setVisible(true);
	}
	/**
	 * Lynches the player who was targeted during the day
	 * Removes that players button for both the day and night panels
	 * @param target
	 */
	private void dayLynch(int target){
		//Lynches the player who was the target during the day 
		g.dayCycle(target);
		dp.removePlayerButton(target);
		np.removePlayerButton(target);
		//Sets the 
		position = 0;
	}
	/**
	 * Calls the night Action method in the game class and odes all of the logic for each player each night
	 * If there was a target of action that night
	 */
	private void nightAction(){
		//If someone was saved or killed this night sets event to their index value
		//Set to -1 if no one was saved or killed that night
		int target = g.nightAction();
		//If there was a target this night
		if(target!=-1){
			//If the player was killed that night remove the player button from both the 
			if(!g.getPlayerInfo().get(target).isHealed()){
				dp.removePlayerButton(target);
				np.removePlayerButton(target);
			}else{
				g.getPlayerInfo().get(target).setIsHealed(false);
			}
			//Switches to the story panel to make a story about what happened to the player
			String name = g.getPlayerInfo().get(target).getName();
			boolean dead = !g.getPlayerInfo().get(target).isHealed();
			switchStory(name,dead);
		}else{
			//Skip the story panel and go to the next day.
			switchDay();
		}
	}
	/**
	 * Finds the next player who is alive in the list of players
	 * Uses recursion and a global variable called position to loop through the list of players
	 * If the player is alive displays the CHeckPLayer Panel, if not finds the next player
	 * Once the position has gone beyond the index of playerInfo, calls the nightAction method
	 */
	private void findNextPlayer(){
		//If the position has not gone out of bounds
		if(position<g.getPlayerInfo().size()){
			//If the player is dead, find the next one
			if(g.getPlayerInfo().get(position).isDead()){
				position++;
				findNextPlayer();
			}else{//If not, display the checkPlayerPanel for the next player in the list	
				switchCheckPlayer();
			}
		//If the position has gone past the list of players, do the night actions of the players and 
		}else if(position==g.getPlayerInfo().size()){
			nightAction();
		}
	}
	/**
	 * This class sets the message for the detective during the night
	 * It looks at the index value of the target of the detective
	 * THen displays if they are art of the Mafia or not
	 * @param target
	 */
	private void detective(int target){
		//If the target of the detective player is part of the Mafia
		//*Note* the Mafia GodFather is hidden from the detective
		if(g.getPlayerInfo().get(target).isMafia()){
			np.setDetectiveMessage("Part of the Mafia");
		}else{
			np.setDetectiveMessage("Not part of the Mafia");
		}
	}
	/**
	 * This class is the ActionListenr for all of the buttons in the displayGame Package
	 * When a button is pressed, the name String of the button is stored as a local variable
	 * A switch statement is used to compare the name with other string values to find the correct button
	 */
	public class Listener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			//Gets the name (NOT TEXT) of the button that was pressed
			JButton source = (JButton)e.getSource();
			String name = source.getName();
			//Finds the button that was pressed and does the needed commands
			switch(name){
			case "Continue_ViewAllPlayersPanel":
				switchDay(); 
				break;
			case "Continue_DayPanel":
				int target = dp.getTarget();//Gets the target of the Day panel (THe person voted out and lynched before night)
				if(target!=-1){//Makes sure a target has been chosen for the day 
					dayLynch(target);//Lynches the target of the day
					findNextPlayer();//Finds the first person in the list of players that is alive and displays his/her night screen
				}
				break;
			case "Continue_CheckPlayerPanel":
				switchNight();
				break;
			case "Continue_NightPanel":
				int target2 = np.getPlayerTarget();
				g.setPlayerTarget(position, target2);
				position++;
				findNextPlayer();
				break;
			case "Continue_StoryPanel":
				switchDay();
				break;
			case "Detective":
				int target3 = np.getPlayerTarget();
				if(target3!=-1){
					detective(target3);
				}
				break;
			default:
				break;
			}
        
		}
    }
}
