package triangleBoardCheater;


//So cool: https://pepkin88.me/triangle-peg-solitaire/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import triangleBoard5.PositonFilterTests;
import triangleBoard5.triangleRecord;
import triangleBoard5.utilFunctions;


//Old ideas:

//STILL IN THE RUNNING: order move list from longest to shortest (longest are probably better)
				//Maybe only activate it for the final depth?

public class TriangleSolve {


	public static HashSet<Long> startingPositionSearched = new HashSet<Long>();


	//This is looking for just 1 solution...
	// TODO: try finding all optimal solutions later...

	//TODO: use pen & paper to figure out which layer actually needs getNecessaryFilter
	public static final int LENGTH = 4;

	public static int MAX_DEPTH_TOTAL = 5;

	public static boolean SEARCH_SINGLE_GOAL = false;
	public static int GOAL_I = 0;
	public static int GOAL_J = 0;
	
	
	public static int MEM_DEPTH_FORWARDS = Math.min(12, MAX_DEPTH_TOTAL - 1);

	
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
				getBestMoveList(boardStart);
				
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
	
	
	public static void getBestMoveList(TriangleBoardCheater board) {

		initRecordedTriangles(board.length());

		int debugNumRecordSavedPrevDepth = 0;	
		System.out.println("Start search for solution starting with:\n"  + board);
		
		//Need to reinit recorded triangle because we're starting over from depth 1:
		initRecordedTriangles(board.length());
		debugNumRecordSavedPrevDepth = 0;
		
		for(int depth=PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle()); depth<= MAX_DEPTH_TOTAL; depth++) {
			System.out.println("DEBUG: trying depth " + depth);
			
			DEPTH_USED_IN_SEARCH = depth;
			
			getBestMoveList(board, depth);
			
			System.out.println("End of search with depth " + depth + " and triangle length " + board.length());
			System.out.println("Num records saved for prev depths: " + debugNumRecordSavedPrevDepth);
			System.out.println("Num records saved total: " + numRecordsSavedForDEBUG);
			debugNumRecordSavedPrevDepth = numRecordsSavedForDEBUG;
			
		}
	}
	
	
	public static int debugVisitsPerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	

	private static int DEPTH_USED_IN_SEARCH = -1;
	
	public static void getBestMoveList(TriangleBoardCheater board, int curMaxDepth) {

		debugVisitsPerNumMoves[board.getNumMovesMade()]++;
		if(curMaxDepth == 3) {
			numFunctionCallFor3AwayDEBUG++;
			
			if(numFunctionCallFor3AwayDEBUG % 500000 == 0) {
				
				System.out.println("Current depth: " + DEPTH_USED_IN_SEARCH + " out of " + MAX_DEPTH_TOTAL);
	
				System.out.println("Num records saved: " + numRecordsSavedForDEBUG);
				board.draw();
				System.out.println("Min num moves: " +( board.getNumMovesMade() + PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle())));
				
				System.out.println("Debug stats");
				for(int i=0; i<=MAX_DEPTH_TOTAL; i++) {
	
					System.out.println("Num moves = " + i);
					System.out.println("visits to getBestMoveList:           " + debugVisitsPerNumMoves[i]);
				
				}
				System.out.println("End DebugStats");
				
			}
		}

		//Less than or equal to 1 for the cheater triangle:
		if(board.getNumPiecesLeft() == 0) {
			System.out.println("FOUND A SOLUTION:");
			board.draw();
			return;
		} else if(board.getNumPiecesLeft() < 0) {
			System.out.println("ERROR: impossible board!");
			System.exit(1);
		}
		
		long lookup = board.getLookupNumber();
		
		if(curMaxDepth == 0){
			return;
		}

		if(recordedTriangles[board.getNumPiecesLeft()].containsKey(lookup)) {
			
			triangleRecord previouslyFoundNode = recordedTriangles[board.getNumPiecesLeft()].get(lookup);
			
			if(board.getNumMovesMade() > previouslyFoundNode.getNumMovesToGetToPos()) {
				return;
				
			} else if(board.getNumMovesMade() == previouslyFoundNode.getNumMovesToGetToPos()){
				
				if(previouslyFoundNode.getDepthUsedToFindRecord() == DEPTH_USED_IN_SEARCH) {
					return;
					
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
				return;
				
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
			
		for(int i=0; i<moves.size(); i++) {
			getBestMoveList(board.doOneMove(moves.get(i)), curMaxDepth - 1);
		}
		
		return;
	}
	
}
