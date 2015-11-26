/*
 * 
 * @author symbol
 *
 */
public class Test {

	/*
	 * fangfa
	 * 
	 * @param firstF
	 * 
	 * @return int
	 */
	public static int add(int firstF) {
		for (int j = 0; j < 2; j++) {
			if (j == 1)
				break;
			if (j != 1)
				return 0;
			else
				return 1;
		}
		return 0;
	}

	public static void main(String[] args) {

		System.out.println(add(1) + "");

	}

}
