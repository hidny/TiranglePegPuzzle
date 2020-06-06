package eulerBook;

public class eulerBook {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Fraction sum = Fraction.ZERO;
		
		Fraction temp1;
		Fraction temp2;
		
		
		for(long i=0; i<20; i++) {
			
			temp1 = new Fraction(1, (long)Math.pow(2, i+1));
			
			for(long j=1; j<=Math.pow(2, i); j++) {
				temp2 = new Fraction(1, 2*j*j -j);
				
				sum = Fraction.plus(sum, Fraction.mult(temp1,  temp2));
				
			}
			System.out.println("Sum: " + sum.getDecimalFormat(15));
		}
		
	}

}
