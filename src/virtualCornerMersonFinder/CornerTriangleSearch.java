package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CornerTriangleSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int LAYERS = 6;
		
		System.out.println("Trying " + LAYERS + " full layers with a move cut-off of " + INITIAL_NUM_MOVES_CUTOFF);
		CornerTriangleBoard a = new CornerTriangleBoard(LAYERS);
		
		//4 full:
		//Num moves Made: 5
		//Num moves Made from inside: 3
		
		//And:
		//Num moves Made: 4
		//Num moves Made from inside: 4

		
		search(a);
	}


	public static Scanner in = new Scanner(System.in);
	
	public static Queue<CornerTriangleBoard> listArray[];
	
	public static HashSet<Long> foundCornerBoardsArray[];

	public static int ARRAY_LENGTH = 20;
	
	public static int debugNumAdded = 0;
	public static int debugNumDuplicateDeleted = 0;
	
	public static int INITIAL_NUM_MOVES_CUTOFF = 20;
	
	//TODO: organize logic:
	//Breadth-First Search
	public static void search(CornerTriangleBoard start) {
		
		//Initialize arrays
		listArray = new Queue[ARRAY_LENGTH];
		foundCornerBoardsArray = new HashSet[ARRAY_LENGTH];
		for(int i=0; i<ARRAY_LENGTH; i++) {
			listArray[i] = new LinkedList<CornerTriangleBoard>();
			foundCornerBoardsArray[i] = new HashSet<Long>();
		}
		//END initlalize arrays
		
		listArray[0].add(start);
		
		//TODO: maybe stedily increment this value??
		//int numMovesCutoff = Integer.MAX_VALUE;
		int numMovesCutoff = INITIAL_NUM_MOVES_CUTOFF;
		
		for(int m=0; m<ARRAY_LENGTH && m < numMovesCutoff; m++) {
			
			System.out.println("m = " + m);
			
			NEXT_POS_IN_QUEUE:
			while(listArray[m].isEmpty() == false) {
	
				CornerTriangleBoard current = listArray[m].remove();
	
				if(current.getNumMovesMade() + 1 >= numMovesCutoff) {
					continue NEXT_POS_IN_QUEUE;
				}
				
				boolean alreadyFoundPosition1 = false;
				long lookup1 = current.getLookupNumber();
				for(int m2=0; m2<m; m2++) {
					if(foundCornerBoardsArray[m2].contains(lookup1)) {
						alreadyFoundPosition1 = true;
						break;
					}
				}
				if(alreadyFoundPosition1) {
					if(foundCornerBoardsArray[m].contains(lookup1) == false) {
						System.out.println("ERROR: expected new pos in queue to also be in foundCornerBoardsArray[m]");
						System.exit(1);
					}
					foundCornerBoardsArray[m].remove(lookup1);
					debugNumDuplicateDeleted++;
					continue;
				}

				
				ArrayList <String>moves = current.getFullMovesExcludingRepeatMoves();
	
				for(int i=0; i<moves.size(); i++) {

					if(current.getNumMovesMade() + 1 >= numMovesCutoff) {
						continue NEXT_POS_IN_QUEUE;
					}
					
					CornerTriangleBoard tmpNextMove = current.doOneMove(moves.get(i));
					
					
					if(tmpNextMove.getNumPiecesLeft() == 0
							|| (tmpNextMove.getNumPiecesLeft() == 1
							         && tmpNextMove.lastMoveLandsInside()//TODO: boolean to check if all pegs were moves
									)) {
						
						int numMersonMoves = tmpNextMove.getNumMovesMadeStartingFromInside();

						System.out.println("Advanced search:");
						System.out.println("Found move list:");
						System.out.println(tmpNextMove);
						
						numMovesCutoff = tmpNextMove.getNumMovesMade();
						System.out.println("New numMoves cut off: " + numMovesCutoff);
						
						System.out.println("Press enter to get next solution");
						in.nextLine();
						
						//TODO: also use this...
						//a.getNumMovesMade();
						
						//TODO: get min combos of a.getNumMovesMade(); and a.getNumMovesMadeStartingFromInside();
					
					}
					
					int numMersonMoves = tmpNextMove.getNumMovesMadeStartingFromInside();
					
					long lookup2 = tmpNextMove.getLookupNumber();
					
					if(numMersonMoves > m + 1) {
						System.out.println("ERROR: unexpected number of merson moves in advanced search!");
						System.exit(1);
					}
					
					boolean alreadyFoundPosition2 = false;
					for(int m2=0; m2<=numMersonMoves; m2++) {
						if(foundCornerBoardsArray[m2].contains(lookup2)) {
							alreadyFoundPosition2 = true;
							break;
						}
					}
					
					if(alreadyFoundPosition2 == false) {
						//TODO: it won't be as simple if we want to minimize both merson and num moves...
	
						foundCornerBoardsArray[numMersonMoves].add(lookup2);
						listArray[numMersonMoves].add(tmpNextMove);

						debugNumAdded++;
						if(debugNumAdded % 1000 == 0) {
							System.out.println("Num added to queue: " + debugNumAdded);
							System.out.println("Num duplicate deleted from lookup: " + debugNumDuplicateDeleted);
						}
						
					}
				} //END FOR EACH MOVE

			
			} //END FOR EACH POS WITH SPECIFIC MESON #
			
		} //END EACH MESON #
		
		System.out.println("End search");
	}
	

}
