package org.example.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    @NotNull
    private Long userId;
    @NotNull
    private String userName;
    private Date createTime;
    @Size(message = "订单金额不能小于0")
    private BigDecimal totalAmount;

    @JsonCreator
    public Orders(long orderId, long userId, String userName,BigDecimal totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.createTime = new Date();
    }

}
