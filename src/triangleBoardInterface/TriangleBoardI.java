package triangleBoardInterface;

public interface TriangleBoardI {

	public TriangleBoardI doOneMove(String move);
	
	public boolean[][] getTriangle();
	
	public int getNumPiecesLeft();
	
	public void draw();
	
	public int getNumMovesMade();
}
