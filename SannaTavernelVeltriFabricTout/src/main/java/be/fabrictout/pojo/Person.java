package be.fabrictout.pojo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Person {
    // ATTRIBUTES
	protected  String firstName;
	protected  String lastName;
	protected  LocalDate birthDate; 
    protected String phoneNumber;


    // CONSTRUCTORS
    public Person() {}

    public Person(String firstName, String lastName, LocalDate birthDate, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }
    // PROPERTIES
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

    public LocalDate getBirthdate() {
        return birthDate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthDate = birthdate;
    }
    
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	// METHODS
    public int calculateAge() {
    	int age = 0;
    	
    	if (birthDate != null) {
        	age = Period.between(this.birthDate, LocalDate.now()).getYears();
    	}
    	
    	return age;
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
