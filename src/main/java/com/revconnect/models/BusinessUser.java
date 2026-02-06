package com.revconnect.models;

public class BusinessUser extends User {
    private String businessName;
    private String businessType;
    private String industry;
    private String companySize;
    private String foundedYear;
    private String headquarters;

    public BusinessUser() {
        super();
        setUserType(UserType.BUSINESS);
    }

    public BusinessUser(String username, String email, String passwordHash,
                        String businessName, String businessType) {
        super(username, email, passwordHash, businessName, "", UserType.BUSINESS);
        this.businessName = businessName;
        this.businessType = businessType;
    }

    // Getters and Setters
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getCompanySize() { return companySize; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }

    public String getFoundedYear() { return foundedYear; }
    public void setFoundedYear(String foundedYear) { this.foundedYear = foundedYear; }

    public String getHeadquarters() { return headquarters; }
    public void setHeadquarters(String headquarters) { this.headquarters = headquarters; }

    @Override
    public String getFirstName() {
        return businessName;
    }

    @Override
    public String getLastName() {
        return businessType != null ? businessType : "";
    }

    @Override
    public String toString() {
        return "BusinessUser{" +
                super.toString() +
                ", businessName='" + businessName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", industry='" + industry + '\'' +
                ", companySize='" + companySize + '\'' +
                ", foundedYear='" + foundedYear + '\'' +
                ", headquarters='" + headquarters + '\'' +
                '}';
    }
}