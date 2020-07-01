package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import triangleBoard5.PositonFilterTests;
import virtualCornerMersonFinder.caseClasses.MersonMoveAndMovePossibility;


//TODO: TEST!
//At least it's only 200 lines...

public class CornerTriangleSearchNew2 {

	
	public static void main(String args[]) {
		
		int NUM_LAYERS=4;
		
		for(long lookup=0; lookup< 1000; lookup++) {
			CornerTriangleBoard tmp = new CornerTriangleBoard(NUM_LAYERS, lookup);
			
			System.out.println("Trying new Corner Triangle:");
			System.out.println(tmp);
			ArrayList<MersonMoveAndMovePossibility> ret = search(tmp);
			

			System.out.println("Answers found:");
			for(int i=0; i<ret.size(); i++) {
				System.out.println("(num Merson, num Moves) = (" + ret.get(i).getNumMersonMoves() + ", " + ret.get(i).getNumMoves() + ")");
			}
			
		}
		
		
	}
	
	public static void testFilled5() {
		CornerTriangleBoard filled = new CornerTriangleBoard(5);
		
		System.out.println(filled);
		
		ArrayList<MersonMoveAndMovePossibility> ret = search(filled);
		
		System.out.println("Answers found:");
		for(int i=0; i<ret.size(); i++) {
			System.out.println("(num Merson, num Moves) = (" + ret.get(i).getNumMersonMoves() + ", " + ret.get(i).getNumMoves() + ")");
		}
		
		//Answer for new CornerTriangleBoard(3):
		//(num Merson, num Moves) = (3, 4)
		
		//Answer for new CornerTriangleBoard(1):
		//(num Merson, num Moves) = (1, 2) (what?)
	}

	public static int INITIAL_NUM_MOVES_CUTOFF = 20;
	
	public static Queue<Long> foundCornerBoardQueueArray[][];
	
	public static HashSet<Long> foundCornerBoardsArray[][];
	public static HashSet<Long> foundCornerBoards;
	
	public static int currentNumMovesCutoff = INITIAL_NUM_MOVES_CUTOFF;
	

	public static int debugNumAdded = 0;
	public static int debugNumDeleted = 0;
	
	public static ArrayList<MersonMoveAndMovePossibility> search(CornerTriangleBoard start) {
		
		
		int numLayersInit = start.getNumLayers();
		int minNumMerson = PositonFilterTests.getNumMesonRegionsSimple(start.getCornerTriangle());
		
		//TODO: compare min to num moves at end...

		foundCornerBoardQueueArray = new Queue[INITIAL_NUM_MOVES_CUTOFF][INITIAL_NUM_MOVES_CUTOFF];
		foundCornerBoardsArray = new HashSet[INITIAL_NUM_MOVES_CUTOFF][INITIAL_NUM_MOVES_CUTOFF];
		foundCornerBoards = new HashSet<Long>();
		
		for(int i=0; i<INITIAL_NUM_MOVES_CUTOFF; i++) {
			for(int j=0; j<INITIAL_NUM_MOVES_CUTOFF; j++) {
				foundCornerBoardQueueArray[i][j] = new LinkedList<Long>();
				foundCornerBoardsArray[i][j] = new HashSet<Long>();

			}
		}
		
		foundCornerBoardQueueArray[0][0].add(start.getLookupNumber());
		foundCornerBoardsArray[0][0].add(start.getLookupNumber());
		foundCornerBoards.add(start.getLookupNumber());
		
		ArrayList<MersonMoveAndMovePossibility> ret = new ArrayList<MersonMoveAndMovePossibility>();
		
		
		//1st possible row:
		FIND_BEST_MERSON:
		for(int j=0; j<currentNumMovesCutoff; j++) {
			for(int i=j; i<=minNumMerson; i++) {

				if(foundCornerBoardQueueArray[i][j].isEmpty() == false) {
					MersonMoveAndMovePossibility searchResult = search(i, j, numLayersInit);
					
					if(searchResult != null) {
						ret.add(searchResult);
						break FIND_BEST_MERSON;
					}
					
				}
			}
		}
		
		if(ret.isEmpty()) {
			System.out.println("Couldn\'t solve using the min merson moves!");
			
		}
		
		//Find secondary non-merson optimal solutions (less good merson moves, but good number of moves)
		for(int i=minNumMerson+1; i<currentNumMovesCutoff; i++) {
			for(int j=i; j<currentNumMovesCutoff; j++) {
				if(foundCornerBoardQueueArray[i][j].isEmpty() == false) {
					MersonMoveAndMovePossibility searchResult = search(i, j, numLayersInit);
					
					if(searchResult != null) {
						ret.add(searchResult);
						break;
					}
					
				}
			}
		}
		
		//Compare answer to min merson moves:
		//(Prediction: minNumMersonMoves in search always = minNumMerson in getNumMesonRegionsSimple)
		if(ret.get(0).getNumMersonMoves() >  minNumMerson) {
			System.out.println("FOUND that the minNumMerson is lower than the min merson moves found with search!");
			System.out.println(start);
			
			System.exit(1);
		}
		
		return ret;
	}


	public static MersonMoveAndMovePossibility search(int numMerson, int numMoves, int numLayers) {
	
		if(numMoves == INITIAL_NUM_MOVES_CUTOFF) {
			System.out.println("GIVE UP because numMoves == INITIAL_NUM_MOVES_CUTOFF");
			System.exit(1);
		}

		//Try to see if there's a solution already within the queue:
		MersonMoveAndMovePossibility ret = searchForSolutionInQueue(numMerson, numMoves, numLayers);

		if(ret != null) {
			return ret;
		}
		
		
		System.out.println("Start search where numMerson = " + numMoves + " and numMoves = " + numMoves);

		FOUND_POSSIBILITY:
		while(foundCornerBoardQueueArray[numMerson][numMoves].isEmpty() == false) {

			CornerTriangleBoard current = new CornerTriangleBoard(numLayers, foundCornerBoardQueueArray[numMerson][numMoves].remove());
			debugNumDeleted++;

			
			boolean alreadyFoundPosition1 = false;
			
			//SANITY TEST VARIABLE
			boolean acceptableCase = false;
			//END SANITY TEST VARIABLE
			
			long lookup1 = current.getLookupNumber();

			//DEBUG
			if(lookup1 == 32769) {
				System.out.println("DEBUG:");
				System.out.println(current);
				System.out.println("END DEBUG");
			}
			//END DEBUG
			
			if(foundCornerBoards.contains(lookup1)) {

				SEARCH_POS_ALREADY_FOUND:
				for(int numMerson2=0; numMerson2<=numMerson; numMerson2++) {
					for(int numMoves2=0; numMoves2<=numMoves; numMoves2++) {
	
						//Avoid finding the position we just dequeued:
						if(numMerson2 == numMerson && numMoves2 == numMoves) {
							continue;
						}
	
						if(foundCornerBoardsArray[numMerson2][numMoves2].contains(lookup1)) {
							
							alreadyFoundPosition1 = true;
	
							if((numMerson2 == numMerson-1 && numMoves2 == numMoves) || numMerson2 == numMerson) {
								acceptableCase = true;
							} else {
								acceptableCase = false;
								break SEARCH_POS_ALREADY_FOUND;
							}
						}
					}
				}
	
				//Using acceptableCase == true, means I'm doing a sanity check:
				if(alreadyFoundPosition1 && acceptableCase == true) {
					//System.out.println("DEBUG: found acceptable case where there's a duplicate position in the queue");
					continue;
	
				
				} else if(alreadyFoundPosition1 && acceptableCase == false) {
					System.out.println("ERROR: put duplicate position into the queue in a way that should not happen!");
					System.out.println(current);
					System.exit(1);
				}
				//END SANITY TEST
			}

			ArrayList <String>moves = current.getFullMovesExcludingRepeatMoves();

			for(int i=0; i<moves.size(); i++) {
				
				CornerTriangleBoard tmpNextMove = current.doOneMove(moves.get(i));
				
				//System.out.println(tmpNextMove);
				
				int numMersonMovesNextMove = numMerson;
				if(tmpNextMove.getNumMovesMadeStartingFromInside() == 1) {
					numMersonMovesNextMove = numMerson + 1;

				} else if(tmpNextMove.getNumMovesMadeStartingFromInside() > 1
						|| tmpNextMove.getNumMovesMadeStartingFromInside() < 0) {
					System.out.println("ERROR: output of getNumMovesMadeStartingFromInside does not make sense in CornerTriangleSearchNew");
					System.exit(1);

				}
	
				int numMovesMadeNextMove = numMoves + 1;


				if(tmpNextMove.getNumPiecesLeft() == 0
						|| (tmpNextMove.getNumPiecesLeft() == 1
								&& tmpNextMove.arePegsOutsidelayers() == false
								)) {

					//At this point, we found a solution, but it might not be optimal

					if(numMersonMovesNextMove > numMerson) {
						//Keep searching for a more optimal move that isn't merson (doesn't start from inside the structure)
						
					} else {
						
						//Celebrate!

						//System.out.println("Found move list:");
						//System.out.println(tmpNextMove);
						
						currentNumMovesCutoff = numMovesMadeNextMove;

						//System.out.println("New numMoves cut off: " + numMersonMovesNextMove);
						
						ret =new MersonMoveAndMovePossibility(numMersonMovesNextMove, numMovesMadeNextMove);

						//System.out.println("Press enter to get next solution");
						//in.nextLine();

						break FOUND_POSSIBILITY;
					}
				
				}
				
				
				long lookupNextMove = tmpNextMove.getLookupNumber();
				boolean alreadyFoundPosition2 = false;

				if(foundCornerBoards.contains(lookupNextMove)) {
	
					SEARCH_POS_ALREADY_FOUND:
					for(int numMerson2=0; numMerson2<=numMersonMovesNextMove; numMerson2++) {
						for(int numMoves2=0; numMoves2<=numMovesMadeNextMove; numMoves2++) {
							if(foundCornerBoardsArray[numMerson2][numMoves2].contains(lookupNextMove)) {
								alreadyFoundPosition2 = true;
								break SEARCH_POS_ALREADY_FOUND;
							}
						}
					}
				}

				if(alreadyFoundPosition2 == false) {
					//TODO: it won't be as simple if we want to minimize both merson and num moves...

					foundCornerBoardsArray[numMersonMovesNextMove][numMovesMadeNextMove].add(lookupNextMove);
					foundCornerBoardQueueArray[numMersonMovesNextMove][numMovesMadeNextMove].add(lookupNextMove);
					foundCornerBoards.add(lookupNextMove);

					//DEBUG
					if(lookupNextMove == 32769) {
						System.out.println("--------------------");
						System.out.println("DEBUG current move:");
						System.out.println("   (num Merson, num Moves) = (" + numMerson + ", " + numMoves + ")");
						System.out.println(current);
						
						System.out.println("DEBUG next move:");
						System.out.println("   (num next Merson, num next Moves) = (" + numMersonMovesNextMove + ", " + numMovesMadeNextMove + ")");
						System.out.println(tmpNextMove);
						System.out.println("END DEBUG next move");
						System.out.println("--------------------");
					}
					//END DEBUG
					
					debugNumAdded++;
					if(debugNumAdded % 10000 == 0) {
						System.out.println("Num added to queue: " + debugNumAdded);
						System.out.println("Num duplicate deleted from queue: " + debugNumDeleted);
					}
					
				}

			} //END FOR EACH MOVE

		
		} //END FOR EACH POS WITH SPECIFIC MESON #
			
		System.out.println("End search in where num Merson moves = " + numMerson + " and num Moves = " + numMoves);

		return ret;

	}
	
	
	//TODO: this is probably not needed, but it doesn't hurt so whatever
	private static MersonMoveAndMovePossibility searchForSolutionInQueue(int numMerson, int numMoves, int numLayers) {

		if(numMoves == INITIAL_NUM_MOVES_CUTOFF) {
			System.out.println("GIVE UP because numMoves == INITIAL_NUM_MOVES_CUTOFF");
			System.exit(1);
		}

		MersonMoveAndMovePossibility ret = null;
		
		System.out.println("Start search solution in queue where numMerson = " + numMerson + " and numMoves = " + numMoves);
		
		//new queue because this algo unfortunately destroys the original queue...
		Queue<Long> reversedCopyOfQueue= new LinkedList<Long>();
		
		FOUND_POSSIBILITY:
		while(foundCornerBoardQueueArray[numMerson][numMoves].isEmpty() == false) {

			long currentLookup = foundCornerBoardQueueArray[numMerson][numMoves].remove();
			reversedCopyOfQueue.add(currentLookup);
			CornerTriangleBoard current = new CornerTriangleBoard(numLayers, currentLookup);
			
			//TODO: make function out of condition
			if(current.getNumPiecesLeft() == 0
					|| (current.getNumPiecesLeft() == 1
							&& current.arePegsOutsidelayers() == false
							)) {
			//END TODO
				currentNumMovesCutoff = numMerson;

				//System.out.println("New numMoves cut off: " + numMersonMovesNextMove);
				
				ret =new MersonMoveAndMovePossibility(numMerson, numMoves);

				//System.out.println("Press enter to get next solution");
				//in.nextLine();

				break FOUND_POSSIBILITY;
			}
			
		}
		
		foundCornerBoardQueueArray[numMerson][numMoves] = reversedCopyOfQueue;
		
		System.out.println("End search solution in queue where numMerson = " + numMoves + " and numMoves = " + numMoves);
		
		
		return ret;
	}

	
}
