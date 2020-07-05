package triangleBoard5;


//So cool: https://pepkin88.me/triangle-peg-solitaire/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import triangleBoardInterface.TriangleBoardI;


//Old ideas:

//STILL IN THE RUNNING: order move list from longest to shortest (longest are probably better)
				//Maybe only activate it for the final depth?

public class TriangleSolve {




	//This is looking for just 1 solution...
	// TODO: try finding all optimal solutions later...

	public static final int LENGTH = 9;

	public static int MAX_DEPTH_TOTAL = 16;

	public static boolean SEARCH_SINGLE_GOAL = false;
	public static int GOAL_I = 0;
	public static int GOAL_J = 0;
	
	
	public static int PERMANENT_SAVE_DEPTH = 10;
	
	public static int REFRESH_MEM_DEPTH_FORWARDS = Math.min(14, MAX_DEPTH_TOTAL - 3);
	
	//recorded overnight:
	//32486384
	//Made it to:
	// 27-9  0-18  29-27-9  20-0-18  45-27-9-29  40-20  60-40  56-36  74-56  63-45  48-28-30-50-48-46-28  45-27-29
	//public static int EIGHTEEN_MIL = 18000000;
	public static int SAVE_LIMIT = 20000000;
	
	//After reducing save limit to 20M: (It's much faster!)
	//public static int SAVE_LIMIT = 5000;
	// 27-9  0-18  29-27-9  20-0-18  48-28  56-38  45-27-9-29-47  68-48  60-58  74-56-38  63-45-65  72-74-54

	public static void main(String args[]) {
		

		//DEBUG STATS:
		for(int i=0; i<MAX_DEPTH_TOTAL + 1; i++) {

			debugVisitsPerNumMoves[i] = 0;
			debugIsolationFilterPerNumMoves[i] = 0;
			debugIsolationFilterTriedPerNumMoves[i] = 0;
			debugIsolationFilterClosePerNumMoves[i] = 0;
			debugIsolationFilterClosePerNumMoves2[i] = 0;
		}
		//END DEBUG STATS
		
		getForwardSolutions();
	}
	
	public static void getForwardSolutions() {

		HashSet<Long> startingPositionSearched = new HashSet<Long>();

		System.out.println("Trying " + LENGTH + " in TriangleSolveOptimizedTrial5");
		System.out.println("Giving up after reaching a max depth of " + MAX_DEPTH_TOTAL);
		System.out.println();

		TriangleBoard boardStart;
		
		for(int i=0; i<LENGTH; i++) {
			for(int j=0; j<=i; j++) {
				boardStart = new TriangleBoard(LENGTH);
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
				getBestMoveList(boardStart);
				
			}
		}
	}


	public static int numFunctionCallFor4AwayDEBUG = 0;
	public static int numRecordsSavedForDEBUG = 0;
	public static int numRecordsCurrentlySaved = 0;
	
	
	public static HashMap<Long, Integer>[] recordedTriangles;
	
	public static void initRecordedTriangles(int boardLength) {
		numRecordsSavedForDEBUG = 0;
		numRecordsCurrentlySaved = 0;
		recordedTriangles = new HashMap[utilFunctions.getTriangleNumber(boardLength)];
		for(int i=0; i<recordedTriangles.length; i++) {
			recordedTriangles[i] = new HashMap<Long, Integer>();
		}
		
	}
	
	public static void refreshTriangles(int boardLength) {
		
		System.out.println("REFRESH TRIANGLES");

		for(int i=0; i<utilFunctions.getTriangleNumber(boardLength); i++) {
			
			Iterator it = recordedTriangles[i].keySet().iterator();
			HashMap<Long, Integer> permaSavedPos = new HashMap<Long, Integer>();
			
			while(it.hasNext()) {
				long tmp = (Long)it.next();
				if(recordedTriangles[i].get(tmp) > PERMANENT_SAVE_DEPTH) {
					numRecordsCurrentlySaved--;
				} else {
					permaSavedPos.put(tmp, recordedTriangles[i].get(tmp));
				}
			}

			recordedTriangles[i] = permaSavedPos;
		}
		
		
		//SANITY TEST
		for(int i=0; i<utilFunctions.getTriangleNumber(boardLength); i++) {
			
			Iterator it = recordedTriangles[i].keySet().iterator();
			
			while(it.hasNext()) {
				if(recordedTriangles[i].get(it.next()) > PERMANENT_SAVE_DEPTH) {
					System.out.println("ERROR: didn't delete properly!");
					System.exit(1);
				}
			}
		}
		//SANITY TEST
		
		//have the limit of saved positions be at least FACTOR times the number of positions to auto-save.
		int ceiling = numRecordsCurrentlySaved * 2;
		
		if(ceiling > SAVE_LIMIT) {
			System.out.println("Extending save limit from " + SAVE_LIMIT + " to " + ceiling);
			SAVE_LIMIT = ceiling;
		}
		

		System.out.println("END REFRESH TRIANGLES");
	}
	
	
	public static void getBestMoveList(TriangleBoard board) {

		int debugNumRecordSavedPrevDepth = 0;	
		System.out.println("Start search for solution starting with:\n"  + board);
		
		//Need to reinit recorded triangle because we're starting over from depth 1:
		debugNumRecordSavedPrevDepth = 0;
		
		for(int depth = PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle()); depth<= MAX_DEPTH_TOTAL; depth++) {
			System.out.println("DEBUG: trying depth " + depth);
			
			DEPTH_USED_IN_SEARCH = depth;

			initRecordedTriangles(board.length());
			
			getBestMoveList(board, depth);
			
			System.out.println("End of search with depth " + depth + " and triangle length " + board.length());
			System.out.println("Num records saved for prev depths: " + debugNumRecordSavedPrevDepth);
			System.out.println("Num records saved total: " + numRecordsSavedForDEBUG);
			System.out.println("Num records saved currently: " + numRecordsCurrentlySaved);
			debugNumRecordSavedPrevDepth = numRecordsSavedForDEBUG;
			
		}
	}
	
	
	public static int debugNumFilteredOut = 0;
	public static int debugNumFiltered = 0;
	public static int debugVisitsPerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	public static int debugIsolationFilterPerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	public static int debugIsolationFilterTriedPerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	public static int debugIsolationFilterClosePerNumMoves[] = new int[MAX_DEPTH_TOTAL+1];
	public static int debugIsolationFilterClosePerNumMoves2[] = new int[MAX_DEPTH_TOTAL+1];
	

	//TODO: keep experimenting and changing this after every optimizaion made:
	//For Trig 9
	public static boolean isolationFilterIsWorthwhile(TriangleBoard board) {
		if(LENGTH == 9) {
			return board.getNumMovesMade() >= 14 || (board.getNumMovesMade() == 13 && board.getNumPiecesLeft() <= utilFunctions.getTriangleNumber(LENGTH)/2 );
		} else {
			return false;
		}
	}
	
	private static int DEPTH_USED_IN_SEARCH = -1;
	
	
	public static void getBestMoveList(TriangleBoard board, int curMaxDepth) {

		debugVisitsPerNumMoves[board.getNumMovesMade()]++;
		if(curMaxDepth == 4) {
			numFunctionCallFor4AwayDEBUG++;
			
			if(numFunctionCallFor4AwayDEBUG % 100000 == 0) {
				
				System.out.println("Current depth: " + DEPTH_USED_IN_SEARCH + " out of " + MAX_DEPTH_TOTAL);
	
				System.out.println("Num records saved total: " + numRecordsSavedForDEBUG);
				System.out.println("Num records saved currently: " + numRecordsCurrentlySaved);
				board.draw();
				System.out.println("Min num moves: " +( board.getNumMovesMade() + PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle())));
				
				System.out.println("Debug stats");
				for(int i=0; i<=MAX_DEPTH_TOTAL; i++) {
	
					System.out.println("Num moves = " + i);
					System.out.println("visits to getBestMoveList:           " + debugVisitsPerNumMoves[i]);
					System.out.println("Visits to Isolation filter:          " + debugIsolationFilterPerNumMoves[i]);
					System.out.println("Visits to Isolation filter function: " + debugIsolationFilterTriedPerNumMoves[i]);
					System.out.println("Close to Isolation filter:(off by 1) " + debugIsolationFilterClosePerNumMoves[i]);
					System.out.println("Close to Isolation filter:(off by 2) " + debugIsolationFilterClosePerNumMoves2[i]);
				}
				System.out.println("End DebugStats");
				
			}
		}

		if(board.getNumPiecesLeft() == 1) {
			System.out.println("FOUND A SOLUTION:");
			board.draw();
			//System.exit(1);
			return;
		}
		
		long lookup = board.getLookupNumber();
		
		if(curMaxDepth == 0){
			return;
		}

		if(recordedTriangles[board.getNumPiecesLeft()].containsKey(lookup)) {
			
			int prevNumMovesToGetToPos = recordedTriangles[board.getNumPiecesLeft()].get(lookup);
			
			if(board.getNumMovesMade() >= prevNumMovesToGetToPos) {
				return;
				
			} else {
				recordedTriangles[board.getNumPiecesLeft()].remove(lookup);
				recordedTriangles[board.getNumPiecesLeft()].put(lookup, board.getNumMovesMade());
				
			}
		}
		
		//APPLY FILTER AFTER WE NOTICE POSITION IS NOT FOUND:
		boolean mustBe100percentMesonEfficient = false;
		
		if(curMaxDepth > 0) {
			

			//Implemented A* filter
			//Simple find merson regions:
			int numMersonRegions = PositonFilterTests.getNumMesonRegionsSimple(board.getTriangle());
			int minNumMovesLeft = numMersonRegions;
			
			//TODO: Fix it!
			//Complex and untested cheater filter
			//int cheaterNumberOfMoves = PositonFilterTests.getCheaterHeuristicMixedWithNumMesonRegionsSimple(board, DEPTH_USED_IN_SEARCH - board.getNumMovesMade());
			//int minNumMovesLeft = Math.max(numMersonRegions, cheaterNumberOfMoves);
			
			
			
			//Found heuristic function with merson regions:
			if(board.getNumMovesMade() + minNumMovesLeft > DEPTH_USED_IN_SEARCH) {
				//board.draw();
				//System.out.println("Merson of above: " + numMersonRegions);
				return;
				
			} else if(board.getNumMovesMade() + numMersonRegions == DEPTH_USED_IN_SEARCH) {
				mustBe100percentMesonEfficient = true;
				//board.draw();
				//System.out.println("Merson above is efficient: " + numMersonRegions);
				
			}
			
			int numMovesLeftBasedOnPegIsolationSpanningTree = 0;
			
			//Only bother doing this calc when there's not many pegs left:
			if(isolationFilterIsWorthwhile(board)) {
				numMovesLeftBasedOnPegIsolationSpanningTree = PositonFilterTests.getMinMovesBasedOnIsolationSpanningTree(board.getTriangle(), board.getNumPiecesLeft());
				
				debugIsolationFilterTriedPerNumMoves[board.getNumMovesMade()]++;
			}
			
			if(board.getNumMovesMade() + numMovesLeftBasedOnPegIsolationSpanningTree > DEPTH_USED_IN_SEARCH) {
				debugIsolationFilterPerNumMoves[board.getNumMovesMade()]++;
				return;
			} else {
				if(isolationFilterIsWorthwhile(board)) {
					
					if(board.getNumMovesMade() + numMovesLeftBasedOnPegIsolationSpanningTree + 1 > DEPTH_USED_IN_SEARCH) {
						debugIsolationFilterClosePerNumMoves[board.getNumMovesMade()]++;
					} else if(board.getNumMovesMade() + numMovesLeftBasedOnPegIsolationSpanningTree + 2 > DEPTH_USED_IN_SEARCH) {
						debugIsolationFilterClosePerNumMoves2[board.getNumMovesMade()]++;
					}
					
				}
			}
		}

		//END APPLY FILTER AFTER WE NOTICE POSITION IS NOT FOUND:
		

		//Record position if worthwhile:
		//(Only record if it won't affect memory requirements too much)
		if(board.getNumMovesMade() <= REFRESH_MEM_DEPTH_FORWARDS) {
		
			if(recordedTriangles[board.getNumPiecesLeft()].containsKey(lookup) == false) {
				recordedTriangles[board.getNumPiecesLeft()].put(lookup, board.getNumMovesMade());
				numRecordsSavedForDEBUG++;
				numRecordsCurrentlySaved++;
				
				if(numRecordsCurrentlySaved > SAVE_LIMIT) {
					refreshTriangles(LENGTH);
				}
				
			}
		}
		
		//get moves available:
		ArrayList<String> moves;
		if(curMaxDepth == 2) {
			moves = board.getFullMovesWith2MovesAwayFilters(mustBe100percentMesonEfficient);
			
		} else {
			moves = board.getNecessaryFullMovesToCheck(mustBe100percentMesonEfficient);
		}
		
		moves = PositonFilterTests.excludeMovesThatLeadToSameOutcome(board, moves);
			
		for(int i=0; i<moves.size(); i++) {
			getBestMoveList(board.doOneMove(moves.get(i)), curMaxDepth - 1);
		}
		
		return;
	}
	
}
