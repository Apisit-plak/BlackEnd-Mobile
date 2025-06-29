package com.example.IOT_HELL.database.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
@Accessors(chain = true)
public class Users extends BaseEntity {

    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "flag_delete")
    private String flagDelete;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "role")
    private String role;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;


}
