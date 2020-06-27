package triangleBoard5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import triangleBoardInterface.TriangleBoardI;

//Hard limit on number of records:
//20344823
public class TriangleBoard implements TriangleBoardI {
	//Only hard-copies allow
	
	public static void main(String args[]) {
		//TESTING code:
		TriangleBoard board = new TriangleBoard(4);

		board.removePiece(0);
		board.removePiece(13);
		board.removePiece(15);
		board.draw();
		
		
		ArrayList<String> moves = board.getFullMovesExcludingRepeatMoves();
		for(int i=0; i<moves.size(); i++) {
			System.out.println(moves.get(i));
		}
		
		board = new TriangleBoard(4);
		board.removePiece(0);
		board.removePiece(14);
		board.removePiece(15);
		board.draw();
		
		moves = board.getFullMovesExcludingRepeatMoves();
		for(int i=0; i<moves.size(); i++) {
			System.out.println(moves.get(i));
		}
		
		board = new TriangleBoard(4);
		board.removePiece(0);
		board.removePiece(12);
		board.removePiece(15);
		board.draw();
		

		System.out.println("***");
		System.out.println("***");
		System.out.println("TESTING AFTER 1 move:");
		moves = board.getFullMovesExcludingRepeatMoves();
		for(int i=0; i<moves.size(); i++) {
			System.out.println(moves.get(i));
		}
		
		for(int i=0; i<moves.size(); i++) {
			TriangleBoard tmp = board.doOneMove(moves.get(i));
			tmp.draw();
		}

		System.out.println("***");
		System.out.println("***");
		System.out.println("Try a double loop:");
		for(int i=0; i<moves.size(); i++) {
			TriangleBoard tmp = board.doOneMove(moves.get(i));
			tmp.draw();
			
			ArrayList <String> moves2 = tmp.getFullMovesExcludingRepeatMoves();

			for(int j=0; j<moves2.size(); j++) {
				TriangleBoard tmp2 = tmp.doOneMove(moves2.get(j));
				tmp2.draw();
				
				System.out.println("Lookup number:");
				System.out.println(tmp2.getLookupNumber());
			}
			
			
		}
		
		
		board = new TriangleBoard(4);
		board.removePiece(0);
		board.removePiece(8);
		board.removePiece(15);
		
		moves = board.getFullMovesExcludingRepeatMoves();

		System.out.println("***");
		System.out.println("***");
		System.out.println("Try a double loop again:");
		for(int i=0; i<moves.size(); i++) {
			TriangleBoard tmp = board.doOneMove(moves.get(i));
			tmp.draw();
			
			ArrayList <String> moves2 = tmp.getFullMovesExcludingRepeatMoves();

			for(int j=0; j<moves2.size(); j++) {
				TriangleBoard tmp2 = tmp.doOneMove(moves2.get(j));
				tmp2.draw();
			}
			
		}
		//END TESTING CODE
		
		TriangleBoard board9 = new TriangleBoard(9);
		board9.removePiece(9);
		String moves2[] = "27-9 0-18 29-9-27-29 45-27".split(" ");
		
		for(int i=0; i<moves2.length; i++) {
			if(moves2[i].trim().equals("") == false) {
				board9 = board9.doOneMove(moves2[i]);
			}
		}
		board9.draw();
		
	}
	
	private boolean triangle[][];
	private int numPiecesLeft;
	private int numMovesMade;

	private String historicMoveList;
	private int internalLastJumpCodeForMultiJumpMoves = -1;
	
	public TriangleBoard(int length) {
		triangle = new boolean[length][];
		for(int i=0; i<length; i++) {
			triangle[i] = new boolean[i+1];
		}

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				triangle[i][j] = true;
			}
		}
		
		numMovesMade = 0;
		
		numPiecesLeft = ((triangle.length + 1) * (triangle.length))/2;
		
		historicMoveList ="";
		
	}
	
	//TODO: only for testing
	public boolean[][] getTriangle() {
		return triangle;
	}
	//END TODO

	public void draw() {
		for(int i=0; i<triangle.length; i++) {
			for(int k=i; k<triangle.length; k++) {
				System.out.print(" ");
			}
			for(int j=0; j<triangle[i].length; j++) {
				if(triangle[i][j]) {
					System.out.print("G ");
				} else {
					System.out.print("_ ");
				}
			}
			System.out.println();
		}

		System.out.println("Num pieces left: " + numPiecesLeft);
		System.out.println("Num moves Made: " + numMovesMade);
		System.out.println("Move list: " + historicMoveList);
		System.out.println("Lookup number: " + this.getLookupNumber());
	}
	

	public String toString() {
		String ret = "";
		for(int i=0; i<triangle.length; i++) {
			for(int k=i; k<triangle.length; k++) {
				ret += " ";
			}
			for(int j=0; j<triangle[i].length; j++) {
				if(triangle[i][j]) {
					ret += "G ";
				} else {
					ret += "_ ";
				}
			}
			ret += "\n";
		}

		ret += "Num pieces left: " + numPiecesLeft + "\n";
		ret += "Num moves Made: " + this.numMovesMade + "\n";
		ret += "Move list: " + historicMoveList + "\n";
		ret += "Lookup number: " + this.getLookupNumber() + "\n";
		ret += "\n";

		return ret;
	}
	
	public void removePiece(int code) {
		int i = code / triangle.length;
		int j = code % triangle.length;
		
		if(j <= i && triangle[i][j] == true) {
			triangle[i][j] = false;
		} else {
			if(j <= i && triangle[i][j] == false) {
				System.err.println("ERROR: trying to remove an illegal piece");
				System.exit(1);
			} else {
				System.err.println("WARNING: piece you're trying to remove is out of bounds");
			}
		}
		
		this.numPiecesLeft--;
		this.lastLookupNumberResult = -1;
	}
	
	public void addPiece(int code) {
		int i = code / triangle.length;
		int j = code % triangle.length;
		
		if(j <= i && triangle[i][j] == false) {
			triangle[i][j] = true;
		} else {
			if(j <= i && triangle[i][j] == true) {
				System.err.println("ERROR: trying to remove an illegal piece");
				System.exit(1);
			} else {
				System.err.println("WARNING: piece you're trying to remove is out of bounds");
			}
		}
		
		this.numPiecesLeft++;
		this.lastLookupNumberResult = -1;
	}
	
	private TriangleBoard prevLocation = null;
	private HashSet<String> moveList = null;
	
	private static long TEST_DEBUG_PRINT[] = new long[100];
	private static long TEST_TOTAL_MOVES_FOUND[] = new long[100];
	private static long TEST_TOTAL_MOVES_NEEDED[] = new long[100];
	
//This works, but isn't going to be used,
	//unless we do a forward save and backwards search:
	public ArrayList<String> getNecessaryFullMovesToCheck(boolean mustBe100percentMesonEfficient) {
		
		ArrayList<String> fullList = getFullMovesExcludingRepeatMoves(mustBe100percentMesonEfficient);
		ArrayList<String> neededList = new ArrayList<String>();
		
		if(this.prevLocation == null) {
			neededList = fullList;
			
		} else {
			
			String moves[] = this.historicMoveList.split(" ");

			String prevJump = moves[moves.length - 1];

			for(int i=0; i<fullList.size(); i++) {
				
				boolean dontNeedToCheck = false;
				
				if(this.prevLocation.moveList.contains(fullList.get(i))
					&& this.prevLocation.doOneMove(fullList.get(i)).couldMoveForwards(prevJump) ) {

					if(this.getLookupNumber() > this.prevLocation.doOneMove(fullList.get(i)).getLookupNumber()) {
						
						//Explanation:
						//Algo should have done current move (fullList.get(i)) first because
						//they are indep and current move would have gotten to a smaller lookup number had it gone first.
						//Unfortunately, I have to use an expensive lookupNumber because that's the only thing that uniquely identifies a position :(
						dontNeedToCheck = true;
					}
				}

				if(dontNeedToCheck == false) {
					neededList.add(fullList.get(i));
				}
			}
		}
		
		TEST_DEBUG_PRINT[this.numMovesMade]++;
		TEST_TOTAL_MOVES_FOUND[this.numMovesMade] += fullList.size();
		TEST_TOTAL_MOVES_NEEDED[this.numMovesMade] += neededList.size();
		
		if(TEST_DEBUG_PRINT[this.numMovesMade] % 200000 == 0) {
			System.out.println("Testing branching improvement for forwards jumps: (current sample: " + fullList.size() + " vs " + neededList.size() +")");
			
			for(int i=0; i<TEST_TOTAL_MOVES_FOUND.length; i++) {
				if(TEST_TOTAL_MOVES_NEEDED[i] == 0) {
					break;
				}
				System.out.println("num moves made before getNecessary: " + i);
				System.out.println("Ratio: " + ((1.0*TEST_TOTAL_MOVES_FOUND[i])/(1.0 * TEST_TOTAL_MOVES_NEEDED[i])));
				System.out.println("Perc: " + ((1.0*TEST_TOTAL_MOVES_NEEDED[i])/(1.0 * TEST_TOTAL_MOVES_FOUND[i])));
			}
		}
		
		return neededList;
	}

	//WARN: lots of copy/paste from getFullMovesExcludingRepeatMoves
	public ArrayList<String> getFullMovesWith2MovesAwayFilters(boolean mustBe100percentMesonEfficient) {

		boolean goodStarts[][] = this.triangle;
		if(mustBe100percentMesonEfficient) {
			goodStarts = PositonFilterTests.getPegsToMoveThatReduceNumMesonRegions(this.triangle);
		}
		
		int lastPegLocation;
		int lastPegLocationi;
		int lastPegLocationj;
		try {
			lastPegLocation = Integer.parseInt(this.historicMoveList.substring(this.historicMoveList.lastIndexOf("-") + 1));
			lastPegLocationi = lastPegLocation / triangle.length;
			lastPegLocationj = lastPegLocation % triangle.length;

		} catch(Exception e) {
			lastPegLocationi = -1;
			lastPegLocationj = -1;
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		
		
		int sumPegsInPosClass[] = getSumPegsInPosClass(triangle);
		int sumPegsOnEdge[] = getSumPegsInEdgeClass(triangle);
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				if(goodStarts[i][j] && (i != lastPegLocationi || j != lastPegLocationj) ) {
					int movingPegPosClass = getPositionClass(i, j);
					ret.addAll(getPromisingMovesToPositions1MoveAwayFromGoal(i * triangle.length + j, sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass));
				}
			}
		}
		
		moveList = new HashSet<String>();
		moveList.addAll(ret);
		return ret;
	}
	
	public static final int NUM_POSITION_CLASSES = 4;
	
	public static int[] getSumPegsInPosClass(boolean triangle[][]) {
		
		int sumPegsInPosClass[] = new int[NUM_POSITION_CLASSES];
		for(int i=0; i<sumPegsInPosClass.length; i++) {
			sumPegsInPosClass[i] = 0;
		}
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<=i; j++) {
				if(triangle[i][j]) {
					sumPegsInPosClass[getPositionClass(i, j)]++;
				}
			}
		}
		return sumPegsInPosClass;
	}
	
	public static int getPositionClass(int i, int j) {
		return 2*(i%2) + j%2;
	}

	public static int NUM_EDGES = 3;
	public static int[] getSumPegsInEdgeClass(boolean triangle[][]) {
		int sumPegsOnEdge[] = new int[NUM_EDGES];
		for(int i=0; i<sumPegsOnEdge.length; i++) {
			sumPegsOnEdge[i] = 0;
		}

		//Corner pieces are considered on both edges
		for(int i=0; i<triangle.length; i++) {
			if(triangle[i][0]) {
				sumPegsOnEdge[0]++;
			}
			if(triangle[i][i]) {
				sumPegsOnEdge[1]++;
			}
			if(triangle[triangle.length - 1][i]) {
				sumPegsOnEdge[2]++;
			}
		}
		return sumPegsOnEdge;
	}

	private void updateSumOfEdges(int i, int j, int sumPegsOnEdge[], int diff) {
		if(i==0) {
			sumPegsOnEdge[0] += diff;
		}
		if(i==j) {
			sumPegsOnEdge[1] += diff;
		}
		if(i==triangle.length - 1) {
			sumPegsOnEdge[2] += diff;
		}
	}
	
	public void updateTmpBoardProperties(int i, int j, int sumPegsInPosClass[], int sumPegsOnEdge[], int diff) {
		updateSumOfEdges(i, j, sumPegsInPosClass, diff);
		sumPegsInPosClass[getPositionClass(i, j)] += diff;
	}

	private ArrayList<String> getPromisingMovesToPositions1MoveAwayFromGoal(int code, int sumPegsInPosClass[], int sumPegsOnEdge[], int movingPegPosClass) {
		int istart = code / triangle.length;
		int jstart = code % triangle.length;
		
		ArrayList<String> ret = new ArrayList<String>();
		
		//There's 6 directions to check...
		//UP:
		if(istart >= 2 && jstart <= istart-2) {
			if(triangle[istart-1][jstart] && triangle[istart-2][jstart] == false) {
				
				updateTmpBoardProperties(istart-1, jstart, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart-2, jstart), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart-1, jstart, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		//UP LEFT
		if(istart >=2 && jstart >= 2) {
			if(triangle[istart-1][jstart-1] && triangle[istart-2][jstart-2] == false) {
				
				updateTmpBoardProperties(istart-1, jstart-1, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart-2, jstart-2), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart-1, jstart-1, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		//RIGHT:
		if(jstart + 2 < triangle[istart].length) {
			if(triangle[istart][jstart+1] && triangle[istart][jstart+2] == false) {
				
				updateTmpBoardProperties(istart, jstart+1, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart, jstart+2), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart, jstart+1, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		//LEFT:
		if(jstart >=2) {
			if(triangle[istart][jstart-1] && triangle[istart][jstart-2] == false) {
				
				updateTmpBoardProperties(istart, jstart-1, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart, jstart-2), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart, jstart-1, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		//DOWN:
		if(istart + 2 < triangle.length) {
			if(triangle[istart+1][jstart] && triangle[istart+2][jstart] == false) {
				
				updateTmpBoardProperties(istart+1, jstart, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart+2, jstart), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart+1, jstart, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		//DOWN RIGHT
		if(istart + 2 < triangle.length) {
			if(triangle[istart+1][jstart+1] && triangle[istart+2][jstart+2] == false) {
				
				updateTmpBoardProperties(istart+1, jstart+1, sumPegsInPosClass, sumPegsOnEdge, -1);
				ret.addAll( getPromissingMovesAfterJump1MoveAwayFromGoal(getCode(istart, jstart) +"-" + getCode(istart+2, jstart+2), sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass) );
				updateTmpBoardProperties(istart+1, jstart+1, sumPegsInPosClass, sumPegsOnEdge, +1);
			}
		}
		
		return ret;
	}
	

	private ArrayList<String> getPromissingMovesAfterJump1MoveAwayFromGoal(String jump, int sumPegsInPosClass[], int sumPegsOnEdge[], int movingPegPosClass) {
		ArrayList<String> ret = new ArrayList<String>();
		
		//Only use move if there's a single peg of a different position class that could clear the board
		//(i.e. there's a single peg of a different position class of the currently jumping peg
		//  AND that single peg doesn't have to go to an impossible to reach edge)

		for(int i=0; i<NUM_POSITION_CLASSES; i++) {
			if(i != movingPegPosClass && sumPegsInPosClass[i] == 1) {
				//TODO: implement edge filters!
				if(triangle.length % 2 == 0) {
					//TODO: test! n=6
					if( (i==0 && sumPegsOnEdge[2] == 0)
							|| (i==1 && sumPegsOnEdge[0] == 0 && sumPegsOnEdge[1] == 0 && sumPegsOnEdge[2] == 0)
							|| (i==2 && sumPegsOnEdge[1] == 0)
							|| (i==3 && sumPegsOnEdge[0] == 0) ){
						ret.add(jump);
						break;
					}
					
				} else {
					//TODO: test! n=7 
					if(i==0
							|| (i==1 && sumPegsOnEdge[0] == 0 && sumPegsOnEdge[1] == 0)
							|| (i==2 && sumPegsOnEdge[1] == 0 && sumPegsOnEdge[2] == 0)
							|| (i==3 && sumPegsOnEdge[0] == 0 && sumPegsOnEdge[2] == 0)) {
						ret.add(jump);
						break;
					}
					
				}
			}
		}
		

		TriangleBoard tmp = this.moveInternal(jump);
		
		int landing = Integer.parseInt(jump.split("-")[1]);
		ArrayList<String> newSeriesOfMoves = tmp.getPromisingMovesToPositions1MoveAwayFromGoal(landing, sumPegsInPosClass, sumPegsOnEdge, movingPegPosClass);
		for(int i=0; i<newSeriesOfMoves.size(); i++) {
			ret.add(jump.split("-")[0] + "-" + newSeriesOfMoves.get(i));
		}
		
		return ret;
	}
	

	public ArrayList<String> getFullMovesExcludingRepeatMoves() {
		return getFullMovesExcludingRepeatMoves(false);
	}
	
	public ArrayList<String> getFullMovesExcludingRepeatMoves(boolean mustBe100percentMesonEfficient) {
		
		boolean goodStarts[][] = this.triangle;
		if(mustBe100percentMesonEfficient) {
			goodStarts = PositonFilterTests.getPegsToMoveThatReduceNumMesonRegions(this.triangle);
		}
		
		int lastPegLocation;
		int lastPegLocationi;
		int lastPegLocationj;
		try {
			lastPegLocation = Integer.parseInt(this.historicMoveList.substring(this.historicMoveList.lastIndexOf("-") + 1));
			lastPegLocationi = lastPegLocation / triangle.length;
			lastPegLocationj = lastPegLocation % triangle.length;

		} catch(Exception e) {
			lastPegLocationi = -1;
			lastPegLocationj = -1;
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				if(goodStarts[i][j] && (i != lastPegLocationi || j != lastPegLocationj) ) {
					ret.addAll(getPossibleMovesFromPosition(i * triangle.length + j));
				}
			}
		}
		
		moveList = new HashSet<String>();
		moveList.addAll(ret);
		return ret;
		
	}
	
	public int getCode(int i, int j) {
		return i*triangle.length + j;
	}
	
	
	//Check if pos could do the move in input	
	//i.e: return true if there's no pegs in the way of the backwards move

	private boolean couldMoveForwards(String forwardsMove) {
		
		String seriesOfJumps[] = forwardsMove.split("-");
		
		int startingI = -1;
		int startingJ = -1;
		
		
		for(int i=0; i<seriesOfJumps.length - 1; i++) {
			
			int from = Integer.parseInt(seriesOfJumps[i]);
			int to = Integer.parseInt(seriesOfJumps[i+1]);

			int fromi = from / triangle.length;
			int fromj = from % triangle.length;
			
			int toi = to / triangle.length;
			int toj = to % triangle.length;
			
			int betweeni = (fromi + toi)/2;
			int betweenj = (fromj + toj)/2;
			
			if(i == 0) {
				//Handle the fact that the peg should at the original position:
				startingI = fromi;
				startingJ = fromj;
				
				if(triangle[startingI][startingJ] == false) {
					return false;
				}
			}
			
			if( triangle[betweeni][betweenj] == false 
				|| triangle[toi][toj] == true) {
				
				if(triangle[toi][toj] == true && startingI == toi && startingJ == toj) {
					//It's only ok for the "to" coord to have a peg if that is also the starting coord (i.e: the peg that's doing the jumping)
				} else {
					//else not ok because that peg is in the way
					return false;
				}
			}
			
		}
		
		return true;
	}

	private ArrayList<String> getPossibleMovesFromPosition(int code) {
		int istart = code / triangle.length;
		int jstart = code % triangle.length;
		
		ArrayList<String> ret = new ArrayList<String>();
		
		//There's 6 directions to check...
		//UP:
		if(istart >= 2 && jstart <= istart-2) {
			if(triangle[istart-1][jstart] && triangle[istart-2][jstart] == false) {
				
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart)) );
			}
		}
		
		//UP LEFT
		if(istart >=2 && jstart >= 2) {
			if(triangle[istart-1][jstart-1] && triangle[istart-2][jstart-2] == false) {
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart-2, jstart-2)) );
			}
		}
		
		//RIGHT:
		if(jstart + 2 < triangle[istart].length) {
			if(triangle[istart][jstart+1] && triangle[istart][jstart+2] == false) {
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart+2)) );
			}
		}
		
		//LEFT:
		if(jstart >=2) {
			if(triangle[istart][jstart-1] && triangle[istart][jstart-2] == false) {
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart, jstart-2)) );
			}
		}
		
		//DOWN:
		if(istart + 2 < triangle.length) {
			if(triangle[istart+1][jstart] && triangle[istart+2][jstart] == false) {
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart)) );
			}
		}
		
		//DOWN RIGHT
		if(istart + 2 < triangle.length) {
			if(triangle[istart+1][jstart+1] && triangle[istart+2][jstart+2] == false) {
				ret.addAll( getPossibleMovesAfterJump(getCode(istart, jstart) +"-" + getCode(istart+2, jstart+2)) );
			}
		}
		
		return ret;
	}
	
	private ArrayList<String> getPossibleMovesAfterJump(String jump) {
		ArrayList<String> ret = new ArrayList<String>();
		
		ret.add(jump);
		

		TriangleBoard tmp = this.moveInternal(jump);
		
		int landing = Integer.parseInt(jump.split("-")[1]);
		ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(landing);
		for(int i=0; i<newSeriesOfMoves.size(); i++) {
			ret.add(jump.split("-")[0] + "-" + newSeriesOfMoves.get(i));
		}
		
		return ret;
	}


	//WARNING: If you're moving the wrong peg, it won't count as an extra move
	public TriangleBoard doOneMove(String move) {
		String seriesOfJumps[] = move.split("-");
		
		TriangleBoard newBoard = this;
		
		for(int i=0; i<seriesOfJumps.length - 1; i++) {
			
			int from = Integer.parseInt(seriesOfJumps[i]);
			int to = Integer.parseInt(seriesOfJumps[i+1]);
			
			newBoard = newBoard.moveInternal(from + "-" + to);
		}
		
		if(newBoard == this) {
			System.out.println("ERROR blank move!");
			System.exit(1);
		}

		newBoard.prevLocation = this;
		
		return newBoard;
		
	}
	
	//pre: valid move
	private TriangleBoard moveInternal(String move) {
		String fromTo[] = move.split("-");
		
		int from = Integer.parseInt(fromTo[0]);
		int to = Integer.parseInt(fromTo[1]);
		
		int fromI = from / triangle.length;
		int fromJ = from % triangle.length;
		
		int toI = to / triangle.length;
		int toJ = to % triangle.length;
		
		TriangleBoard newBoard = new TriangleBoard(triangle.length);
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				newBoard.triangle[i][j] = triangle[i][j];
			}
		}
		
		if(newBoard.triangle[fromI][fromJ] == false) {
			System.out.println("ERROR move 1");
		}
		
		if(newBoard.triangle[(fromI+toI)/2][(fromJ+toJ)/2] == false) {
			System.out.println("ERROR move 2");
		}
		
		if(newBoard.triangle[toI][toJ] == true) {
			System.out.println("ERROR move 3");
		}
		
		newBoard.triangle[fromI][fromJ] = false;
		newBoard.triangle[(fromI+toI)/2][(fromJ+toJ)/2] = false;
		newBoard.triangle[toI][toJ] = true;
		
		newBoard.numPiecesLeft = this.numPiecesLeft - 1;
		
		newBoard.historicMoveList = this.historicMoveList;
		
		if(internalLastJumpCodeForMultiJumpMoves == from) {
			//not a new move
			newBoard.numMovesMade = this.numMovesMade;
			newBoard.historicMoveList += "-" + to;
			
		} else {
			newBoard.numMovesMade = this.numMovesMade + 1;
			newBoard.historicMoveList += "  " + move;
		}
		newBoard.internalLastJumpCodeForMultiJumpMoves = to;
		
		return newBoard;
	}


	public int getNumPiecesLeft() {
		return numPiecesLeft;
	}

	public int getNumMovesMade() {
		return numMovesMade;
	}

	public String getHistoricMoveList() {
		return historicMoveList;
	}
	

	private long lastLookupNumberResult = -1;
	public long getLookupNumber() {
		if(lastLookupNumberResult == -1) {
			lastLookupNumberResult = TriangleLookup.convertToNumberWithComboTricksAndSymmetry(triangle, numPiecesLeft);
		}
		return TriangleLookup.convertToNumberWithComboTricksAndSymmetry(triangle, numPiecesLeft);
	}
	
	public int length() {
		return triangle.length;
	}
	
}