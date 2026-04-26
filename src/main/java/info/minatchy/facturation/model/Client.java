package info.minatchy.facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Client à facturer.
 */
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;          // ex: HCM Works Inc. (SDI)

    private String address;       // ex: 2 St. Clair Avenue West Toronto
    private String city;          // ex: Toronto
    private String province;      // ex: Ontario
    private String postalCode;    // ex: M4V 1L5
    private String country;       // ex: Canada
    private String email;
    private String phone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    /**
     * Retourne l'adresse complète sur plusieurs lignes pour l'affichage PDF.
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.isBlank()) sb.append(address).append("\n");
        StringBuilder line2 = new StringBuilder();
        if (province != null && !province.isBlank()) line2.append(province);
        if (postalCode != null && !postalCode.isBlank()) {
            if (!line2.isEmpty()) line2.append(" ");
            line2.append(postalCode);
        }
        if (!line2.isEmpty()) sb.append(line2).append("\n");
        if (country != null && !country.isBlank()) sb.append(country);
        return sb.toString().trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
}
