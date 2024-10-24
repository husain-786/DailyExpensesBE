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
@Table(name = "expenses_for_each")
public class ExpensesForEachEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "srno")
	private int srno;
	
	@Column(name = "product_srno")
	private int productSrno;
	
	@Column(name = "buyer_user_uuid")
    private String buyerUserUuid;

	@Column(name = "buyer_name")
	private String buyerName;
	
	@Column(name = "contri_user_uuid")
	private String contriUserUuid;
	
	@Column(name = "contri_user_name")
	private String contriUserName;
	
	@Column(name = "price")
	private int price;

    @Column(name = "buy_date")
    private Date buyDate;
    
}
