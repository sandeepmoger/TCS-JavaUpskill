package com.abc;
//pulling from git
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Scanner;

class test {
    // JDBC config – CHANGE user/password as per your MySQL
	static final String DB_URL = "jdbc:mysql://localhost:3306/empdb?useSSL=false";

    static final String USER   = "root";
    static final String PASS   = "Root123$";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(" MySQL Driver loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println(" Driver not found: " + e.getMessage());
            return;  
        }
        while (true) {
            System.out.println("1. Create\n2. Display\n3. Raise Salary\n4. Exit");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    // Create (Insert)
                    while (true) {
                        System.out.println("Enter name:");
                        String name = sc.nextLine();

                        System.out.println("Enter age:");
                        byte age = sc.nextByte();
                        sc.nextLine();

                        System.out.println("Enter Designation:");
                        String desg = sc.nextLine();

                        int sal = 0;
                        if (desg.equalsIgnoreCase("Programmer"))
                            sal = 20000;
                        else if (desg.equalsIgnoreCase("Manager"))
                            sal = 25000;
                        else if (desg.equalsIgnoreCase("TeamLead"))
                            sal = 15000;
                        else {
                            System.out.println("Invalid Designation!");
                            break;
                        }

                        String sql = "INSERT INTO employees(name, age, designation, salary) VALUES(?, ?, ?, ?)";

                        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                             PreparedStatement ps = con.prepareStatement(sql)) {

                            ps.setString(1, name);
                            ps.setByte(2, age);
                            ps.setString(3, desg);
                            ps.setInt(4, sal);

                            ps.executeUpdate();
                            System.out.println("Successfull");
                        } catch (SQLException e) {
                            System.out.println("DB Error: " + e.getMessage());
                        }

                        System.out.println("Enter Yes to add and No to stop:");
                        String ans = sc.nextLine();
                        if (!ans.equalsIgnoreCase("Yes"))
                            break;
                    }
                    break;

                case 2:
                    // Display (Select)
                    String selectSql = "SELECT name, age, designation, salary FROM employees";

                    try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                         Statement st = con.createStatement();
                         ResultSet rs = st.executeQuery(selectSql)) {

                        if (!rs.isBeforeFirst()) {
                            System.out.println("No data Found! Please enter data");
                            break;
                        }

                        System.out.println("***** Employee Data *****");
                        while (rs.next()) {
                            String name = rs.getString("name");
                            int age = rs.getInt("age");
                            String desg = rs.getString("designation");
                            int sal = rs.getInt("salary");

                            System.out.println("Name: " + name);
                            System.out.println("Age: " + age);
                            System.out.println("Designation: " + desg);
                            System.out.println("Salary: " + sal);
                            System.out.println("=================");
                        }
                    } catch (SQLException e) {
                        System.out.println("DB Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    // Raise Salary (Update)
                    System.out.println("Enter employee name to raise salary:");
                    String raiseName = sc.nextLine();

                    String findSql = "SELECT id, salary FROM employees WHERE name = ?";
                    String updateSql = "UPDATE employees SET salary = ? WHERE id = ?";

                    try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                         PreparedStatement psFind = con.prepareStatement(findSql)) {

                        psFind.setString(1, raiseName);
                        ResultSet rs = psFind.executeQuery();

                        if (!rs.isBeforeFirst()) {
                            System.out.println("Employee not found!");
                            break;
                        }

                        System.out.println("How much percentage? (1-10)");
                        byte per = sc.nextByte();
                        sc.nextLine();

                        if (per < 1 || per > 10) {
                            System.out.println("Invalid Percentage!");
                            break;
                        }

                        while (rs.next()) {
                            int id = rs.getInt("id");
                            int sal = rs.getInt("salary");
                            int newSal = sal + (sal * per / 100);

                            try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                                psUpdate.setInt(1, newSal);
                                psUpdate.setInt(2, id);
                                psUpdate.executeUpdate();
                            }

                            
                        }
                    } catch (SQLException e) {
                        System.out.println("DB Error: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Thank you for using the application!");
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}