package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.Scanner;


//WARNING: this code is really really easy to mess up
//READ IT THOROUGHLY!

//NOTE: pegs are allowed to pop-into existence when above the layer number

public class CornerTriangleBoard {

	
	public static void main(String args[]) {
		CornerTriangleBoard test1 = new CornerTriangleBoard(3);

		System.out.println(test1);
		ArrayList<String> listOfMoves = test1.getFullMovesExcludingRepeatMoves();
		//ArrayList<String> listOfMoves = test1.getPossibleMovesFromPosition(10);
		
		System.out.println("Number of possible moves: " + listOfMoves.size());
		System.out.println("list:");
		for(int i=0; i<listOfMoves.size(); i++) {
			System.out.println(listOfMoves.get(i));
		}
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("List of positions after 1st move:");
		for(int i=0; i<listOfMoves.size(); i++) {
			
			CornerTriangleBoard tmp = test1.doOneMove(listOfMoves.get(i));
			System.out.println(tmp);

			System.out.println("----");
			ArrayList<String> listOfMoves2 = tmp.getFullMovesExcludingRepeatMoves();
			
			System.out.println("Number of possible moves for second move: " + listOfMoves2.size());
			System.out.println("list:");
			for(int i2=0; i2<listOfMoves2.size(); i2++) {
				System.out.println(listOfMoves2.get(i2));
			}
			
			for(int i2=0; i2<listOfMoves2.size(); i2++) {
				
				System.out.println("Second move:");
				System.out.println("move: " + listOfMoves2.get(i2) ) ;
				CornerTriangleBoard tmp2 = tmp.doOneMove(listOfMoves2.get(i2));
				System.out.println(tmp2);
				System.out.println("----");
				
			}
			System.out.println("----");
			System.out.println("----");
			System.out.println("----");
			
			System.out.println("Press enter to get to the next move");
			//in.nextLine();
		}
	}
	
	private int numLayers = -1;
	
	private boolean cornerTriangle[][];
	
	private int numPiecesLeft;
	
	private int numMovesMade;


	private int numMovesMadeStartingFromInside;
	
	// if there's only 1 peg left within the layers and usedOutsidePegs is true,
	// then it could that the last peg ends in the corner...
	private boolean usedOutsidePegs;

	private String historicMoveList;
	private int internalLastJumpCodeForMultiJumpMoves = -1;

	
	public int getNumLayers() {
		return numLayers;
	}

	//WARNING: DO NOT MODIFY OUTSIDE THIS CLASS
	public boolean[][] getCornerTriangle() {
		return cornerTriangle;
	}
	//END WARNING

	public int getNumPiecesLeft() {
		return numPiecesLeft;
	}

	public int getNumMovesMade() {
		return numMovesMade;
	}

	public int getNumMovesMadeStartingFromInside() {
		return numMovesMadeStartingFromInside;
	}

	public boolean isUsedOutsidePegs() {
		return usedOutsidePegs;
	}

	public String getHistoricMoveList() {
		return historicMoveList;
	}
	
	
	
	public static final String SPACE_BETWEEN_MOVES = "  "; 
	
	//Will use and not fill 2 extra layers beyond the number of layers set

	// The fill up the layers with pegs solution is just a proof-of-concept
		//TODO (nah): lookup numbers could be reflected
		//, long lookupNumber
	
	public static final int EXTRA_LAYERS_ABOVE_LAYERS = 2;
	

	//TODO: for now, just fill the corner with pegs, but try every combo of peg/non-peg later
	public CornerTriangleBoard(int numLayers) {
		
		this.numLayers = numLayers;
		
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
		ret += "Num moves Made from inside: " + this.numMovesMadeStartingFromInside + "\n";
		ret += "Used outside pegs: " + this.usedOutsidePegs + "\n";
		ret += "Move list: " + historicMoveList + "\n";
		ret += "Lookup number: " + this.getLookupNumber() + "\n";
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
		
		int lastPegLocationi;
		int lastPegLocationj;
		try {
			lastPegLocationi = internalLastJumpCodeForMultiJumpMoves / cornerTriangle.length;
			lastPegLocationj = internalLastJumpCodeForMultiJumpMoves % cornerTriangle.length;

		} catch(Exception e) {
			lastPegLocationi = -1;
			lastPegLocationj = -1;
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for(int i=0; i<cornerTriangle.length; i++) {
			for(int j=0; j<cornerTriangle[i].length; j++) {
				if(goodStarts[i][j] && (i != lastPegLocationi || j != lastPegLocationj) ) {
					
					ret.addAll(getPossibleMovesFromPosition(getCode(i, j), true));
				}
			}
		}
		
		return ret;
		
	}
	

	private ArrayList<String> getPossibleMovesFromPosition(int code, boolean isFirstJump) {
		int istart = code / cornerTriangle.length;
		int jstart = code % cornerTriangle.length;
		
		ArrayList<String> ret = new ArrayList<String>();
		
		//Only deal with moves that influence pegs within the layers:
		//UP:
		if(istart-2 < this.numLayers) {
			//Only make moves involving the main layers
			if(istart >= 2 && jstart <= istart-2) {
				if( (cornerTriangle[istart-1][jstart] || istart-1 >= this.numLayers) && cornerTriangle[istart-2][jstart] == false) {
				
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart), isFirstJump) );
				}
			}
		
			//UP LEFT
			if(istart >=2 && jstart >= 2) {
				if((cornerTriangle[istart-1][jstart-1] || istart-1 >= this.numLayers) && cornerTriangle[istart-2][jstart-2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart-2), isFirstJump) );
				}
			}
		}
		
		if(istart < this.numLayers) {
			//Don't make moves that stay below the main layers:
			//RIGHT:
			if(jstart + 2 < cornerTriangle[istart].length) {
				if(cornerTriangle[istart][jstart+1] && cornerTriangle[istart][jstart+2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart+2), isFirstJump) );
				}
			}
			
			//LEFT:
			if(jstart >=2) {
				if(cornerTriangle[istart][jstart-1] && cornerTriangle[istart][jstart-2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart-2), isFirstJump) );
				}
			}
			
			//DOWN:
			if(istart + 2 < cornerTriangle.length) {
				if(istart + 1 >= this.numLayers ||
						(cornerTriangle[istart+1][jstart] && cornerTriangle[istart+2][jstart] == false)) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart), isFirstJump) );
				}
			}
			
			//DOWN RIGHT
			if(istart + 2 < cornerTriangle.length) {
				if(istart + 1 >= this.numLayers ||
						cornerTriangle[istart+1][jstart+1]  && cornerTriangle[istart+2][jstart+2] == false) {
					ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart+2), isFirstJump) );
				}
			}
		}
		
		return ret;
	}

	private ArrayList<String> getPossibleMovesAfterJump(String jump, boolean isFirstJump) {


		int from = Integer.parseInt(jump.split("-")[0]);
		int to = Integer.parseInt(jump.split("-")[1]);
		
		//Check if we aren't jumping over a peg that was already jumped over:
		//Basically: pegs captured twice on same move shouldn't be allowed:
		// example shouldn't be allowed: 10-20-10
		if(isFirstJump == false) {
			String prevMoves[] = this.historicMoveList.split(SPACE_BETWEEN_MOVES)[this.historicMoveList.split(SPACE_BETWEEN_MOVES).length - 1].split("-");
			
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
	
		if(isFirstJump == false &&
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
		
		CornerTriangleBoard tmp = this.moveInternal(jump,isFirstJump);
		
		int landing = Integer.parseInt(jump.split("-")[1]);
		int landingI = landing / cornerTriangle.length;
		int landingJ = landing % cornerTriangle.length;
		
		if(landingI < numLayers) {

			ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(landing, false);
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
							//I think it needs to be equal to landingI, so I added this sanity check just in case:
							System.out.println("ERROR: something is wrong in getPossibleMovesAfterJump (corner Triangle)");
							System.exit(1);
						}
						//END SANITY CHECK
						
						ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(getCode(i, j), false);
						for(int i2=0; i2<newSeriesOfMoves.size(); i2++) {
							ret.add(curJumpDescription + "-" + newSeriesOfMoves.get(i2));
						}
					}
				}
			}
			
		}
		
		return ret;
	}


	private int getCode(int i, int j) {
		return i*cornerTriangle.length + j;
	}
	
	private static int getPositionClass(int i, int j) {
		return 2*(i%2) + j%2;
	}
	

	//pre: valid move
	private CornerTriangleBoard moveInternal(String move, boolean isFirstJump) {
		String fromTo[] = move.split("-");
		
		int from = Integer.parseInt(fromTo[0]);
		int to = Integer.parseInt(fromTo[1]);
		
		int fromI = from / cornerTriangle.length;
		int fromJ = from % cornerTriangle.length;
		
		int toI = to / cornerTriangle.length;
		int toJ = to % cornerTriangle.length;
		
		CornerTriangleBoard newBoard = new CornerTriangleBoard(this.numLayers);
		
		for(int i=0; i<cornerTriangle.length; i++) {
			for(int j=0; j<cornerTriangle[i].length; j++) {
				newBoard.cornerTriangle[i][j] = cornerTriangle[i][j];
			}
		}
		
		//System.out.println(fromI + "," + fromJ);

		if(fromI < this.numLayers && newBoard.cornerTriangle[fromI][fromJ] == false) {
			System.out.println("ERROR corner board move 1");
			System.exit(1);
		}
		
		if((fromI+toI)/2 < this.numLayers && newBoard.cornerTriangle[(fromI+toI)/2][(fromJ+toJ)/2] == false) {
			System.out.println("ERROR corner board move 2");
			System.exit(1);
		}
		
		if(newBoard.cornerTriangle[toI][toJ] == true) {
			System.out.println("ERROR corner board move 3");
			System.exit(1);
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
		newBoard.numMovesMade = this.numMovesMade;
		newBoard.numMovesMadeStartingFromInside = this.numMovesMadeStartingFromInside;
		
		if(isFirstJump) {
			
			newBoard.numMovesMade++;
			
			if(fromI < this.numLayers) {
				newBoard.numMovesMadeStartingFromInside++;
			} else {
				newBoard.usedOutsidePegs = true;
			}
			
			newBoard.historicMoveList += SPACE_BETWEEN_MOVES + move;
		} else {

			//not a new move
			
			if(internalLastJumpCodeForMultiJumpMoves != from) {

				newBoard.historicMoveList += "-" + from + "-" + to;

			} else {
				newBoard.historicMoveList += "-" + to;
			}
			
		}

		newBoard.internalLastJumpCodeForMultiJumpMoves = to;
		
		return newBoard;
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
				if(i == 0) {
					newBoard = newBoard.moveInternal(from + "-" + to, true);
				} else {
					newBoard = newBoard.moveInternal(from + "-" + to, false);
					
				}
				
				//TODO: debug numMovesMadeStartingFromInside!
				if(newBoard.numMovesMadeStartingFromInside > 0) {
					//System.out.println("hello");
					//System.out.println(newBoard);
				}
			} else if(i==0) {
				System.out.println("ERROR: first move completely outside the layers in CornerTriangleBoard doOneMove!");
				System.exit(1);
			}

			if(fromI >= this.numLayers) {
				newBoard.usedOutsidePegs = true;
			}
		}
		
		if(newBoard == this) {
			System.out.println("ERROR blank move!");
			System.exit(1);
		}

		//newBoard.prevLocation = this;
		
		return newBoard;
		
	}
	
	public long getLookupNumber() {
		return CornerTriangleLookup.convertToNumberSimple(cornerTriangle);
	}

}
