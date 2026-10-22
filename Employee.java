import java.math.BigDecimal;

public class Employee {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final int departmentId;
    private final BigDecimal baseSalary;
    private final BigDecimal monthlyBonus;
    private final BigDecimal taxRate;
    private final BigDecimal performanceRating;
    private final int attendanceDays;

    public Employee(
            String firstName,
            String lastName,
            String email,
            int departmentId,
            BigDecimal baseSalary,
            BigDecimal monthlyBonus,
            BigDecimal taxRate,
            BigDecimal performanceRating,
            int attendanceDays) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentId = departmentId;
        this.baseSalary = baseSalary;
        this.monthlyBonus = monthlyBonus;
        this.taxRate = taxRate;
        this.performanceRating = performanceRating;
        this.attendanceDays = attendanceDays;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getDepartmentId() { return departmentId; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public BigDecimal getMonthlyBonus() { return monthlyBonus; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getPerformanceRating() { return performanceRating; }
    public int getAttendanceDays() { return attendanceDays; }
}