package triangleBoard5;

import UtilityFunctions.UtilityFunctions;

public class TriangleLookup {

	public static long pascalsTriangle[][] = UtilityFunctions.createPascalTriangle(100);

	public static long convertToNumberWithComboTricksAndSymmetry(boolean triangle[][], int numPiecesLeft) {
		long ret = 0;
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[i][j]) {
					
					if(curIndex >= numFocusedFound) {
						ret += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		if(numFocusedFound != 0) {
			System.out.println("ERROR: incorrect number of pieces left given to convertToNumberWithComboTricksAndSymmetry function");
			System.exit(1);
		}
		
		//Reflection:
		curIndex= startIndex;
		numFocusedFound = numPiecesLeft;

		int LAST_INDEX = triangle.length - 1;
		
		long reflection = 0;

		for(int i=0; i<triangle.length && reflection < ret; i++) {
			for(int j=0; j<triangle[i].length && reflection < ret; j++) {
				curIndex--;
				
				if(triangle[i][i - j]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		
		ret = Math.min(ret, reflection);
		
		//Rotation Clockwise:
		curIndex= startIndex;
		numFocusedFound = numPiecesLeft;
		
		long rotClockwise = 0;

		
		for(int i=0; i<triangle.length && rotClockwise < ret; i++) {
			for(int j=0; j<triangle[i].length && rotClockwise < ret; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX-i+j][LAST_INDEX-i]) {
					
					if(curIndex >= numFocusedFound) {
						rotClockwise += pascalsTriangle[curIndex][numFocusedFound];

					}
					numFocusedFound--;
				}
			}
		}
		
		ret = Math.min(ret, rotClockwise);

		//Rotation Clockwise:with reflection:
		curIndex= startIndex;
		numFocusedFound = numPiecesLeft;
		
		long rotClockwiseReflection = 0;

		for(int i=0; i<triangle.length && rotClockwiseReflection < ret; i++) {
			for(int j=0; j<triangle[i].length && rotClockwiseReflection < ret; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX-j][LAST_INDEX-i]) {
					
					if(curIndex >= numFocusedFound) {
						rotClockwiseReflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		
		ret = Math.min(ret, rotClockwiseReflection);
		
		//Rotation Counter-Clockwise:
		curIndex= startIndex;
		numFocusedFound = numPiecesLeft;
		
		long rotCounterClockwise = 0;

		
		for(int i=0; i<triangle.length && rotCounterClockwise < ret; i++) {
			for(int j=0; j<triangle[i].length && rotCounterClockwise < ret; j++) {

				curIndex--;
				
				if(triangle[LAST_INDEX - j][i - j]) {
					
					if(curIndex >= numFocusedFound) {
						rotCounterClockwise += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}

		ret = Math.min(ret, rotCounterClockwise);

		//Rotation Counter-Clockwise with reflection:
		curIndex= startIndex;
		numFocusedFound = numPiecesLeft;
		
		long rotCounterClockwiseReflection = 0;

		for(int i=0; i<triangle.length && rotCounterClockwiseReflection < ret; i++) {
			for(int j=0; j<triangle[i].length && rotCounterClockwiseReflection < ret; j++) {
					
				curIndex--;
				
				if(triangle[LAST_INDEX -i + j][j]) {
				
					if(curIndex >= numFocusedFound) {
						rotCounterClockwiseReflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		
		ret = Math.min(ret, rotCounterClockwiseReflection);
		
		
		return ret;
	}
	
	//START TEST FUNCTIONS
	

	public static long TESTconvertToNumberWithComboTricksReverse(boolean triangle[][], int numPiecesLeft) {
		long ret = 0;

		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				
				curIndex--;
				if(triangle[i][j]) {
					
					if(curIndex >= numFocusedFound) {
						ret += pascalsTriangle[curIndex][numFocusedFound];
					}

					numFocusedFound--;
				}
			}
		}
		
		//I don't want to take advantage of symmetries all of the time, but I could do it at the beginning at least:
		//if(numFocusedFound > utilFunctions.getTriangleNumber(triangle.length) - 7) {
		//	ret = Math.min(ret, getComboNumAfterReflection(triangle));
		//}
		
		return ret;
	}
	
	public static long TESTconvertToNumberWithComboTricksReflection(boolean triangle[][], int numPiecesLeft) {
		
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		long reflection = 0;

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[i][i - j]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		return reflection;
	}
	

	public static long TESTconvertToNumberWithComboTricksClockwise(boolean triangle[][], int numPiecesLeft) {
		
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int LAST_INDEX = triangle.length - 1;
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		long reflection = 0;

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX-i+j][LAST_INDEX-i]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		return reflection;
	}
	
	public static long TESTconvertToNumberWithComboTricksClockwiseReflection(boolean triangle[][], int numPiecesLeft) {
		
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int LAST_INDEX = triangle.length - 1;
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		long reflection = 0;

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX-j][LAST_INDEX-i]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		return reflection;
	}
	
	public static long TESTconvertToNumberWithComboTricksCounterClockwise(boolean triangle[][], int numPiecesLeft) {
		
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int LAST_INDEX = triangle.length - 1;
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		long reflection = 0;

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX - j][i - j]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		return reflection;
	}
	
	public static long TESTconvertToNumberWithComboTricksCounterClockwiseReflection(boolean triangle[][], int numPiecesLeft) {
		
		int startIndex = utilFunctions.getTriangleNumber(triangle.length);
		
		int LAST_INDEX = triangle.length - 1;
		int curIndex= startIndex;
		int numFocusedFound = numPiecesLeft;
		
		long reflection = 0;

		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				curIndex--;
				
				if(triangle[LAST_INDEX -i + j][j]) {
					
					if(curIndex >= numFocusedFound) {
						reflection += pascalsTriangle[curIndex][numFocusedFound];
					}
					numFocusedFound--;
				}
			}
		}
		return reflection;
	}
	
	public static void main(String args[]) { 
		System.out.println("Test combo");
		
		int LENGTH = 5;
		int NUM_TRIANGLE = utilFunctions.getTriangleNumber(LENGTH);
		int NUM_COINS = 4;
		int NUM_SYMMETRIES = 6;

		boolean combo[] = new boolean[NUM_TRIANGLE];
		
		
		boolean nums[][] = new boolean[6][150000000];
		
		
		for(int i=0; i<combo.length; i++) {
			if(i<NUM_COINS) {
				combo[i] = true;
			} else {
				combo[i] = false;
			}
		}
		
		int comboIndex = 0;
		while(combo != null) {
			
			int index = 0;
			boolean trig[][] = new boolean[LENGTH][];
			for(int i=0; i<trig.length; i++) {
				trig[i] = new boolean[i + 1];
				for(int j=0; j<=i; j++) {
					trig[i][j] = combo[index];
					index++;
				}
			}
			
			long num[] = new long[NUM_SYMMETRIES];
			num[0] = TriangleLookup.TESTconvertToNumberWithComboTricksReverse(trig, NUM_COINS);
			num[1] = TriangleLookup.TESTconvertToNumberWithComboTricksReflection(trig, NUM_COINS);
			num[2] = TriangleLookup.TESTconvertToNumberWithComboTricksClockwise(trig, NUM_COINS);
			num[3] = TriangleLookup.TESTconvertToNumberWithComboTricksClockwiseReflection(trig, NUM_COINS);
			num[4] = TriangleLookup.TESTconvertToNumberWithComboTricksCounterClockwise(trig, NUM_COINS);
			num[5] = TriangleLookup.TESTconvertToNumberWithComboTricksCounterClockwiseReflection(trig, NUM_COINS);

			long min = num[0];
			for(int i=0; i<NUM_SYMMETRIES; i++) {
				if(nums[i][(int)num[i]] == true) {
					System.out.println("ERROR 1!");
					System.exit(1);
				}
				nums[i][(int)num[i]] = true;
				
				if(num[i] < min) {
					min = num[i];
				}
				
				
			}
			
			
			if(min != TriangleLookup.convertToNumberWithComboTricksAndSymmetry(trig, NUM_COINS)) {

				System.out.println("ERROR 2!");
				System.exit(1);
			}
			
			comboIndex++;

			combo = UtilityFunctions.getNextCombination(combo);
			
			if(comboIndex % 100000 == 0) {
				System.out.println(comboIndex);
			}
		}
		

		for(int i=0; i<NUM_SYMMETRIES; i++) {
			for(int j=1; j<nums[i].length; j++) {
				if(nums[i][j-1] == false && nums[i][j] == true) {
					System.out.println("ERROR 3!");
					System.exit(1);
				}
			}
		}
		
		System.out.println("No errors!");
		
	}
	//END TEST FUNCTIONS
	
	
	//Simple algo impossible to mess up lookup unless num element greater than 63
	public static long convertToNumberSimple(boolean triangle[][]) {
		long ret = 0L;
		
		long curNum=1L;
		long mult = 2L;
		
		for(int i=0; i<triangle.length; i++) {
			for(int j=0; j<triangle[i].length; j++) {
				if(triangle[i][j]) {
					ret += curNum;
				}
				curNum *= mult;
			}
		}
		
		return ret;
	}
	
	
}
