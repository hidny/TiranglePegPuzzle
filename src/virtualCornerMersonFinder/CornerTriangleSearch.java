package virtualCornerMersonFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CornerTriangleSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CornerTriangleBoard a = new CornerTriangleBoard(2);
		
		search(a);
	}

	//TODO: make a list of queues based on number of starting from inside
	//TODO: public static Queue<CornerTriangleBoard> list[] = new LinkedList[10];

	public static Queue<CornerTriangleBoard> list = new LinkedList<CornerTriangleBoard>();
	
	public static HashSet<Long> foundCornerBoards = new HashSet<Long>();

	public static Scanner in = new Scanner(System.in);
	
	//Breadth-First Search
	public static void search(CornerTriangleBoard start) {
		
		
		list.add(start);
		
		while(list.isEmpty() == false) {

			CornerTriangleBoard current = list.remove();
			ArrayList <String>moves = current.getFullMovesExcludingRepeatMoves();

			for(int i=0; i<moves.size(); i++) {
				
				CornerTriangleBoard tmpNextMove = current.doOneMove(moves.get(i));
				
				
				if(tmpNextMove.getNumPiecesLeft() == 0
						|| (tmpNextMove.getNumPiecesLeft() == 1 && tmpNextMove.isUsedOutsidePegs() //TODO: boolean to check if all pegs were moves
								)) {
					
					int numMersonMoves = tmpNextMove.getNumMovesMadeStartingFromInside();
					
					System.out.println("Found move list:");
					System.out.println(tmpNextMove);
					
					System.out.println("Press enter to get next solution");
					in.nextLine();
					
					//TODO: also use this...
					//a.getNumMovesMade();
					
					//TODO: get min combos of a.getNumMovesMade(); and a.getNumMovesMadeStartingFromInside();
				
				}
				
				long lookup = tmpNextMove.getLookupNumber();
				if(foundCornerBoards.contains(lookup) == false) {
					//TODO: it won't be as simple if we want to minimize both merson and num moves...

					foundCornerBoards.add(lookup);
					list.add(tmpNextMove);
					
					
				}
			}
			
			
			
		}
	}

}
