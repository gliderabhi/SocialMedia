package com.example.munnasharma.classes;

/**
 * Created by MunnaSharma on 5/31/2017.
 */

public class StudentDetails {

 private String FirstName,LastName,College,Branch,Email,MobileNo,Year,sex,ProviderId,profilePicLocation;

    public StudentDetails(){

    }
  public StudentDetails(String name,String email,String providerId){
          this.FirstName=name;
      this.Email=email;
      this.ProviderId=providerId;
    }
    public StudentDetails(String firstName,String LastName,String College,String Branch,String year,String email,String MobileNo,String sex){
       //Save the data sent of students in objetcs created
        this.FirstName=firstName;
        this.LastName=LastName;
        this.College=College;
        this.Branch=Branch;
        this.Email=email;
        this.Year=year;
        this.sex=sex;
        this.MobileNo=MobileNo;

    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    public void setProfilePicLocation(String profilePicLocation) {
        this.profilePicLocation = profilePicLocation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProviderId() {
        return ProviderId;
    }

    public void setProviderId(String providerId) {
        ProviderId = providerId;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public String getCollege() {
        return College;
    }

    public void setCollege(String college) {
        College = college;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }


}
