package payroll.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_ORDER")
@Data
@NoArgsConstructor
public class Order {

    private @Id @GeneratedValue Long id;

    private String description;
    private Status status;

    public Order(String description, Status status) {

        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + this.id + ", description='" + this.description + '\'' + ", status=" + this.status + '}';
    }
}