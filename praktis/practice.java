package praktis;
import java.util.Scanner;
public class practice {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Input an amount: ");
        int num = scan.nextInt();

        // Define an array with available denominations in DESC order
        int[] denominations = {1000, 500, 200, 100, 50, 20, 10, 5, 1};

        // Create an array to store the count for each denominations
        int[] breakdown = new int[denominations.length];

        //iterate over each domination.
        for (int i = 0; i< denominations.length; i++) {
            // Calculate how many times the denominations fits into the amount.
            breakdown[i] = num / denominations[i];
            //Update the amount to the remainder.
            num = num % denominations[i];
        }
        // print the result in the desired format
        System.out.println("\nOutput");
        for (int i = 0; i < denominations.length; i++) {
            System.out.println(denominations[i] + "-" + breakdown[i]);
        }
        scan.close();

    }
}
