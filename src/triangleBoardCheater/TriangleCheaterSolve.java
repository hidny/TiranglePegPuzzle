package triangleBoardCheater;


//So cool: https://pepkin88.me/triangle-peg-solitaire/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import triangleBoard5.PositonFilterTests;
import triangleBoard5.triangleRecord;
import triangleBoard5.utilFunctions;


//The idea of this is to solve the board quickly by cheating and then if the cheating method
// doesn't do it in enough moves, we could dismiss the solutions.
//I'm hoping that this is better than getting an idea of the minimum number of moves by just counting the number of Merson regions 

public class TriangleCheaterSolve {


	public static HashSet<Long> startingPositionSearched = new HashSet<Long>();


	//This is looking for just 1 solution...
	// TODO: try finding all optimal solutions later...

	//TODO: use pen & paper to figure out which layer actually needs getNecessaryFilter
	public static final int LENGTH = 9;

	public static int MAX_DEPTH_TOTAL = 20;

	public static boolean SEARCH_SINGLE_GOAL = false;
	public static int GOAL_I = 0;
	public static int GOAL_J = 0;
	
	
	public static int MEM_DEPTH_FORWARDS = Math.min(20, MAX_DEPTH_TOTAL - 1);

	
	public static void main(String args[]) {
		

		//DEBUG STATS:
		for(int i=0; i<MAX_DEPTH_TOTAL + 1; i++) {

			debugVisitsPerNumMoves[i] = 0;
		}
		//END DEBUG STATS
		
		getForwardSolutions();
	}
	
	public static void getForwardSolutions() {
		
		System.out.println("Trying " + LENGTH + " in TriangleSolveOptimizedTrial5");
		System.out.println("Giving up after reaching a max depth of " + MAX_DEPTH_TOTAL);
		System.out.println();

		TriangleBoardCheater boardStart;
		
		for(int i=0; i<LENGTH; i++) {
			for(int j=0; j<=i; j++) {
				boardStart = new TriangleBoardCheater(LENGTH);
				boardStart.removePiece(i * LENGTH + j);
				
				long lookup = boardStart.getLookupNumber();
				
				if(startingPositionSearched.contains(lookup)) {
					System.out.println("SKIPING (" + i + ", " + j + ")");
					System.out.println();
					System.out.println();
					continue;
				} else {
					startingPositionSearched.add(lookup);
				}
				
				initRecordedTriangles(LENGTH);
				System.out.println("start!");
				int minNumberOfMovves = getMinNumberOfMovesByCheating(boardStart, MAX_DEPTH_TOTAL);
				System.out.println("Min number of moves: " + minNumberOfMovves);
				
			}
		}
	}


	public static int numFunctionCallFor3AwayDEBUG = 0;
	public static int numRecordsSavedForDEBUG = 0;
	
	
	public static HashMap<Long, triangleRecord>[] recordedTriangles;
	
	public static void initRecordedTriangles(int boardLength) {
		numRecordsSavedForDEBUG = 0;
		recordedTriangles = new HashMap[utilFunctions.getTriangleNumber(boardLength)];
		for(int i=0; i<recordedTriangles.length; i++) {
			recordedTriangles[i] = new HashMap<Long, triangleRecord>();
		}
		
	}
	
	
	public static int getMinNumberOfMovesByCheating(TriangleBoardCheater board, int maxDepth) {

		initRecordedTriangles(board.length());

		int debugNumRecordSavedPrevDepth = 0;

		//System.out.println("Start search for solution starting with:\n"  + board);
		
		//Need to reinit recorded triangle because we're starting over from depth 1:
		initRecordedTriangles(board.length());
		debugNumRecordSavedPrevDepth = 0;
		
		boolean foundSolution = false;
		int minMoves = -1;
		
		for(int depth=PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle()); foundSolution == false && depth<= maxDepth; depth++) {
			//System.out.println("DEBUG: trying depth " + depth);
			
			DEPTH_USED_IN_SEARCH = depth;
			
			foundSolution = getBestMoveList(board, depth);
			
			if(foundSolution) {
				minMoves = depth;
			}
			
			//System.out.println("End of search with depth " + depth + " and triangle length " + board.length());
			//System.out.println("Num records saved for prev depths: " + debugNumRecordSavedPrevDepth);
			//System.out.println("Num records saved total: " + numRecordsSavedForDEBUG);
			debugNumRecordSavedPrevDepth = numRecordsSavedForDEBUG;
			
		}

		return minMoves;
	}
	
	
	public static int debugVisitsPerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	

	private static int DEPTH_USED_IN_SEARCH = -1;
	
	public static boolean getBestMoveList(TriangleBoardCheater board, int curMaxDepth) {

		debugVisitsPerNumMoves[board.getNumMovesMade()]++;
		if(curMaxDepth == 3) {
			numFunctionCallFor3AwayDEBUG++;
			
			if(numFunctionCallFor3AwayDEBUG % 500000 == 0) {
				
				System.out.println("Cheater: Current depth: " + DEPTH_USED_IN_SEARCH + " out of " + MAX_DEPTH_TOTAL);
	
				System.out.println("Cheater: Num records saved: " + numRecordsSavedForDEBUG);
				board.draw();
				System.out.println("Cheater: Min num moves: " +( board.getNumMovesMade() + PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle())));
				
				System.out.println("Cheater: Debug stats");
				for(int i=0; i<=MAX_DEPTH_TOTAL; i++) {
	
					System.out.println("Cheater: Num moves = " + i);
					System.out.println("Cheater: visits to getBestMoveList:           " + debugVisitsPerNumMoves[i]);
				
				}
				System.out.println("Cheater: End DebugStats");
				
			}
		}

		//Less than or equal to 1 for the cheater triangle:
		if(board.getNumPiecesLeft() == 0) {
			//System.out.println("FOUND A SOLUTION:");
			//board.draw();
			return true;
		} else if(board.getNumPiecesLeft() < 0) {
			System.out.println("ERROR: impossible board!");
			System.exit(1);
		}
		
		long lookup = board.getLookupNumber();
		
		if(curMaxDepth == 0){
			return false;
		}

		if(recordedTriangles[board.getNumPiecesLeft()].containsKey(lookup)) {
			
			triangleRecord previouslyFoundNode = recordedTriangles[board.getNumPiecesLeft()].get(lookup);
			
			if(board.getNumMovesMade() > previouslyFoundNode.getNumMovesToGetToPos()) {
				return false;
				
			} else if(board.getNumMovesMade() == previouslyFoundNode.getNumMovesToGetToPos()){
				
				if(previouslyFoundNode.getDepthUsedToFindRecord() == DEPTH_USED_IN_SEARCH) {
					return false;
					
				} else {
					previouslyFoundNode.updateNumMovesToGetToPos(board.getNumMovesMade(), DEPTH_USED_IN_SEARCH);
					
				}
				
			} else {
				previouslyFoundNode.updateNumMovesToGetToPos(board.getNumMovesMade(), DEPTH_USED_IN_SEARCH);
			}
		}
		
		//APPLY FILTER AFTER WE NOTICE POSITION IS NOT FOUND:
		boolean mustBe100percentMesonEfficient = false;
		
		if(curMaxDepth > 0) {
			int numMesonRegions = PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle());
			//Implemented A* filter
			//Found heuristic function with merson regions:
			if(board.getNumMovesMade() + numMesonRegions > DEPTH_USED_IN_SEARCH) {
				return false;
				
			} else if(board.getNumMovesMade() + numMesonRegions == DEPTH_USED_IN_SEARCH) {
				mustBe100percentMesonEfficient = true;
			}
			
		}

		//END APPLY FILTER AFTER WE NOTICE POSITION IS NOT FOUND:
		

		//Record position if worthwhile:
		//(Only record if it won't affect memory requirements too much)
		if(board.getNumMovesMade() <= MEM_DEPTH_FORWARDS) {
		
			if(recordedTriangles[board.getNumPiecesLeft()].containsKey(lookup) == false) {
				recordedTriangles[board.getNumPiecesLeft()].put(lookup, new triangleRecord(board.getNumMovesMade(), DEPTH_USED_IN_SEARCH));
				numRecordsSavedForDEBUG++;
				
			}
		}
		
		//get moves available:
		ArrayList<String> moves;
		
		//Getting full moves because I'm not sure if getNecessary still works with the new cheater rules...
		//TODO: investigate reinstating the getNecessaryFullCheatMovesToCheck function.
		moves = board.getFullMovesExcludingRepeatMoves(mustBe100percentMesonEfficient);
		
		
		moves = PositonFilterTests.excludeMovesThatLeadToSameOutcome(board, moves);
		
		boolean foundSolution =false;
		for(int i=0; i<moves.size(); i++) {
			boolean tmp = getBestMoveList(board.doOneMove(moves.get(i)), curMaxDepth - 1);
			
			if(tmp) {
				foundSolution = true;
				
				//TDOO: comment next line for more solutions: 
				return foundSolution;
			}
		}
		
		return foundSolution;
	}
	
}
