package be.fabrictout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import be.fabrictout.dao.EmployeeDAO;

public abstract class Employee extends Person implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    private String registrationCode;
    private String password;

    // CONSTRUCTORS
    public Employee() {
        super();
    }

    public Employee(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
                   String registrationCode, String password) {
        super(idPerson, firstName, lastName, birthDate, phoneNumber);
        this.registrationCode = registrationCode;
        this.password = password;
    }

    // PROPERTIES
    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // METHODS
    public static int authenticate(EmployeeDAO employeeDAO, String registrationCode, String password) {
        return employeeDAO.authenticateDAO(registrationCode, password);
    }
    
    
	public static String findTypeById(EmployeeDAO employeeDAO, int id) {
		return employeeDAO.findTypeByIdDAO(id);
	}
    
    @Override
    public String toString() {
        return "Employee{" +
                ", registrationCode='" + registrationCode + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(registrationCode, employee.registrationCode) &&
               Objects.equals(password, employee.password) &&
               super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationCode, password, super.hashCode());
    }

}
