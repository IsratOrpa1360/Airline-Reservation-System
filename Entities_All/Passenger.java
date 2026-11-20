package Entities_All;

public class Passenger {
    private int pl;
    private String name;
    private String passengerId;
    private String gender;
    private String email;
    private String phoneNumber;
    private String passportNumber;
    private String flightNumber;
    private String nationality;
    private String emergencyContact;
    private String preferredMeal;
    private String dob;

    public Passenger() {}

    public Passenger(int pl, String name, String passengerId, String gender, String email, String phoneNumber,
            String passportNumber, String flightNumber, String nationality, String emergencyContact,
            String preferredMeal, String dob) {
        this.pl = pl;
        this.name = name;
        this.passengerId = passengerId;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passportNumber = passportNumber;
        this.flightNumber = flightNumber;
        this.nationality = nationality;
        this.emergencyContact = emergencyContact;
        this.preferredMeal = preferredMeal;
        this.dob = dob;
    }

    public Passenger(String pl, String name, String passengerId, String gender, String email, String phoneNumber,
            String passportNumber, String flightNumber, String nationality, String emergencyContact,
            String preferredMeal, String dob) {
        this.pl = Integer.parseInt(pl.trim());
        this.name = name;
        this.passengerId = passengerId;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passportNumber = passportNumber;
        this.flightNumber = flightNumber;
        this.nationality = nationality;
        this.emergencyContact = emergencyContact;
        this.preferredMeal = preferredMeal;
        this.dob = dob;
    }

    public int getPl()                      { return pl; }
    public String getName()                 { return name; }
    public String getPassengerId()          { return passengerId; }
    public String getGender()               { return gender; }
    public String getEmail()                { return email; }
    public String getPhoneNumber()          { return phoneNumber; }
    public String getPassportNumber()       { return passportNumber; }
    public String getFlightNumber()         { return flightNumber; }
    public String getNationality()          { return nationality; }
    public String getEmergencyContact()     { return emergencyContact; }
    public String getPreferredMeal()        { return preferredMeal; }
    public String getDob()                  { return dob; }

    public void setPl(int pl)                               { this.pl = pl; }
    public void setName(String name)                        { this.name = name; }
    public void setPassengerId(String passengerId)          { this.passengerId = passengerId; }
    public void setGender(String gender)                    { this.gender = gender; }
    public void setEmail(String email)                      { this.email = email; }
    public void setPhoneNumber(String phoneNumber)          { this.phoneNumber = phoneNumber; }
    public void setPassportNumber(String passportNumber)    { this.passportNumber = passportNumber; }
    public void setFlightNumber(String flightNumber)        { this.flightNumber = flightNumber; }
    public void setNationality(String nationality)          { this.nationality = nationality; }
    public void setEmergencyContact(String ec)              { this.emergencyContact = ec; }
    public void setPreferredMeal(String meal)               { this.preferredMeal = meal; }
    public void setDob(String dob)                          { this.dob = dob; }

    public String getFileWriteFormat() {
        return pl + ";" + name + ";" + passengerId + ";" + gender + ";" + email + ";" + phoneNumber + ";"
                + passportNumber + ";" + flightNumber + ";" + nationality + ";" + emergencyContact + ";"
                + preferredMeal + ";" + dob + "\n";
    }

    public Object[] getTableRow() {
        return new Object[] { pl, name, passengerId, gender, email, phoneNumber, passportNumber,
                flightNumber, nationality, emergencyContact, preferredMeal, dob };
    }

    public void show() {
        System.out.println("PL: " + pl + "  Name: " + name + "  Flight: " + flightNumber);
    }
}
