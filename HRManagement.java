import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HRManagement {

    public int addEmployee(Employee employee) throws SQLException {
        String sql = """
                INSERT INTO employees (
                    first_name, last_name, email, department_id,
                    base_salary, monthly_bonus, tax_rate,
                    performance_rating, attendance_days
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getEmail());
            statement.setInt(4, employee.getDepartmentId());
            statement.setBigDecimal(5, employee.getBaseSalary());
            statement.setBigDecimal(6, employee.getMonthlyBonus());
            statement.setBigDecimal(7, employee.getTaxRate());

            if (employee.getPerformanceRating() == null) {
                statement.setNull(8, Types.DECIMAL);
            } else {
                statement.setBigDecimal(8, employee.getPerformanceRating());
            }

            statement.setInt(9, employee.getAttendanceDays());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Employee was added, but no ID was returned.");
    }

    public List<String> viewEmployees() throws SQLException {
        String sql = """
                SELECT e.employee_id, e.first_name, e.last_name, e.email,
                       d.department_name, e.base_salary, e.monthly_bonus,
                       e.tax_rate, e.performance_rating, e.attendance_days
                FROM employees e
                JOIN departments d ON e.department_id = d.department_id
                ORDER BY e.employee_id
                """;

        List<String> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                String employee =
                        "ID: " + result.getInt("employee_id")
                                + " | Name: " + result.getString("first_name")
                                + " " + result.getString("last_name")
                                + " | Email: " + result.getString("email")
                                + " | Department: " + result.getString("department_name")
                                + " | Base salary: " + result.getBigDecimal("base_salary")
                                + " | Bonus: " + result.getBigDecimal("monthly_bonus")
                                + " | Tax rate: " + result.getBigDecimal("tax_rate") + "%"
                                + " | Rating: " + result.getBigDecimal("performance_rating")
                                + " | Attendance: " + result.getInt("attendance_days");

                employees.add(employee);
            }
        }

        return employees;
    }

    public boolean updateEmployee(int employeeId, Employee employee) throws SQLException {
        String sql = """
                UPDATE employees
                SET first_name = ?, last_name = ?, email = ?, department_id = ?,
                    base_salary = ?, monthly_bonus = ?, tax_rate = ?,
                    performance_rating = ?, attendance_days = ?
                WHERE employee_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getEmail());
            statement.setInt(4, employee.getDepartmentId());
            statement.setBigDecimal(5, employee.getBaseSalary());
            statement.setBigDecimal(6, employee.getMonthlyBonus());
            statement.setBigDecimal(7, employee.getTaxRate());

            if (employee.getPerformanceRating() == null) {
                statement.setNull(8, Types.DECIMAL);
            } else {
                statement.setBigDecimal(8, employee.getPerformanceRating());
            }

            statement.setInt(9, employee.getAttendanceDays());
            statement.setInt(10, employeeId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean removeEmployee(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);
            return statement.executeUpdate() == 1;
        }
    }

    public BigDecimal calculateNetSalary(int employeeId) throws SQLException {
        String sql = """
                SELECT base_salary, monthly_bonus, tax_rate
                FROM employees
                WHERE employee_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Employee not found: " + employeeId);
                }

                BigDecimal grossSalary =
                        result.getBigDecimal("base_salary")
                                .add(result.getBigDecimal("monthly_bonus"));

                BigDecimal tax = grossSalary
                        .multiply(result.getBigDecimal("tax_rate"))
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                return grossSalary.subtract(tax).setScale(2, RoundingMode.HALF_UP);
            }
        }
    }
}