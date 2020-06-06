package eulerBook;

public class wolframSum {

	public static void main(String args[]) {
	Fraction sum = Fraction.ZERO;
	
	//Fraction temp1;
	Fraction temp2 = null;
	
	//expecting log(4)
	
		for(long j=1; j<=10000; j++) {
			temp2 = new Fraction(1, 2*j*j -j);
			
			sum = Fraction.plus(sum, temp2);
			System.out.println("Sum: " + sum.getDecimalFormat(15));
			
		}
		System.out.println("Sum: " + sum.getDecimalFormat(15));
	
	
}
}
