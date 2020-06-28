package triangleBoardCheater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import triangleBoard5.PositonFilterTests;
import triangleBoard5.TriangleBoard;
import triangleBoard5.TriangleLookup;
import triangleBoard5.utilFunctions;
import triangleBoardInterface.TriangleBoardI;

//Hard limit on number of records:
//20344823
public class TriangleBoardCheater  implements TriangleBoardI {
	//Only hard-copies allow
	
	public static void main(String args[]) {
		//TESTING code:
		TriangleBoardCheater board = new TriangleBoardCheater(4);

		board.removePiece(4);
		board.draw();
		
		
		ArrayList<String> moves = board.getFullMovesExcludingRepeatMoves();
		for(int i=0; i<moves.size(); i++) {
			System.out.println(moves.get(i));
		}
		
		
		for(int i=0; i<moves.size(); i++) {
			TriangleBoardCheater tmp = board.doOneMove(moves.get(i));
			tmp.draw();
		}

		System.out.println("***");
		System.out.println("***");
		System.out.println("Try a double loop:");
		for(int i=0; i<moves.size(); i++) {
			TriangleBoardCheater tmp = board.doOneMove(moves.get(i));
			//tmp.draw();
			
			ArrayList <String> moves2 = tmp.getFullMovesExcludingRepeatMoves();

			for(int j=0; j<moves2.size(); j++) {
				TriangleBoardCheater tmp2 = tmp.doOneMove(moves2.get(j));
				tmp2.draw();
				
				System.out.println("Lookup number:");
				System.out.println(tmp2.getLookupNumber());
			}
			
			
		}
		
		System.out.println("***");
		System.out.println("***");
		System.out.println("Try a trigle loop:");
		for(int i=0; i<moves.size(); i++) {
			TriangleBoardCheater tmp = board.doOneMove(moves.get(i));
			
			ArrayList <String> moves2 = tmp.getFullMovesExcludingRepeatMoves();

			for(int j=0; j<moves2.size(); j++) {
				TriangleBoardCheater tmp2 = tmp.doOneMove(moves2.get(j));
				ArrayList <String> moves3 = tmp2.getFullMovesExcludingRepeatMoves();

				
				for(int k=0; k<moves3.size(); k++) {
					TriangleBoardCheater tmp3 = tmp2.doOneMove(moves3.get(k));
					tmp3.draw();
					
					System.out.println("Lookup number:");
					System.out.println(tmp3.getLookupNumber());
				}
				
			}
			
			
		}
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Test2:");
		
		TriangleBoardCheater board4 = new TriangleBoardCheater(4);
		board4.removePiece(4);
		String moves2[] = "12-4  0-8  10-0  14-12".split("  ");
		
		for(int i=0; i<moves2.length; i++) {
			if(moves2[i].trim().equals("") == false) {
				board4 = board4.doOneMove(moves2[i]);
				board4.draw();
			}
		}
		board4.draw();
		
		
		
	}
	
	
	private boolean triangle[][];
	private int numPiecesLeft;
	private int numMovesMade;

	private String historicMoveList;
	private int internalLastJumpCodeForMultiJumpMoves = -1;
	
	public TriangleBoardCheater(int length) {
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
	
	public TriangleBoardCheater(TriangleBoardI a) {
		boolean copyTriangle[][] = a.getTriangle();

		triangle = new boolean[copyTriangle.length][];

		numPiecesLeft = 0;
		
		for(int i=0; i<copyTriangle.length; i++) {
			
			triangle[i] = new boolean[copyTriangle[i].length];

			for(int j=0; j < copyTriangle[i].length; j++) {
				triangle[i][j] = copyTriangle[i][j];
				if(copyTriangle[i][j]) {
					numPiecesLeft++;
				}
				
			}
		}
		
		numMovesMade = 0;
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
				System.exit(1);
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
				System.exit(1);
			}
		}
		
		this.numPiecesLeft++;
		this.lastLookupNumberResult = -1;
	}
	
	private HashSet<String> moveList = null;
	
	

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
		
		
		if(ret.isEmpty()) {
			//Cheater always wins...
			for(int i=0; i<triangle.length; i++) {
				for(int j=0; j<triangle[i].length; j++) {
					if(goodStarts[i][j] && (i != lastPegLocationi || j != lastPegLocationj) ) {
						//TODO: I assume this only works for corners?
						ret.add((i * triangle.length + j) + "-" + (i * triangle.length + j));
						
					}
				}
			}
		}
		
		return ret;
		
	}
	
	public int getCode(int i, int j) {
		return i*triangle.length + j;
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
		

		TriangleBoardCheater tmp = this.moveInternal(jump);
		
		int landing = Integer.parseInt(jump.split("-")[1]);
		ArrayList<String> newSeriesOfMoves = tmp.getPossibleMovesFromPosition(landing);
		for(int i=0; i<newSeriesOfMoves.size(); i++) {
			ret.add(jump.split("-")[0] + "-" + newSeriesOfMoves.get(i));
		}
		
		return ret;
	}


	//WARNING: If you're moving the wrong peg, it won't count as an extra move
	public TriangleBoardCheater doOneMove(String move) {
		
		
		String seriesOfJumps[] = move.split("-");
		
		TriangleBoardCheater newBoard = this;
		
		for(int i=0; i<seriesOfJumps.length - 1; i++) {
			
			int from = Integer.parseInt(seriesOfJumps[i]);
			int to = Integer.parseInt(seriesOfJumps[i+1]);
			
			newBoard = newBoard.moveInternal(from + "-" + to);
		}
		
		if(newBoard == this) {
			System.out.println("ERROR blank move!");
			System.exit(1);
		}
	
		
		if(newBoard.numPiecesLeft + 3 < utilFunctions.getTriangleNumber(this.triangle.length)) {
			int lastPegLocation;
			int lastPegLocationi;
			int lastPegLocationj;
			try {
				lastPegLocation = Integer.parseInt(newBoard.historicMoveList.substring(newBoard.historicMoveList.lastIndexOf("-") + 1));
				lastPegLocationi = lastPegLocation / triangle.length;
				lastPegLocationj = lastPegLocation % triangle.length;

			} catch(Exception e) {
				lastPegLocationi = -1;
				lastPegLocationj = -1;
			}
			
			newBoard.triangle[lastPegLocationi][lastPegLocationj] = false;
			newBoard.numPiecesLeft--;
			
			//newBoard.internalLastJumpCodeForMultiJumpMoves


			boolean cheaterTriangle[][] = new boolean[newBoard.triangle.length][];
			
			//Cheating happens here: (Basically assume anything capturable is captured.)
			for(int i=0; i<cheaterTriangle.length; i++) {
				cheaterTriangle[i] = new boolean[i + 1];
				for(int j=0; j<cheaterTriangle[i].length; j++) {
					cheaterTriangle[i][j] = false;
				}
			}
	
			for(int i=0; i<newBoard.triangle.length; i++) {
				for(int j=0; j< newBoard.triangle[i].length; j++) {
					if(newBoard.triangle[i][j]) {
						
						cheaterTriangle[i][j] = true;
						
						if( (j>0 && j < newBoard.triangle[i].length - 1)
							&& newBoard.triangle[i][j-1] == false && newBoard.triangle[i][j+1] == false ) {
							
							cheaterTriangle[i][j] = false;
							newBoard.numPiecesLeft--;
						
						} else if(i>0 && i < newBoard.triangle.length - 1 && j < newBoard.triangle[i-1].length
							&& newBoard.triangle[i-1][j] == false && newBoard.triangle[i+1][j] == false ) {
								
							cheaterTriangle[i][j] = false;
							newBoard.numPiecesLeft--;
						
						} else if( (i>0 && i < newBoard.triangle.length - 1)
							&& (j>0 && j < newBoard.triangle[i].length - 1)
							 && newBoard.triangle[i-1][j-1] == false && newBoard.triangle[i+1][j+1] == false ) {
								
							cheaterTriangle[i][j] = false;
							newBoard.numPiecesLeft--;
						}
					}
				}
			}
			
			newBoard.triangle = cheaterTriangle;

			//End cheating
		}
		
		
		return newBoard;
		
	}
	
	//pre: valid move
	private TriangleBoardCheater moveInternal(String move) {
		

		String fromTo[] = move.split("-");
		
		int from = Integer.parseInt(fromTo[0]);
		int to = Integer.parseInt(fromTo[1]);
		
		int fromI = from / triangle.length;
		int fromJ = from % triangle.length;
		
		int toI = to / triangle.length;
		int toJ = to % triangle.length;
		
		
		
		TriangleBoardCheater newBoard = new TriangleBoardCheater(triangle.length);
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				newBoard.triangle[i][j] = triangle[i][j];
			}
		}
		
		//Check if we're not just cheating and are actually capturing a piece in a move:
		if(to != from) {
			
			if(newBoard.triangle[fromI][fromJ] == false) {
				System.out.println("ERROR move 1");
				System.exit(1);
			}
			
			if(newBoard.triangle[(fromI+toI)/2][(fromJ+toJ)/2] == false) {
				System.out.println("ERROR move 2");
				System.exit(1);
			}
			
			if(newBoard.triangle[toI][toJ] == true) {
				System.out.println("ERROR move 3");
				System.exit(1);
			}
			
			newBoard.triangle[fromI][fromJ] = false;
			newBoard.triangle[(fromI+toI)/2][(fromJ+toJ)/2] = false;
			newBoard.triangle[toI][toJ] = true;
			
			newBoard.numPiecesLeft = this.numPiecesLeft - 1;

		} else {
			newBoard.numPiecesLeft = this.numPiecesLeft;
		}

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