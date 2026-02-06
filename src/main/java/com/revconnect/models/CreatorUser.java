package com.revconnect.models;

public class CreatorUser extends User {
    private String creatorCategory;
    private String niche;
    private String platforms;
    private String audienceSize;
    private String collaborationPreference;

    public CreatorUser() {
        super();
        setUserType(UserType.CREATOR);
    }

    public CreatorUser(String username, String email, String passwordHash,
                       String firstName, String lastName, String creatorCategory) {
        super(username, email, passwordHash, firstName, lastName, UserType.CREATOR);
        this.creatorCategory = creatorCategory;
    }

    // Getters and Setters
    public String getCreatorCategory() { return creatorCategory; }
    public void setCreatorCategory(String creatorCategory) { this.creatorCategory = creatorCategory; }

    public String getNiche() { return niche; }
    public void setNiche(String niche) { this.niche = niche; }

    public String getPlatforms() { return platforms; }
    public void setPlatforms(String platforms) { this.platforms = platforms; }

    public String getAudienceSize() { return audienceSize; }
    public void setAudienceSize(String audienceSize) { this.audienceSize = audienceSize; }

    public String getCollaborationPreference() { return collaborationPreference; }
    public void setCollaborationPreference(String collaborationPreference) { this.collaborationPreference = collaborationPreference; }

    @Override
    public String toString() {
        return "CreatorUser{" +
                super.toString() +
                ", creatorCategory='" + creatorCategory + '\'' +
                ", niche='" + niche + '\'' +
                ", platforms='" + platforms + '\'' +
                ", audienceSize='" + audienceSize + '\'' +
                ", collaborationPreference='" + collaborationPreference + '\'' +
                '}';
    }
}