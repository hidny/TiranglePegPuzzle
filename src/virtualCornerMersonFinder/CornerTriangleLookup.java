package virtualCornerMersonFinder;

public class CornerTriangleLookup {

	//TODO: maybe make it reflectively symmetric???
	//nah...

	//Simple algo impossible to mess up lookup unless num element greater than 63
	public static long convertToNumberSimple(boolean triangle[][], boolean pegsOutsidelayers) {
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
		
		if(pegsOutsidelayers) {
			ret += curNum;
		}
		
		return ret;
	}
}
