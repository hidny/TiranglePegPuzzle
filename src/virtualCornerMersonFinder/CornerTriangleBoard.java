package virtualCornerMersonFinder;

import java.util.ArrayList;

import triangleBoard5.TriangleBoard;


//TODO: WARNING: this code is really really easy to mess up
//READ IT THOROUGHLY!

//NOTE: pegs are allowed to pop-into existence when above the layer number

public class CornerTriangleBoard {

	
	public static void main(String args[]) {
		CornerTriangleBoard test1 = new CornerTriangleBoard(3, 20);

		System.out.println(test1);
		ArrayList<String> listOfMoves = test1.getFullMovesExcludingRepeatMoves();
		//ArrayList<String> listOfMoves = test1.getPossibleMovesFromPosition(10);
		
		System.out.println("Number of possible moves:");
		for(int i=0; i<listOfMoves.size(); i++) {
			System.out.println(listOfMoves.get(i));
		}
		
		System.out.println("List of positions after 1st move:");
		for(int i=0; i<listOfMoves.size(); i++) {
			System.out.println(test1.doOneMove(listOfMoves.get(i)));
			System.out.println("----");
		}
	}
	
	private int numLayers = -1;
	

	private boolean cornerTriangle[][];
	
	private int numPiecesLeft;
	
	private int numMovesMade;

	private int maxTolerableNumMoves;
	
	private int numMovesMadeStartingFromInside;
	
	// if there's only 1 peg left within the layers and usedOutsidePegs is true,
	// then it could that the last peg ends in the corner...
	private boolean usedOutsidePegs;

	private String historicMoveList;
	private int internalLastJumpCodeForMultiJumpMoves = -1;
	
	
	//TODO: use (don't just fill up the layers with pegs)
	// The fill up the layers with pegs solution is just a proof-of-concept
		//TODO (nah): lookup numbers could be reflected
		//, long lookupNumber
	
	public static final int EXTRA_LAYERS_ABOVE_LAYERS = 2;
	

	//TODO: for now, just fill the corner with pegs, but try every combo of peg/non-peg later
	public CornerTriangleBoard(int numLayers, int maxTolerableNumMoves) {
		
		this.numLayers = numLayers;
		this.maxTolerableNumMoves = maxTolerableNumMoves;
		
		this.numPiecesLeft = 0;
		
		cornerTriangle = new boolean[this.numLayers + EXTRA_LAYERS_ABOVE_LAYERS][];

		for(int i=0; i<cornerTriangle.length; i++) {
			cornerTriangle[i] = new boolean[i+1];
		}

		for(int i=0; i<cornerTriangle.length; i++) {
			for(int j=0; j<cornerTriangle[i].length; j++) {

				if(i<this.numLayers) {
					cornerTriangle[i][j] = true;
					numPiecesLeft++;
				} else {
					//Always false:
					cornerTriangle[i][j] = false;
				}
			}
			
		}
		
		numMovesMade = 0;
		numMovesMadeStartingFromInside = 0;
		
		historicMoveList ="";
		
	}
	
	public String toString() {
		String ret = "";
		for(int i=0; i<cornerTriangle.length; i++) {
			for(int k=i; k<cornerTriangle.length; k++) {
				ret += " ";
			}
			for(int j=0; j<cornerTriangle[i].length; j++) {
				if(cornerTriangle[i][j]) {
					ret += "G ";
				} else {
					ret += "_ ";
				}
			}
			ret += "\n";
		}

		ret += "Num pieces left: " + numPiecesLeft + "\n";
		ret += "Num moves Made: " + this.numMovesMade + "\n";
		ret += "Max moves could make: " + this.maxTolerableNumMoves + "\n";
		ret += "Num moves Made from inside: " + this.numMovesMadeStartingFromInside + "\n";
		ret += "Used outside pegs: " + this.usedOutsidePegs + "\n";
		ret += "Move list: " + historicMoveList + "\n";
		//TODO
		//ret += "Lookup number: " + this.getLookupNumber() + "\n";
		ret += "\n";

		return ret;
	}
	

	public void removePiece(int code) {
		int i = code / cornerTriangle.length;
		int j = code % cornerTriangle.length;
		
		if(i > this.numLayers) {
			System.out.println("ERROR: trying to remove piece from the out-of-bounds area");
			System.exit(1);
		}
		
		if(j <= i && cornerTriangle[i][j] == true) {
			cornerTriangle[i][j] = false;
		} else {
			if(j <= i && cornerTriangle[i][j] == false) {
				System.err.println("ERROR: trying to remove an illegal piece");
				System.exit(1);
			} else {
				System.err.println("WARNING: piece you're trying to remove is out of bounds");
			}
		}
		
		this.numPiecesLeft--;
		//this.lastLookupNumberResult = -1;
	}
	
	

	public void addPiece(int code) {
		int i = code / cornerTriangle.length;
		int j = code % cornerTriangle.length;
		

		if(i > this.numLayers) {
			System.out.println("ERROR: trying to add piece to the out-of-bounds area");
			System.exit(1);
		}
		
		if(j <= i && cornerTriangle[i][j] == false) {
			cornerTriangle[i][j] = true;
		} else {
			if(j <= i && cornerTriangle[i][j] == true) {
				System.err.println("ERROR: trying to remove an illegal piece");
				System.exit(1);
			} else {
				System.err.println("WARNING: piece you're trying to remove is out of bounds");
			}
		}
		
		this.numPiecesLeft++;
		//this.lastLookupNumberResult = -1;
	}

	
	//TODO: Maybe add getNecessary... but I don't know... do it later!
	
	public ArrayList<String> getFullMovesExcludingRepeatMoves() {

		//TODO: Maybe this logic shouldn't be in CornerTriangleBoard except to error out?
		//Even that is a little suspicious
		if(this.numMovesMade == this.maxTolerableNumMoves) {
			System.out.println("ERROR: trying to move after it's been deemed not worth it!");
			System.exit(1);
		}
		//END TODO
		
		boolean goodStarts[][] = new boolean[this.cornerTriangle.length][];

		//A good starting point is within the corner or wherever outside the corner:
		for(int i=0; i<goodStarts.length; i++) {
			goodStarts[i] = new boolean[i + 1];
			
			for(int j=0; j<=i; j++) {
				if(i < numLayers) {
					goodStarts[i][j] = this.cornerTriangle[i][j];
				
				} else {
					goodStarts[i][j] = true;
				}
			}

		}
		
		int lastPegLocation;
		int lastPegLocationi;
		int lastPegLocationj;
		try {
			lastPegLocation = Integer.parseInt(this.historicMoveList.substring(this.historicMoveList.lastIndexOf("-") + 1));
			lastPegLocationi = lastPegLocation / cornerTriangle.length;
			lastPegLocationj = lastPegLocation % cornerTriangle.length;

			//TODO: should these values be the same?
			if(internalLastJumpCodeForMultiJumpMoves != lastPegLocation) {
				System.out.println("ERROR: didn't get same results when comparing internalLastJumpCodeForMultiJumpMoves and lastPegLocation");
				System.exit(1);
			}
		} catch(Exception e) {
			lastPegLocationi = -1;
			lastPegLocationj = -1;
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for(int i=0; i<cornerTriangle.length; i++) {
			for(int j=0; j<cornerTriangle[i].length; j++) {
				if(goodStarts[i][j] && (i != lastPegLocationi || j != lastPegLocationj) ) {
					ret.addAll(getPossibleMovesFromPosition(getCode(i, j)));
				}
			}
		}
		
		return ret;
		
	}
	

	private ArrayList<String> getPossibleMovesFromPosition(int code) {
		int istart = code / cornerTriangle.length;
		int jstart = code % cornerTriangle.length;
		
		ArrayList<String> ret = new ArrayList<String>();
		
		//Only deal with moves that influence pegs within the layers:
		//UP:
		if(istart-2 < this.numLayers) {
			//Only make moves involving the main layers
			if(istart >= 2 && jstart <= istart-2) {
				if( (cornerTriangle[istart-1][jstart] || istart-1 >= this.numLayers) && cornerTriangle[istart-2][jstart] == false) {
				
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart), isFirstJump(code)) );
				}
			}
		
			//UP LEFT
			if(istart >=2 && jstart >= 2) {
				if((cornerTriangle[istart-1][jstart-1] || istart-1 >= this.numLayers) && cornerTriangle[istart-2][jstart-2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart-2), isFirstJump(code)) );
				}
			}
		}
		
		if(istart < this.numLayers) {
			//Don't make moves that stay below the main layers:
			//RIGHT:
			if(jstart + 2 < cornerTriangle[istart].length) {
				if(cornerTriangle[istart][jstart+1] && cornerTriangle[istart][jstart+2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart+2), isFirstJump(code)) );
				}
			}
			
			//LEFT:
			if(jstart >=2) {
				if(cornerTriangle[istart][jstart-1] && cornerTriangle[istart][jstart-2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart-2), isFirstJump(code)) );
				}
			}
			
			//DOWN:
			if(istart + 2 < cornerTriangle.length) {
				if(istart + 1 >= this.numLayers ||
						(cornerTriangle[istart+1][jstart] && cornerTriangle[istart+2][jstart] == false)) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart), isFirstJump(code)) );
				}
			}
			
			//DOWN RIGHT
			if(istart + 2 < cornerTriangle.length) {
				if(istart + 1 >= this.numLayers ||
						cornerTriangle[istart+1][jstart+1]  && cornerTriangle[istart+2][jstart+2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart+2), isFirstJump(code)) );
				}
			}
		}
		
		return ret;
	}
	
	private boolean isFirstJump(int code) {
		
		//TODO SANITY TEST (Delete same code in main solve if sanity test passes)
		int lastPegLocation;
		try {
			lastPegLocation = Integer.parseInt(this.historicMoveList.substring(this.historicMoveList.lastIndexOf("-") + 1));


			//TODO: should these values be the same?
			if(internalLastJumpCodeForMultiJumpMoves != lastPegLocation) {
				System.out.println("ERROR: didn't get same results when comparing internalLastJumpCodeForMultiJumpMoves and lastPegLocation");
				System.exit(1);
			}
		} catch(Exception e) {
		}
		//END SANITY TEST

		
		return internalLastJumpCodeForMultiJumpMoves != code;
	}
	
	
	public static final String SPACE_BETWEEN_MOVES = "  "; 

	private ArrayList<String> getPossibleMovesAfterJump(String jump, boolean isFirstJump) {


		int from = Integer.parseInt(jump.split("-")[0]);
		int to = Integer.parseInt(jump.split("-")[1]);
		
		//Check if we aren't jumping over a peg that was already jumped over:
		//Basically: pegs captured twice on same move shouldn't be allowed:
		// example shouldn't be allowed: 10-20-10
		if(isFirstJump == false) {
			String prevMoves[] = this.historicMoveList.split(SPACE_BETWEEN_MOVES)[this.historicMoveList.split(SPACE_BETWEEN_MOVES).length - 1].split("-");
			//TODO test
			
			
			int fromI = from / cornerTriangle.length;
			int fromJ = from % cornerTriangle.length;
			
			int toI = to / cornerTriangle.length;
			int toJ = to % cornerTriangle.length;
			
			int jumpedPegI = (fromI + toI)/2;
			int jumpedPegJ = (fromJ + toJ)/2;
			
			if(fromI < this.numLayers || toI < this.numLayers) {
				//if the jump has a peg within the layers:
				
				for(int i=1; i<prevMoves.length; i++) {
	
					int fromPrev = Integer.parseInt(prevMoves[i - 1]);
					int toPrev = Integer.parseInt(prevMoves[i]);
					
					int fromPrevI = fromPrev / cornerTriangle.length;
					int fromPrevJ = fromPrev % cornerTriangle.length;
					
					int toPrevI = toPrev / cornerTriangle.length;
					int toPrevJ = toPrev % cornerTriangle.length;
					
					int jumpedPrevPegI = (fromPrevI + toPrevI)/2;
					int jumpedPrevPegJ = (fromPrevJ + toPrevJ)/2;
					
					if(fromPrevI < this.numLayers || toPrevI < this.numLayers) {
						if(jumpedPrevPegI == jumpedPegI && jumpedPrevPegJ ==jumpedPegJ) {
							return new ArrayList<String>();
						}
						
					}
				}
			}
			
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		

		String curJumpDescription = jump.split("-")[0];
	
		//TODO: put this condition in a function
		if(internalLastJumpCodeForMultiJumpMoves != -1 &&
				internalLastJumpCodeForMultiJumpMoves != from) {
			
			//if peg is re-entering layers from another outside location, note it down

			ret.add(internalLastJumpCodeForMultiJumpMoves + "-" + jump);
			curJumpDescription = internalLastJumpCodeForMultiJumpMoves + "-" + jump.split("-")[0];

		} else {
			//If peg is not re-entering layers from another outside location, 
			// just add the jump as is and only add the from location to the beginning of multi-jump moves. 
			ret.add(jump);
			curJumpDescription = jump.split("-")[0];
		}
		
		CornerTriangleBoard tmp = this.moveInternal(jump);
		
		int landing = Integer.parseInt(jump.split("-")[1]);
		int landingI = landing / cornerTriangle.length;
		int landingJ = landing % cornerTriangle.length;
		
		if(landingI < numLayers) {

			ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(landing);
			for(int i=0; i<newSeriesOfMoves.size(); i++) {
				ret.add(curJumpDescription + "-" + newSeriesOfMoves.get(i));
			}

		} else {

			//IDEA: landing should be able to go to any space outside the layers
			// as long as the position class is the same
			
			int landingPositionClass = getPositionClass(landingI, landingJ);
			
			for(int i=this.numLayers; i<this.cornerTriangle.length; i++) {
				for(int j=0; j<=i; j++) {
					
					if(getPositionClass(i, j) == landingPositionClass) {
						
						//SANITY CHECK
						if(i != landingI) {
							//I think i needs to be equal to landingI, so I added this sanity check just in case:
							System.out.println("ERROR: something is wrong in getPossibleMovesAfterJump (corner Triangle)");
							System.exit(1);
						}
						//END SANITY CHECK
						
						ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(getCode(i, j));
						for(int i2=0; i2<newSeriesOfMoves.size(); i2++) {
							ret.add(curJumpDescription + "-" + newSeriesOfMoves.get(i2));
						}
					}
				}
			}
			
		}
		
		return ret;
	}


	public int getCode(int i, int j) {
		return i*cornerTriangle.length + j;
	}
	
	
	private int getPositionClass(int code) {

		int i = code / cornerTriangle.length;
		int j = code % cornerTriangle.length;
		
		return 2*(i%2) + j%2;
	}
	
	public static int getPositionClass(int i, int j) {
		return 2*(i%2) + j%2;
	}
	

	//pre: valid move
	private CornerTriangleBoard moveInternal(String move) {
		String fromTo[] = move.split("-");
		
		int from = Integer.parseInt(fromTo[0]);
		int to = Integer.parseInt(fromTo[1]);
		
		int fromI = from / cornerTriangle.length;
		int fromJ = from % cornerTriangle.length;
		
		int toI = to / cornerTriangle.length;
		int toJ = to % cornerTriangle.length;
		
		CornerTriangleBoard newBoard = new CornerTriangleBoard(this.numLayers, this.maxTolerableNumMoves);
		
		for(int i=0; i<cornerTriangle.length; i++) {
			for(int j=0; j<cornerTriangle[i].length; j++) {
				newBoard.cornerTriangle[i][j] = cornerTriangle[i][j];
			}
		}
		
		//System.out.println(fromI + "," + fromJ);

		if(fromI < this.numLayers && newBoard.cornerTriangle[fromI][fromJ] == false) {
			System.out.println("ERROR move 1");
		}
		
		if((fromI+toI)/2 < this.numLayers && newBoard.cornerTriangle[(fromI+toI)/2][(fromJ+toJ)/2] == false) {
			System.out.println("ERROR move 2");
		}
		
		if(newBoard.cornerTriangle[toI][toJ] == true) {
			System.out.println("ERROR move 3");
		}

		newBoard.numPiecesLeft = this.numPiecesLeft;
		
		if(newBoard.cornerTriangle[fromI][fromJ]) {
			newBoard.cornerTriangle[fromI][fromJ] = false;
			newBoard.numPiecesLeft--;
		}
		
		if(newBoard.cornerTriangle[(fromI+toI)/2][(fromJ+toJ)/2]) {
			newBoard.cornerTriangle[(fromI+toI)/2][(fromJ+toJ)/2] = false;
			newBoard.numPiecesLeft--;
		}		
		
		if(toI < this.numLayers) {
			newBoard.cornerTriangle[toI][toJ] = true;
			newBoard.numPiecesLeft++;
		}
		
		newBoard.historicMoveList = this.historicMoveList;
		
		if(internalLastJumpCodeForMultiJumpMoves == from
			|| isMultiJumpFromOutsideTheLayers(from)) {

			//not a new move
			newBoard.numMovesMade = this.numMovesMade;
			
			if(internalLastJumpCodeForMultiJumpMoves != -1 &&
					internalLastJumpCodeForMultiJumpMoves != from) {

				newBoard.historicMoveList += "-" + from + "-" + to;

			} else {
				newBoard.historicMoveList += "-" + to;
			}
			
		} else {
			newBoard.numMovesMade = this.numMovesMade + 1;
			
			if(fromI < this.numLayers) {
				this.numMovesMadeStartingFromInside++;
			} else {
				this.usedOutsidePegs = true;
			}
			
			newBoard.historicMoveList += SPACE_BETWEEN_MOVES + move;
			
		}
		newBoard.internalLastJumpCodeForMultiJumpMoves = to;
		
		return newBoard;
	}
	
	private boolean isMultiJumpFromOutsideTheLayers(int from) {
		return internalLastJumpCodeForMultiJumpMoves >= this.numLayers && from >= this.numLayers
				&& getPositionClass(from) == getPositionClass(internalLastJumpCodeForMultiJumpMoves);
	}
	
	public CornerTriangleBoard doOneMove(String move) {
		String seriesOfJumps[] = move.split("-");
		
		CornerTriangleBoard newBoard = this;
		
		for(int i=0; i<seriesOfJumps.length - 1; i++) {
			
			int from = Integer.parseInt(seriesOfJumps[i]);
			int to = Integer.parseInt(seriesOfJumps[i+1]);
			
			int fromI = from / this.cornerTriangle.length;
			int toI = to / this.cornerTriangle.length;
			
			if(fromI < this.numLayers || toI < this.numLayers) {
				newBoard = newBoard.moveInternal(from + "-" + to);
			}
		}
		
		if(newBoard == this) {
			System.out.println("ERROR blank move!");
			System.exit(1);
		}

		//newBoard.prevLocation = this;
		
		return newBoard;
		
	}

}
