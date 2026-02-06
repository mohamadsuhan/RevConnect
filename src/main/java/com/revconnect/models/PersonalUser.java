package com.revconnect.models;

public class PersonalUser extends User {
    private String location;
    private String occupation;
    private String education;
    private String skills;

    public PersonalUser() {
        super();
        setUserType(UserType.PERSONAL);
    }

    public PersonalUser(String username, String email, String passwordHash,
                        String firstName, String lastName) {
        super(username, email, passwordHash, firstName, lastName, UserType.PERSONAL);
    }

    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    @Override
    public String toString() {
        return "PersonalUser{" +
                super.toString() +
                ", location='" + location + '\'' +
                ", occupation='" + occupation + '\'' +
                ", education='" + education + '\'' +
                ", skills='" + skills + '\'' +
                '}';
    }
}