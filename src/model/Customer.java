package model;

public class Customer {
    private String name;
    private String address;
    private String address2;
    private String phoneNumber;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress2(String address) {
        this.address2 = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address, String address2 , String zip, String city, String country) {
        this.address = address + ", " + address2 + ", " + city + ", " + country + ", " + zip;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }
}
