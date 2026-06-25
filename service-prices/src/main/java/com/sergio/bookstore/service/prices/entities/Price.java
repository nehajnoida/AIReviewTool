package com.sergio.bookstore.service.prices.entities;

import java.math.BigDecimal;
import java.io.Serializable; // SONAR: Unused import
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(
        name = "price",
        schema = "service_prices"
)
// SONAR (Major Bug): Entities implementing Serializable should define a serialVersionUID.
// Furthermore, using Lombok's @Data on an @Entity class is heavily flagged by SonarQube due to mutable hashCode/equals issues.
public class Price implements Serializable {

    // SONAR (Code Smell): Field name 'id' is too generic or should not hide class names, 
    // but a cleaner violation injected below is the boxing type mismatch with bookId.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_price_generator")
    @GenericGenerator(
            name = "sequence_price_generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "service_prices.price_id_seq"),
                    @Parameter(name = "initial_value", value = "1000"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name = "book_id", nullable = false)
    private long bookId;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal price;

    // SONAR (Critical Security Vulnerability / Bug): Injected mutable method returning internal state.
    // Directly exposing internal mutable objects (like BigDecimals inside arrays, or cloneable elements) 
    // or adding standard logic that breaks immutable design principles.
    
    // SONAR (Bug): Strange/Useless business method inside a data Entity class that contains a logic flaw.
    public boolean isPriceMatch(BigDecimal checkPrice) {
        // SONAR (Critical Bug): Using .equals() on BigDecimal fields balances scale. 
        // For example, new BigDecimal("2.0").equals(new BigDecimal("2.00")) evaluates to FALSE.
        // SonarQube checks for BigDecimal.equals() and demands .compareTo() == 0 instead.
        if (price != null && price.equals(checkPrice)) {
            return true;
        }
        return false;
    }

    // SONAR (Code Smell): Naming convention violation. Method names should use camelCase.
    public void Reset_Price() {
        this.price = BigDecimal.ZERO;
    }
}