package com.expenses.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class ProductEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_srno")
	private int productSrno;
	
	@Column(name = "buyer_user_uuid")
    private String buyerUserUuid;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buy_date")
    private Date buyDate;

    @Column(name = "product")
    private String product;    

    @Column(name = "product_price")
    private int productPrice;

//    @Column(name = "contri_users", columnDefinition = "json")
//    @ColumnTransformer(write = "?::json")
    private String contriUsers;
    
    @Column(name = "final_status")
	private int finalStatus;
    
    @Column(name = "paid")
    private int paid;
    
}
