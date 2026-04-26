package info.minatchy.facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Informations sur l'émetteur de la facture (vous-même).
 * Il n'y a généralement qu'un seul émetteur (profil unique).
 */
@Entity
@Table(name = "issuer")
public class Issuer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String companyName;   // ex: COMPANY_NAME

    @NotBlank
    @Column(nullable = false)
    private String contactName;   // ex: CONTACT_NAME

    private String email;         // ex: EMAIL
    private String phone;         // ex: (438) 497 0971
    private String tpsNumber;     // ex: 727188633 RT0001
    private String tvqNumber;     // ex: 1233677082 TQ0001
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTpsNumber() { return tpsNumber; }
    public void setTpsNumber(String tpsNumber) { this.tpsNumber = tpsNumber; }

    public String getTvqNumber() { return tvqNumber; }
    public void setTvqNumber(String tvqNumber) { this.tvqNumber = tvqNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
