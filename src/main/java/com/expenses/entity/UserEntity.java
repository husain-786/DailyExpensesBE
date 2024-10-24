package com.expenses.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
	
	private static final long serialVersionUID = -8093711151959123362L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_srno")
	private int userSrno;
	
	@Column(name = "user_uuid")
    private String userUuid = UUID.randomUUID().toString();

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;
    
    @Column(name = "final_status")
    private int finalStatus;
    
}
