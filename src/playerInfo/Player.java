package playerInfo;

/**
 * Inherited by different roles as a framework to store
 * attributes and a unique action
 * 
 * Each player is assigned a unique position:
 * -Position: index entry in List<Player> in Game
 * -Status: Possible states player may have
 * 
 * For the algorithms to work, the following night
 * players must be active players must be called
 * in order:
 * 1) Barman - Stops action
 * 2) Bodyguard - sacrifices self if target is not
 *			 	  healed
 * 3) Vigilante & Hitman - kills
 * 5) Doctor - heals
 * 
 * @author Elvin Limpin 30018832
 *
 */

public abstract class Player {
	
	private String name; //from UserInput

	private int position;
	private int target;  	//position of target. -1 if N/A
	private int status;
	
	/**
	 * Role configuration that is accessed by the town class
	 */
	protected Role role;
	
	private boolean isLynched; 
	
	/**
	 * Constants for status
	 */
	public final int DEAD = 0;
	public final int ALIVE = 1;
	public final int TARGETED = 2;
	public final int HEALED = 3;
	public final int PROTECTED = 4;
	public final int STOPPED = 5;
	
	/**
	 * on default, protected, or healed
	 */
	public final int ACTIVE = 1 | 2 | 3 | 4;
	
	/**
	 * Initializes default values for Player attributes
	 * @param name
	 * @param position
	 * @param role
	 */
	public Player(String name, int position,  Role role){
		this.name = name;
		this.position = position;
		this.role = role;
		this.target = -1;
		this.status = 0;
		this.isLynched = false;
	}
	
	/**
	 * Creates a clone to prevent privacy leaks
	 * Used for doAction() and mafia players who 
	 * switch roles
	 * @param p
	 */
	public Player(Player p){
		this.name = p.name;
		this.position = p.position;
		this.target = p.target;
		this.status = p.status;
		this.isLynched = p.isLynched;
	}
	
	/**
	 * Requires a copy of a player (the target) as param
	 * Abstract method returns the changed or unchanged
	 * position of the target player
	 * @param p
	 * @return new Status of player
	 */
	public abstract int doAction(Player p);
	
	public void setTarget(int target){
		this.target = target;
	}
	
	public void setLynched(boolean lynched){
		this.isLynched = lynched;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getPosition(){
		return this.position;
	}
	
	public int getTarget(){
		return this.target;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public boolean wasLynched(){
		return this.isLynched;
	}
}
