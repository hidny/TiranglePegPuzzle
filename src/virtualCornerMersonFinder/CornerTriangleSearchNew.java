package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import triangleBoard5.PositonFilterTests;


//TODO: TEST!
//At least it's only 200 lines...

public class CornerTriangleSearchNew {

	public static int INITIAL_NUM_MOVES_CUTOFF = 20;
	
	public static Queue<Long> foundCornerBoardQueueArray[][];
	public static HashSet<Long> foundCornerBoardsArray[][];
	public static int currentNumMovesCutoff = INITIAL_NUM_MOVES_CUTOFF;
	

	public static int debugNumAdded = 0;
	public static int debugNumDuplicateDeleted = 0;
	
	public static ArrayList<MersonMoveAndMovePossibility> search(CornerTriangleBoard start) {
		
		
		int numLayersInit = start.getNumLayers();
		
		//TODO: compare min to num moves at end...
		
		
		foundCornerBoardQueueArray = new Queue[INITIAL_NUM_MOVES_CUTOFF][INITIAL_NUM_MOVES_CUTOFF];
		foundCornerBoardsArray = new HashSet[INITIAL_NUM_MOVES_CUTOFF][INITIAL_NUM_MOVES_CUTOFF];

		for(int i=0; i<INITIAL_NUM_MOVES_CUTOFF; i++) {
			for(int j=0; j<INITIAL_NUM_MOVES_CUTOFF; j++) {
				foundCornerBoardQueueArray[i][j] = new LinkedList<Long>();
				foundCornerBoardsArray[i][j] = new HashSet<Long>();

			}
		}
		
		foundCornerBoardQueueArray[0][0].add(start.getLookupNumber());
		foundCornerBoardsArray[0][0].add(start.getLookupNumber());
		
		ArrayList<MersonMoveAndMovePossibility> ret = new ArrayList<MersonMoveAndMovePossibility>();
		
		for(int slack = 0; slack<currentNumMovesCutoff; slack++) {
			
			if(foundCornerBoardQueueArray[0][slack].isEmpty() == false) {
				break;
			}
			
			for(int i=0; i<currentNumMovesCutoff; i++) {
				
				if(foundCornerBoardQueueArray[i][i+slack].isEmpty() == false) {
					ArrayList<MersonMoveAndMovePossibility> tmp = search(i, i+slack, numLayersInit);
					
					if(tmp.isEmpty() == false) {
						ret.addAll(tmp);
					}
					
				} else {
					break;
				}
			}
		}
		

		//Compare answer to min merson moves:
		//(Prediction: minNumMersonMoves in search always = minNumMerson in getNumMesonRegionsSimple)
		int minNumMerson = PositonFilterTests.getNumMesonRegionsSimple(start.getCornerTriangle());
		if(ret.get(0).getNumMersonMoves() >  minNumMerson) {
			System.out.println("FOUND that the minNumMerson is lower than the min merson moves found with search!");
			System.out.println(start);
			
			System.exit(1);
		}
		
		return ret;
	}


	public static ArrayList<MersonMoveAndMovePossibility> search(int numMerson, int numMoves, int numLayers) {
	
		if(numMoves == INITIAL_NUM_MOVES_CUTOFF) {
			System.out.println("GIVE UP because numMoves == INITIAL_NUM_MOVES_CUTOFF");
			System.exit(1);
		}

		ArrayList<MersonMoveAndMovePossibility> ret = new ArrayList<MersonMoveAndMovePossibility>();
		
		System.out.println("Start search where numMerson = " + numMoves + " and numMoves = " + numMoves);

		while(foundCornerBoardQueueArray[numMerson][numMoves].isEmpty() == false) {

			CornerTriangleBoard current = new CornerTriangleBoard(numLayers, foundCornerBoardQueueArray[numMerson][numMoves].remove());

			//SANITY TEST
			boolean alreadyFoundPosition1 = false;
			long lookup1 = current.getLookupNumber();
			
			SEARCH_POS_ALREADY_FOUND_SANITY:
			for(int numMerson2=0; numMerson2<=numMerson; numMerson2++) {
				for(int numMoves2=0; numMoves2<=numMoves; numMoves2++) {
					if(foundCornerBoardsArray[numMerson2][numMoves2].contains(lookup1)) {
						alreadyFoundPosition1 = true;
						break SEARCH_POS_ALREADY_FOUND_SANITY;
					}
				}
			}

			if(alreadyFoundPosition1) {
				System.out.println("ERROR: put duplicate position into the queue!");
				System.exit(1);
				continue;
			}
			//END SANITY TEST
			 

			ArrayList <String>moves = current.getFullMovesExcludingRepeatMoves();

			for(int i=0; i<moves.size(); i++) {
				
				CornerTriangleBoard tmpNextMove = current.doOneMove(moves.get(i));
				
				//System.out.println(tmpNextMove);
				
				if(tmpNextMove.getNumPiecesLeft() == 0
						|| (tmpNextMove.getNumPiecesLeft() == 1
								&& tmpNextMove.arePegsOutsidelayers() == false
								)) {
					
					int numMersonMoves = tmpNextMove.getNumMovesMadeStartingFromInside();

					
					currentNumMovesCutoff = tmpNextMove.getNumMovesMade();
					
					//System.out.println("Advanced search:");
					//System.out.println("Found move list:");
					//System.out.println(tmpNextMove);
					//System.out.println("New numMoves cut off: " + numMovesCutoff);
					
					ret.add(new MersonMoveAndMovePossibility(numMersonMoves, tmpNextMove.getNumMovesMade()));
					
					//System.out.println("Press enter to get next solution");
					//in.nextLine();
					
					//TODO: also use this...
					//a.getNumMovesMade();
					
					//TODO: get min combos of a.getNumMovesMade(); and a.getNumMovesMadeStartingFromInside();
				
				}
				
				int numMersonMovesNextMove = tmpNextMove.getNumMovesMadeStartingFromInside();
				int numMovesMadeNextMove = numMoves + 1;
				long lookupNextMove = tmpNextMove.getLookupNumber();
				
				if(numMersonMovesNextMove > numMerson + 1) {
					System.out.println("ERROR: unexpected number of merson moves in advanced search!");
					System.exit(1);
				}
				
				
				boolean alreadyFoundPosition2 = false;
				
				SEARCH_POS_ALREADY_FOUND:
				for(int numMerson2=0; numMerson2<=numMersonMovesNextMove; numMerson2++) {
					for(int numMoves2=0; numMoves2<=numMovesMadeNextMove; numMoves2++) {
						if(foundCornerBoardsArray[numMerson2][numMoves2].contains(lookupNextMove)) {
							alreadyFoundPosition2 = true;
							break SEARCH_POS_ALREADY_FOUND;
						}
					}
				}

				if(alreadyFoundPosition2 == false) {
					//TODO: it won't be as simple if we want to minimize both merson and num moves...

					foundCornerBoardsArray[numMersonMovesNextMove][numMovesMadeNextMove].add(lookupNextMove);
					foundCornerBoardQueueArray[numMersonMovesNextMove][numMovesMadeNextMove].add(lookupNextMove);

					debugNumAdded++;
					if(debugNumAdded % 1000 == 0) {
						System.out.println("Num added to queue: " + debugNumAdded);
						System.out.println("Num duplicate deleted from lookup: " + debugNumDuplicateDeleted);
					}
					
				}
			} //END FOR EACH MOVE

		
		} //END FOR EACH POS WITH SPECIFIC MESON #
			
		System.out.println("End search in where num Merson moves = " + numMerson + " and num Moves = " + numMoves);

		return ret;

	}
	
	
}
