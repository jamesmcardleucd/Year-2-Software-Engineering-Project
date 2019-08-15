public class JAR implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    private static final int INNER_END = 6;     //Index for the end of the inner board.
    private static final int OUTER_END = 18;    //Index for the end of the home board.
    private static final int OFFER_DOUBLE_DIFFERENCE = 6;
    
    private final int SIX = 6;
    private final int EIGHTEEN = 18;
    private final int BAR_PIP = 25; 
    private final int HOME_PIP = 0;

    JAR(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
    }

    public String getName() {
        return "JAR"; // must match the class name
    }

    public String getCommand(Plays possiblePlays) {
    	int [][] possibleState= board.get();
    	int playScores;
    	int winningScore = 0;
    	int [] moves;
    	int posWeight = 1, negWeight = -1;
    	int [] pipCountDifference = calculatePipCountDifference();
    	int moveIndex = 0;
    	int winningIndex = 0;
    	
    	/*
    	 * ArrayList loop to iterate through all the possible moves and calculate their winning potential.
    	 */
         for (Play play : possiblePlays) {
        	 playScores = 0;
        	//First, make the play on the currentState of the board to make it the possibleState.
        	 moves = new int[play.toString().length()];
        	 moves = convertMoveToIntArray(play.toString());
        	 for(int i = 0;i < moves.length/2;i=i+2) {
        		 possibleState[me.getId()][moves[i]]--;
        		 possibleState[me.getId()][moves[i+1]]++;
        	 }
        	 //Secondly, we need to check all of our features against this possibleState of the board.
        	 int [] weights = calculateWeights(pipCountDifference);
        	 posWeight = weights[0];
        	 negWeight = weights[1];
        	 
        	 
        	 for (int i = 0; i < 2; i++) {
        		 if(getDoubleDecision() == "Offer Double") {
        		 
        		 	return "double";
        	 	}
        	 }
        	 
        	 /*
        	  * Feature: Prime
        	  * Moving a group of 4/5/6(ideally) checkers across the board as a unit
        	  * in order to block your opponents checkers from advancing.
        	  * Score: 5
        	  * Weight: The more checkers you are blocking the higher the weight.
        	  */
        	// if(board.getNumCheckers(me.getId(), ))
        	 
        	 
        	 /*
        	  * Feature: Blitz
        	  * Knocking out an opponents checker when you have the chance.
        	  * This will especially have a varying weight.
        	  * Score: 3
        	  * Weight: Higher weight the closer your opponents checkers are to your homeboard/bearing off.
        	  */
        	 //If we are in a relatively strong position, prioritize knocking out checkers.
        	 if(calculateNumberOfCheckersInRange(opponent.getId(), 1, 6) > 3) {
        		 if(play.toString().contains("*")) {
        			 playScores += 13;
        		 }
        	 }
        	 
        	 
        	
        	 /*
        	  * Feature: Anchoring
        	  * Getting two of your checkers on a pip in your opponents homeboard to make it
        	  * more difficult to bear off.
        	  * Score: 5
        	  * Weight: Shouldn't really vary but could change.
        	  */
        	 //Opponents Home Board
        	 
        	 if(pipCountDifference[me.getId()] - pipCountDifference[opponent.getId()] >= 7) {
        		 for(int pip = 1;pip < 7;pip++) {
        			 if(board.getNumCheckers(me.getId(), pip) <= 1 && possibleState[me.getId()][pip] >= 2){
        				 playScores += 3;
        			 } 
        		 }
        	 }
        	 else {
        		 for(int pip = 1;pip < 7;pip++) {
        			 if(board.getNumCheckers(me.getId(), pip) <= 1 && possibleState[me.getId()][pip] >= 2){
        				 playScores += 2;
        			 } 
        		 }
        	 }
        	 
        	 /*
        	  * Feature: Race to the End
        	  * Prioritize just getting to the end as fast as possible.
        	  * Score: 5
        	  * Weight: Highest when none of the opponents checkers are in front of any of your checkers.
        	  */
        	 
        	 	/*if(pipCountDifference[me.getId()] - pipCountDifference[opponent.getId()] < 10 ||
        	 	   board.) {        		 
        	 		playScores += 15;
        	 	}*/
        	 if(board.lastCheckerInInnerBoard(me.getId())) {
            	 for(int i = 0;i < moves.length/2;i=i+2) {
            		 if(moves[i+1] == HOME_PIP) {
            			 playScores += (20*posWeight);
            		 }
            	 }
        	 }
        	 	for(int i = 0;i < moves.length/2;i=i+2) {
        	 	if(moves[i] >= 24) {
        	 		playScores += 15 * posWeight ;
				}
				
        	 	else if(moves[i] >= EIGHTEEN)
				{
					playScores += 12 * posWeight;
				}
				
        	 	else if(moves[i] <= EIGHTEEN && moves[i] > SIX)
				{
					playScores += 10 *posWeight;
				}
				
        	 	else if(moves[i]<= SIX )
				{
					playScores += 8 * posWeight;
				}
				
        	
        	 	}
        	 //}
        	 
        	 /*
        	  * Feature: Block-Blot
        	  * Ensure none of your checkers are left alone and prioritize making the blocks.
        	  * Score: 5
        	  * Weight: Lower weigh the less opponent checkers are in front of you.
        	  */
        	 
        	 //If the move makes a blot go to a block.
        	 for(int i = 0;i < moves.length/2;i=i+2) {
        		 possibleState[me.getId()][moves[i]]--;
        		 possibleState[me.getId()][moves[i+1]]++;
        		 if(board.getNumCheckers(me.getId(), moves[i+1]) == 1 && possibleState[me.getId()][moves[i+1]] >= 2) {
        			 playScores += 10;
        	 	}
        		 if(board.getNumCheckers(me.getId(), moves[i]) == 2 && possibleState[me.getId()][moves[i]] == 1) {
        			 playScores -= 8;
        		 }
        	 }
        	       	 
        	 
        	 /*
        	  * Thirdly, we need to compare the score for this move with the highest score we've gotten so far,
        	  * and if it's bigger, change the winningIndex to the moveIndex.
        	  */
        	 
        	
        	 if(playScores > winningScore) {
        		 winningIndex = moveIndex;
        		 winningScore = playScores;
        	 }
        	 moveIndex++;
         }
         
         /*
          * Calculate the index of the highest element in playScore.
          */
         
         if (shouldIDouble() && canIDouble()) {
        	 
        	 return offerDouble();
        	 
         } else {
        	 
        	 return "" + (winningIndex+1);
        	 
         }
    }
    
    public String offerDouble()
	{
		return "double";
		
	}
    
    public String decision() {
	    if(shouldIDouble() && canIDouble())
		{
			return offerDouble();
		}
	    else {
	    	return "y";
	    }
		
	}
	
	public boolean shouldIDouble() {
		
		int oppPos = 0;
		int myPos = 0;
		int oppMaxPip = 100;
		int myMaxPip = 100;
		boolean oppFinalQuad = false;
		boolean myFinalQuad = false;
		
		for(int i = 0; i <= 24; i++)
		{
			if(board.getNumCheckers(opponent.getId(), i) > 0)
			{
				oppPos += i;                                         
				
				if(i < oppMaxPip)
				{
					oppMaxPip = i;
				}
				
			}
			
			if(board.getNumCheckers(me.getId(), i) > 0)
			{
				myPos += i;
				
				if(i < myMaxPip)
				{
					myMaxPip = i;
				}
				
			}
		}
		
		if(oppMaxPip > 17)
		{
			oppFinalQuad = true;
		}
		if(myMaxPip > 17)
		{
			myFinalQuad = true;
		}
		
		if(myFinalQuad && !oppFinalQuad)
		{
			return true;
		}
		
		if(myPos - oppPos > OFFER_DOUBLE_DIFFERENCE)
		{
			return true;
		}
		if(board.getNumCheckers(me.getId(), 0) > 13 && board.getNumCheckers(opponent.getId(), 0) < 12) 
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	public boolean canIDouble() {
		
		if(cube.isOwned())
		{
			return true;
		}
		
		else return false;
	}
	
	public String getDoubleDecision() {
		
		if (shouldIDouble()) {
			
			return "double";
			
		} else {
			
			return "y";
			
		}
		
		
	}
    
    /*
     * Function to convert the move from a string to an integer array.
     * Input: "24-18 24-22"  
     * Output: {24,18,24,22}
     */
	private int[] convertMoveToIntArray(String input) {
		int [] moves;
		//Use input as key to find value in map
		
		String chosenMove = input;
		String [] parts = chosenMove.split("[- *]+");
		for(int i = 0; i < parts.length; i++) {
			if(parts[i].equals("Bar")) 
				parts[i] = "25"; 
			else if(parts[i].equals("Off"))
				parts[i] = "0";
		}
		moves = new int[parts.length];
		//Split the move up into its values as Strings, then parse them to integers..
		for(int i = 0; i < parts.length;i++) {
			moves[i] = Integer.parseInt(parts[i]);
		}
		
		return moves;
	}
	
	private int[] calculatePipCountDifference() {
		int [] pipCount = {0,0};
		for(int player = 0;player < 2;player++) {
			for(int pip = 25;pip > 0;pip--) {
				if(board.getNumCheckers(player, pip) > 0)
					pipCount[player] += (pip * board.getNumCheckers(player, pip));
			}
		}
		
		return pipCount;
	}
	
	private int[] calculateWeights(int [] pipCount) {
		int [] weights = {1, -1}; //Index 0: positive weight, Index 1: negative weight.
		/*
		 * Important factors for calculating weight:
		 * 		Pip Count Difference
		 * 		Number of checkers in the home board for you and opponent.
		 * 		Number of checkers in the outer board for you and opponent.
		 */
		
		//If most of your opponents checkers are in the home board, prioritize getting to the home board.
		if((!board.lastCheckerInInnerBoard(opponent.getId()) && !board.lastCheckerInOpponentsInnerBoard(opponent.getId())) || board.lastCheckerInInnerBoard(opponent.getId()))
		{
			weights[0] = 2;
		}
		else if(board.lastCheckerInInnerBoard(me.getId())) {
			weights[0] = 8;
		}
		
		
		
		return weights;
	}
	
		private int calculateNumberOfCheckersInRange(int playr, int startPip, int endPip) {
		int numOfCheckers = 0;
		for(int pip = startPip;pip <= endPip;pip++) {
			numOfCheckers += board.getNumCheckers(playr, pip);
		}
		return numOfCheckers;
	}
}