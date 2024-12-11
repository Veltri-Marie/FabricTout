package be.fabrictout.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.fabrictout.dao.EmployeeDAO;

public class Employee extends Person {
	// ATTRIBUTES
    private int idEmployee;
    private String registrationCode;
    private String password;
    private Role role;
    private List<Maintenance> maintenances;

    
    // CONSTRUCTORS
    public Employee() {
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
		
    }
    
    public Employee(String firstName, String lastName, LocalDate birthDate, String phoneNumber, 
            int idEmployee, String registrationCode, String password, Role role) {
		super(firstName, lastName, birthDate, phoneNumber);
		this.idEmployee = idEmployee;
		this.registrationCode = registrationCode;
		this.password = password;
		this.role = role;
    }

    public int getIdEmployee() {
        return idEmployee;
    }
    
	public void setIdEmployee(int idEmployee) {
		this.idEmployee = idEmployee;
	}
	
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

    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
    	this.role = role;
    }
    
    public List<Maintenance> getMaintenances() {
		return maintenances;
	}
    
    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }
    
    // METHODS
    public boolean create(EmployeeDAO employeeDAO) {
        return employeeDAO.createDAO(this); 
    }

    public static int getNextId(EmployeeDAO employeeDAO) {
        return employeeDAO.getNextIdDAO(); 
    }
    
	public static int authenticate(EmployeeDAO employeeDAO, String registrationCode, String password) {
		return employeeDAO.authenticateDAO(registrationCode, password);
	}
    
	public boolean delete(EmployeeDAO employeeDAO) {
		return employeeDAO.deleteDAO(this);
	}
	
	public boolean update(EmployeeDAO employeeDAO) {
		return employeeDAO.updateDAO(this);
	}
	
	public static Employee find(EmployeeDAO employeeDAO, int id) {
		return employeeDAO.findDAO(id);
	}
	
	public static List<Employee> findAll(EmployeeDAO employeeDAO) {
		return employeeDAO.findAllDAO();
	}
    
	public void addMaintenance(Maintenance maintenance) {
		if (maintenances == null) {
			maintenances = new ArrayList<>();
		}
		maintenances.add(maintenance);
	}
    
    @Override
    public String toString() {
        return "Employee{" +
                "idEmployee=" + idEmployee +
                ", registrationCode='" + registrationCode + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return idEmployee == employee.idEmployee && 
               Objects.equals(registrationCode, employee.registrationCode) &&
               Objects.equals(password, employee.password) && 
               role == employee.role && 
               super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEmployee, registrationCode, password, role, super.hashCode());
    }
    
    
    
    
}

