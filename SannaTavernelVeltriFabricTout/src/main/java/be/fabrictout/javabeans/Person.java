package be.fabrictout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import be.fabrictout.dao.PersonDAO;

public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    protected int idPerson;
    protected String firstName;
    protected String lastName;
    protected LocalDate birthDate;
    protected String phoneNumber;

    // CONSTRUCTORS
    public Person() {}

    public Person(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber) {
    	this.idPerson = idPerson;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    // PROPERTIES
	public int getIdPerson() {
		return idPerson;
	}
	
	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}
	
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // METHODS    
    public static int getNextId(PersonDAO personDAO) {
        return personDAO.getNextIdDAO();
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate, phoneNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) &&
               Objects.equals(lastName, person.lastName) &&
               Objects.equals(birthDate, person.birthDate) &&
               Objects.equals(phoneNumber, person.phoneNumber);
    }
}
