package com.example.IOT_HELL.database;

import com.example.IOT_HELL.database.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private Users user;

    public UserPrincipal(Users user) {
        this.user = user;
    }

    //ทำหน้าที่เป็นตัวแทนของผู้ใช้ในระบบและช่วยในการจัดการการตรวจสอบสิทธิ์
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER")); //ร้างคอลเล็กชันที่มีขนาด 1 ซึ่งประกอบด้วยวัตถุ SimpleGrantedAuthority ที่มีชื่อบทบาทเป็น "USER"
    } //SimpleGrantedAuthority เป็นคลาสที่ใช้ใน Spring Security เพื่อแทนบทบาทหรือสิทธิ์การเข้าถึง

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    //การเช็ค ว่า  Account นั้นไม่ได้ หมดอายุหรือไม่
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //การเช็ค ว่า Account นั้นไม่ได้ถูกล็อก
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //สถานะบัญชีผู้ใช้เปิดใช้งาน
    @Override
    public boolean isEnabled() {
        return true;
    }
}
