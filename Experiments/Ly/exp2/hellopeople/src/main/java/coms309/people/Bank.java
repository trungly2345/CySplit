package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Bank {

    private int bank_id;

    private String name;

    private String address;

    private String telephone;

    private boolean isPublic;

    public Bank(int bank_id, String name, String address, String telephone, Boolean isPublic){
        this.bank_id = bank_id;
        this.name =  name;
        this.address = address;
        this.isPublic = isPublic;
    }


    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public String toString() {
        return   name + " "
                + bank_id + " "
                + address + " "
                + telephone+ " "
                + isPublic;
    }
}
