package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import triangleBoard5.PositonFilterTests;
import triangleBoard5.utilFunctions;

public class CornerTriangleSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		testFilledLayer(5);
		
	}
	
	public static void testFilledLayer(int numLayers) {
		
		System.out.println("Trying " + numLayers + " full layers with a move cut-off of " + INITIAL_NUM_MOVES_CUTOFF);
		CornerTriangleBoard board = new CornerTriangleBoard(numLayers);
		
		int a[] = searchOld(board);
		
		for(int i=0; i<a.length; i += 2) {
			System.out.println("In full layers: We could do "  + a[i] + " merson move(s) with " + a[i+1] + " move(s).");
		}
		System.out.println("-------------------------------------------");
		//4 full:
		//Num moves Made: 5
		//Num moves Made from inside: 3
		
		//And:
		//Num moves Made: 4
		//Num moves Made from inside: 4
	}
	
	public static void testWholeLayer(int numLayers) {
		
		for(int j=0; j<Math.pow(2, utilFunctions.getTriangleNumber(numLayers)); j++) {
			CornerTriangleBoard test = new CornerTriangleBoard(numLayers, j);
			
			System.out.println(test);
			
			int a[] = searchOld(test);
			
			for(int i=0; i<a.length; i += 2) {
				System.out.println("We could do "  + a[i] + " merson move(s) with " + a[i+1] + " move(s).");
			}
			System.out.println("-------------------------------------------");
		}
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
	public static int[] searchOld(CornerTriangleBoard start) {
		
		if(start.getLookupNumber() == 0) {
			return new int[] {0, 0};
		}
		
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
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
					
					//System.out.println(tmpNextMove);
					
					if(tmpNextMove.getNumPiecesLeft() == 0
							|| (tmpNextMove.getNumPiecesLeft() == 1
									&& tmpNextMove.arePegsOutsidelayers() == false
									)) {
						
						int numMersonMoves = tmpNextMove.getNumMovesMadeStartingFromInside();

						
						numMovesCutoff = tmpNextMove.getNumMovesMade();
						
						//System.out.println("Advanced search:");
						//System.out.println("Found move list:");
						//System.out.println(tmpNextMove);
						//System.out.println("New numMoves cut off: " + numMovesCutoff);
						

						ret.add(numMersonMoves);
						ret.add(tmpNextMove.getNumMovesMade());
						
						//System.out.println("Press enter to get next solution");
						//in.nextLine();
						
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
		
		if(ret.size() == 0) {
			System.out.println("ERROR: Didn't find any answers!");
			System.exit(1);
		}
		int retInt[] = new int[ret.size()];
		for(int i=0; i<retInt.length; i++) {
			retInt[i] = ret.get(i);
		}
		
		return retInt;
		
	}
	

}
