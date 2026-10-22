import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static final HRManagement hr = new HRManagement();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Employee Management System ===");
            System.out.println("1. Add employee");
            System.out.println("2. View employees");
            System.out.println("3. Update employee");
            System.out.println("4. Remove employee");
            System.out.println("5. Calculate net salary");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            try {
                int choice = readInt();

                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> viewEmployees();
                    case 3 -> updateEmployee();
                    case 4 -> removeEmployee();
                    case 5 -> calculateSalary();
                    case 6 -> {  // Make sure this matches option 6
                        System.out.println("Goodbye.");
                        return;
                    }
                    default -> System.out.println("Please choose 1 to 6.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private static void addEmployee() throws SQLException {
        Employee employee = readEmployeeDetails();
        int id = hr.addEmployee(employee);
        System.out.println("Employee added. ID: " + id);
    }

    private static void viewEmployees() throws SQLException {
        List<String> employees = hr.viewEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (String employee : employees) {
            System.out.println(employee);
        }
    }

    private static void updateEmployee() throws SQLException {
        System.out.print("Employee ID to update: ");
        int id = readInt();
        Employee employee = readEmployeeDetails();

        if (hr.updateEmployee(id, employee)) {
            System.out.println("Employee updated.");
        } else {
            System.out.println("No employee found with that ID.");
        }
    }

    private static void removeEmployee() throws SQLException {
        System.out.print("Employee ID to remove: ");
        int id = readInt();

        if (hr.removeEmployee(id)) {
            System.out.println("Employee removed.");
        } else {
            System.out.println("No employee found with that ID.");
        }
    }

    private static void calculateSalary() throws SQLException {
        System.out.print("Employee ID: ");
        int id = readInt();

        BigDecimal netSalary = hr.calculateNetSalary(id);
        System.out.println("Net monthly salary: " + netSalary);
    }

    private static Employee readEmployeeDetails() {
        System.out.print("First name: ");
        String firstName = input.nextLine().trim();

        System.out.print("Last name: ");
        String lastName = input.nextLine().trim();

        System.out.print("Email: ");
        String email = input.nextLine().trim();

        System.out.println("Department: 1=IT, 2=HR, 3=Sales");
        System.out.print("Department ID: ");
        int departmentId = readInt();

        if (departmentId < 1 || departmentId > 3) {
            throw new IllegalArgumentException("Department ID must be 1, 2, or 3.");
        }

        System.out.print("Base monthly salary: ");
        BigDecimal baseSalary = readMoney();

        System.out.print("Monthly bonus: ");
        BigDecimal bonus = readMoney();

        System.out.print("Tax rate (for example, 10 for 10%): ");
        BigDecimal taxRate = readMoney();

        System.out.print("Performance rating (1-5, or 0 if unknown): ");
        BigDecimal ratingInput = readMoney();

        BigDecimal rating = ratingInput.compareTo(BigDecimal.ZERO) == 0 ? null : ratingInput;

        if (rating != null && (rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(new BigDecimal("5")) > 0)) {
            throw new IllegalArgumentException("Rating must be from 1 to 5.");
        }

        System.out.print("Attendance days this month (0-31): ");
        int attendanceDays = readInt();

        if (attendanceDays < 0 || attendanceDays > 31) {
            throw new IllegalArgumentException("Attendance must be from 0 to 31.");
        }

        if (baseSalary.compareTo(BigDecimal.ZERO) < 0
                || bonus.compareTo(BigDecimal.ZERO) < 0
                || taxRate.compareTo(BigDecimal.ZERO) < 0
                || taxRate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Salary and bonus must be non-negative; tax rate must be from 0 to 100.");
        }

        return new Employee(firstName, lastName, email, departmentId, baseSalary, bonus, taxRate, rating, attendanceDays);
    }

    private static int readInt() {
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a whole number.");
        }
    }

    private static BigDecimal readMoney() {
        try {
            return new BigDecimal(input.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid number, such as 60000 or 10.50.");
        }
    }
}