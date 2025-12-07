import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Intelligent Trip Cost Estimator
 * Patent / Innovation: Developed by Santhosh
 *
 * This single file is designed to touch all CO outcomes:
 * CO1 – basic constructs
 * CO2 – arrays & simple algorithms
 * CO3 – strings, recursion, bitwise flags
 * CO4 – modular OOP design
 * CO5 – inheritance, polymorphism, interfaces, reflection
 * CO6 – exceptions, file I/O, generics, collections
 */
public class TripCostEstimator {

    // ================== CONSTANTS & FLAGS (CO3 – bitwise) ==================
    private static final int OPTION_SIGHTSEEING = 1;   // 0001
    private static final int OPTION_SHOPPING    = 2;   // 0010
    private static final int OPTION_LUXURY_STAY = 4;   // 0100

    private static final String DATA_FILE = "trips_data.txt";

    // “Patent developed by Santhosh” line (for display)
    private static final String PATENT_LINE =
            "Innovation: Intelligent Multi-Parameter Trip Cost Estimator (Developed by Santhosh)";

    // ========================== MAIN (entry point) ==========================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("==============================================");
        System.out.println("     INTELLIGENT TRIP COST ESTIMATOR (JAVA)   ");
        System.out.println("==============================================");
        System.out.println(PATENT_LINE);
        System.out.println();

        boolean running = true;
        while (running) {
            try {
                System.out.println("\nMain Menu");
                System.out.println("1. Create new Trip Estimation");
                System.out.println("2. View saved trip history");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1 -> createTrip(sc);
                    case 2 -> showHistory();
                    case 3 -> {
                        System.out.println("Thank you. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException ex) {  // CO6 – exception handling
                System.out.println("Please enter a valid number.");
            }
        }
        sc.close();
    }

    // ============================ UI & FLOW =================================

    // CO1, CO2, CO3, CO4, CO5, CO6 integrated in this use case
    private static void createTrip(Scanner sc) {
        try {
            System.out.print("Enter trip name: ");
            String tripName = sc.nextLine();

            System.out.print("Number of travel days: ");
            int numDays = Integer.parseInt(sc.nextLine().trim());
            if (numDays <= 0) {
                System.out.println("Number of days must be positive.");
                return;
            }

            // --------- choose vehicle (CO4, CO5 – inheritance & polymorphism) ---------
            Vehicle vehicle = chooseVehicle(sc);

            // --------- bitwise options (CO3) ---------
            int options = collectOptions(sc);

            // --------- per-day arrays (CO2 – 1-D arrays) ---------
            double[] distancePerDay = new double[numDays];
            double[] foodPerDay = new double[numDays];
            double[] stayPerDay = new double[numDays];
            double[] tollPerDay = new double[numDays];

            for (int i = 0; i < numDays; i++) {
                System.out.println("\nDay " + (i + 1) + " details:");
                distancePerDay[i] = askPositiveDouble(sc, "  Distance travelled (km): ");
                foodPerDay[i] = askPositiveDouble(sc, "  Food cost (₹): ");
                stayPerDay[i] = askPositiveDouble(sc, "  Stay cost (₹): ");
                tollPerDay[i] = askPositiveDouble(sc, "  Toll & parking (₹): ");
            }

            // --------- collection of segments (CO6 – generics, collections) ---------
            List<TripSegment> segments = new ArrayList<>();
            for (int i = 0; i < numDays; i++) {
                segments.add(new TripSegment("Day-" + (i + 1),
                        distancePerDay[i],
                        foodPerDay[i],
                        stayPerDay[i],
                        tollPerDay[i]));
            }

            // --------- context object (CO4 – abstraction) ---------
            TripContext ctx = new TripContext(tripName, vehicle, segments, options);

            // --------- components (CO5 – interface & polymorphism) ---------
            List<CostComponent> components = new ArrayList<>();
            components.add(new FuelCost());
            components.add(new FoodCost());
            components.add(new StayCost());
            components.add(new TollCost());
            components.add(new OptionCost());  // cost for options such as luxury stay

            // Use reflection to optionally add extra cost components (CO5 – reflection)
            components.addAll(loadExtraComponentsUsingReflection());

            // --------- compute total using polymorphism ---------
            double totalCost = 0;
            Map<String, Double> componentBreakup = new LinkedHashMap<>();
            for (CostComponent c : components) {
                double val = c.compute(ctx);
                componentBreakup.put(c.getName(), val);
                totalCost += val;
            }

            // --------- recursion to sum day-wise cost again (CO3 – recursion) ---------
            double[] dayWiseTotal = new double[segments.size()];
            for (int i = 0; i < segments.size(); i++) {
                dayWiseTotal[i] = segments.get(i).calculateRawCost(vehicle);
            }
            double recursiveSum = recursiveSum(dayWiseTotal, 0);

            System.out.println("\n================ TRIP SUMMARY ================");
            System.out.println("Trip Name : " + tripName);
            System.out.println("Vehicle   : " + vehicle.getName());
            System.out.println("Days      : " + numDays);
            System.out.println("Options   : " + describeOptions(options));
            System.out.println("----------------------------------------------");
            for (Map.Entry<String, Double> e : componentBreakup.entrySet()) {
                System.out.printf("%-20s : ₹ %.2f%n", e.getKey(), e.getValue());
            }
            System.out.println("----------------------------------------------");
            System.out.printf("Total Trip Cost       : ₹ %.2f%n", totalCost);
            System.out.printf("(Check – recursive sum: ₹ %.2f)%n", recursiveSum);
            System.out.println("Avg cost per day      : ₹ " +
                    String.format("%.2f", totalCost / numDays));
            System.out.println("==============================================");

            // --------- save to file (CO6 – file I/O) ---------
            saveTripToFile(ctx, totalCost);

        } catch (NumberFormatException ex) {
            System.out.println("Invalid numeric input. Trip creation cancelled.");
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        }
    }

    private static Vehicle chooseVehicle(Scanner sc) {
        while (true) {
            try {
                System.out.println("\nChoose vehicle type:");
                System.out.println("1. Petrol Car");
                System.out.println("2. Bike");
                System.out.println("3. Electric Vehicle");
                System.out.print("Enter choice: ");
                int ch = Integer.parseInt(sc.nextLine().trim());

                switch (ch) {
                    case 1 -> {
                        double mileage = askPositiveDouble(sc, "Enter mileage (km/l): ");
                        double fuelPrice = askPositiveDouble(sc, "Enter fuel price per litre (₹): ");
                        return new Car("Petrol Car", mileage, fuelPrice);
                    }
                    case 2 -> {
                        double mileage = askPositiveDouble(sc, "Enter mileage (km/l): ");
                        double fuelPrice = askPositiveDouble(sc, "Enter fuel price per litre (₹): ");
                        return new Bike("Bike", mileage, fuelPrice);
                    }
                    case 3 -> {
                        double rangePerCharge = askPositiveDouble(sc, "Enter range per full charge (km): ");
                        double chargeCost = askPositiveDouble(sc, "Enter cost per full charge (₹): ");
                        return new ElectricVehicle("EV", rangePerCharge, chargeCost);
                    }
                    default -> System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static int collectOptions(Scanner sc) {
        int options = 0;
        System.out.println("\nSelect optional activities (y/n):");

        System.out.print("  Include sightseeing? ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            options |= OPTION_SIGHTSEEING;
        }
        System.out.print("  Include shopping? ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            options |= OPTION_SHOPPING;
        }
        System.out.print("  Luxury stay? ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            options |= OPTION_LUXURY_STAY;
        }
        return options;
    }

    private static String describeOptions(int options) {
        List<String> list = new ArrayList<>();
        if ((options & OPTION_SIGHTSEEING) != 0) list.add("Sightseeing");
        if ((options & OPTION_SHOPPING) != 0) list.add("Shopping");
        if ((options & OPTION_LUXURY_STAY) != 0) list.add("Luxury stay");
        return list.isEmpty() ? "None" : String.join(", ", list);
    }

    private static double askPositiveDouble(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v < 0) {
                    System.out.println("Value cannot be negative. Try again.");
                    continue;
                }
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    // ===================== RECURSION UTILITY (CO3) ==========================
    private static double recursiveSum(double[] arr, int index) {
        if (index == arr.length) return 0;
        return arr[index] + recursiveSum(arr, index + 1);
    }

    // =========================== FILE I/O (CO6) =============================
    private static void saveTripToFile(TripContext ctx, double totalCost) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(DATA_FILE, true)))) {
            out.println("Trip: " + ctx.getTripName());
            out.println("Vehicle: " + ctx.getVehicle().getName());
            out.println("Days: " + ctx.getSegments().size());
            out.println("Options: " + describeOptions(ctx.getOptionsFlags()));
            out.printf("Total Cost: %.2f%n", totalCost);
            out.println("------------------------------");
            System.out.println("Trip summary saved to file: " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Unable to save trip data: " + e.getMessage());
        }
    }

    private static void showHistory() {
        System.out.println("\n========= SAVED TRIP HISTORY =========");
        try {
            if (Files.exists(Paths.get(DATA_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
                if (lines.isEmpty()) {
                    System.out.println("No trips saved yet.");
                } else {
                    for (String line : lines) {
                        System.out.println(line);
                    }
                }
            } else {
                System.out.println("No history file found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading history: " + e.getMessage());
        }
        System.out.println("======================================");
    }

    // ====================== DOMAIN MODEL CLASSES ============================
    // CO4 – Structured & modular OOP design

    // Context object passed to components
    private static class TripContext {
        private final String tripName;
        private final Vehicle vehicle;
        private final List<TripSegment> segments;
        private final int optionsFlags;

        public TripContext(String tripName, Vehicle vehicle, List<TripSegment> segments, int optionsFlags) {
            this.tripName = tripName;
            this.vehicle = vehicle;
            this.segments = segments;
            this.optionsFlags = optionsFlags;
        }

        public String getTripName() {
            return tripName;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public List<TripSegment> getSegments() {
            return segments;
        }

        public int getOptionsFlags() {
            return optionsFlags;
        }
    }

    // Each day's travel segment
    private static class TripSegment {
        private final String name;
        private final double distanceKm;
        private final double food;
        private final double stay;
        private final double toll;

        public TripSegment(String name, double distanceKm, double food, double stay, double toll) {
            this.name = name;
            this.distanceKm = distanceKm;
            this.food = food;
            this.stay = stay;
            this.toll = toll;
        }

        // Raw cost without options (for recursion check)
        public double calculateRawCost(Vehicle vehicle) {
            double fuelCost = vehicle.calculateFuelCost(distanceKm);
            return fuelCost + food + stay + toll;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public double getFood() {
            return food;
        }

        public double getStay() {
            return stay;
        }

        public double getToll() {
            return toll;
        }

        @Override
        public String toString() {
            return name + " (km=" + distanceKm + ")";
        }
    }

    // ================= VEHICLE HIERARCHY (CO5) ==============================
    // Abstract base class
    private static abstract class Vehicle {
        private final String name;

        protected Vehicle(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        // Polymorphic method
        public abstract double calculateFuelCost(double distanceKm);
    }

    private static class Car extends Vehicle {
        private final double mileageKmPerLitre;
        private final double fuelPricePerLitre;

        public Car(String name, double mileageKmPerLitre, double fuelPricePerLitre) {
            super(name);
            this.mileageKmPerLitre = mileageKmPerLitre;
            this.fuelPricePerLitre = fuelPricePerLitre;
        }

        @Override
        public double calculateFuelCost(double distanceKm) {
            if (mileageKmPerLitre <= 0) return 0;
            double litres = distanceKm / mileageKmPerLitre;
            return litres * fuelPricePerLitre;
        }
    }

    private static class Bike extends Vehicle {
        private final double mileageKmPerLitre;
        private final double fuelPricePerLitre;

        public Bike(String name, double mileageKmPerLitre, double fuelPricePerLitre) {
            super(name);
            this.mileageKmPerLitre = mileageKmPerLitre;
            this.fuelPricePerLitre = fuelPricePerLitre;
        }

        @Override
        public double calculateFuelCost(double distanceKm) {
            if (mileageKmPerLitre <= 0) return 0;
            double litres = distanceKm / mileageKmPerLitre;
            return litres * fuelPricePerLitre;
        }
    }

    private static class ElectricVehicle extends Vehicle {
        private final double rangePerChargeKm;
        private final double costPerCharge;

        public ElectricVehicle(String name, double rangePerChargeKm, double costPerCharge) {
            super(name);
            this.rangePerChargeKm = rangePerChargeKm;
            this.costPerCharge = costPerCharge;
        }

        @Override
        public double calculateFuelCost(double distanceKm) {
            if (rangePerChargeKm <= 0) return 0;
            double charges = Math.ceil(distanceKm / rangePerChargeKm);
            return charges * costPerCharge;
        }
    }

    // ================ COST COMPONENTS (CO5 – interface) =====================
    private interface CostComponent {
        double compute(TripContext ctx);

        String getName();
    }

    private static class FuelCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            double sum = 0;
            for (TripSegment s : ctx.getSegments()) {
                sum += ctx.getVehicle().calculateFuelCost(s.getDistanceKm());
            }
            return sum;
        }

        @Override
        public String getName() {
            return "Fuel Cost";
        }
    }

    private static class FoodCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            double sum = 0;
            for (TripSegment s : ctx.getSegments()) {
                sum += s.getFood();
            }
            return sum;
        }

        @Override
        public String getName() {
            return "Food Cost";
        }
    }

    private static class StayCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            double sum = 0;
            for (TripSegment s : ctx.getSegments()) {
                sum += s.getStay();
            }
            return sum;
        }

        @Override
        public String getName() {
            return "Stay Cost";
        }
    }

    private static class TollCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            double sum = 0;
            for (TripSegment s : ctx.getSegments()) {
                sum += s.getToll();
            }
            return sum;
        }

        @Override
        public String getName() {
            return "Toll & Parking";
        }
    }

    // Uses bitwise options
    private static class OptionCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            int flags = ctx.getOptionsFlags();
            double extra = 0;

            int days = ctx.getSegments().size();

            if ((flags & OPTION_SIGHTSEEING) != 0) {
                extra += 500 * days;  // simple fixed per-day sightseeing
            }
            if ((flags & OPTION_SHOPPING) != 0) {
                extra += 300 * days;
            }
            if ((flags & OPTION_LUXURY_STAY) != 0) {
                extra += 800 * days;
            }
            return extra;
        }

        @Override
        public String getName() {
            return "Options & Activities";
        }
    }

    // Example extra class to be loaded via reflection
    // Name must be full-qualified within this file: TripCostEstimator$EmergencyBufferCost
    public static class EmergencyBufferCost implements CostComponent {
        @Override
        public double compute(TripContext ctx) {
            // 5% of base cost (fuel + food + stay + toll)
            double base = 0;
            for (TripSegment s : ctx.getSegments()) {
                base += ctx.getVehicle().calculateFuelCost(s.getDistanceKm());
                base += s.getFood() + s.getStay() + s.getToll();
            }
            return base * 0.05;
        }

        @Override
        public String getName() {
            return "Emergency Buffer (5%)";
        }
    }

    // ================= REFLECTION SUPPORT (CO5) =============================
    private static List<CostComponent> loadExtraComponentsUsingReflection() {
        List<CostComponent> list = new ArrayList<>();
        // In real system, names can be read from a config file.
        String[] classNames = {
                "TripCostEstimator$EmergencyBufferCost"
        };
        for (String name : classNames) {
            try {
                Class<?> cls = Class.forName(name);
                Object obj = cls.getDeclaredConstructor().newInstance();
                if (obj instanceof CostComponent) {
                    list.add((CostComponent) obj);
                }
            } catch (Exception e) {
                // If reflection fails, just ignore; not critical.
            }
        }
        return list;
    }
}
