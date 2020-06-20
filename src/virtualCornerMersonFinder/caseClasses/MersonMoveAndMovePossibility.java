package virtualCornerMersonFinder.caseClasses;

public class MersonMoveAndMovePossibility {

	public MersonMoveAndMovePossibility(int numMersonMoves, int numMoves) {
		super();
		this.numMersonMoves = numMersonMoves;
		this.numMoves = numMoves;
	}
	public int getNumMersonMoves() {
		return numMersonMoves;
	}
	public int getNumMoves() {
		return numMoves;
	}
	private int numMersonMoves;
	private int numMoves;
}
