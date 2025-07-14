package pract;
import java.util.Scanner;

public class Fare {

    // Method to input passenger details
    public static String[] get_passenger_details() {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter passenger name: ");
        String name = scan.nextLine();

        System.out.print("Enter distance traveled (km): ");
        double distance = scan.nextDouble();

        System.out.print("Enter passenger type (S - Senior, P - PWD, T - Student, R - Regular): ");
        char type = scan.next().charAt(0);
        scan.close();

        return new String[] {name, String.valueOf(distance), String.valueOf(type)};
    }

    // Method to calculate total fare based on distance
    public static double calculate_fare(double distance) {
        double base_fare = 60.0;  // Base fare for first 2km
        double additional_fare = 7.50; // Additional fare per km after 2km
        double total_fare;

        if (distance <= 2) {
            total_fare = base_fare;
        } else {
            // Ensure additional distance is rounded up to the nearest km
            int extra_km = (int) Math.ceil(distance - 2);
            total_fare = base_fare + (extra_km * additional_fare);
        }

        return Math.round(total_fare * 100.0) / 100.0; // Rounding to 2 decimal places
    }

    // Method to calculate discount and generate an official receipt
    public static void generate_receipt(String name, double distance, char type) {
        double total_fare = calculate_fare(distance);
        double discount = 0;
        String passengerType = "";

        // Determine discount based on passenger type
        switch (Character.toUpperCase(type)) {
            case 'S':
                passengerType = "Senior";
                discount = total_fare * 0.20;
                break;
            case 'P':
                passengerType = "PWD";
                discount = total_fare * 0.15;
                break;
            case 'T':
                passengerType = "Student";
                discount = total_fare * 0.10;
                break;
            case 'R':
                passengerType = "Regular";
                discount = 0;
                break;
            default:
                System.out.println("Invalid Passenger Type.");
                return;
        }

        // Apply rounding to ensure correct format
        discount = Math.round(discount * 100.0) / 100.0;
        double total_amount = Math.round((total_fare - discount) * 100.0) / 100.0;

        // Print the official receipt
        System.out.println("\n========== OFFICIAL RECEIPT ==========");
        System.out.println("Passenger Name: " + name);
        System.out.println("Type of Passenger: " + passengerType);
        System.out.printf("Total Fare: %.2f\n", total_fare);
        System.out.printf("Discount: %.2f\n", discount);
        System.out.printf("Total Amount to be Paid: %.2f\n", total_amount);
    }

    public static void main(String[] args) {
        String[] details = get_passenger_details();
        String name = details[0];
        double distance = Double.parseDouble(details[1]);
        char type = details[2].charAt(0);

        generate_receipt(name, distance, type);
    }
}
