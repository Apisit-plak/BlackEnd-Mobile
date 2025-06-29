    package com.example.IOT_HELL.config;


    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


    @Configuration
    @EnableWebSecurity
    //ดำเนินการตรวจสอบสิทธิ์และการอนุญาตของผู้ใช้ในระบบ
    public class SecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JwtFilter jwtFilter;

        @Autowired
        CustomCorsConfiguration customCorsConfiguration;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .cors(c -> c.configurationSource(customCorsConfiguration)) // ให้สิทธิ์การเข้าถึง ส่วนต่างๆ
                    .authorizeHttpRequests(request -> request.requestMatchers(
                                    "/ws/**",   // ✅ WebSocket Endpoint
                                    "/app/**",  // ✅ STOMP Application Prefix
                                    "/topic/**",// ✅ STOMP Message Broker
                                    "/sendAlert",
                                    "/api/fetch",
                                    "/editIoTData/**",
                                    "/api/notification/send",
                                    "create",
                                    "login",
                                    "Tree888",
                                    "iotDataInfo",
                                    "save",
                                    "firebase/{boardId}",
                                    "profile/{userId}"

                            ).permitAll()


                            .anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults()) //การการตรวจสอบสิทธิ์ ใน header ของ HTTP request
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //การใช้งาน ที่ทำงานทุกครั้ง เมื่อ ผู้ใช้ตองการเรียกใช้ ต้องส่ง token jwt มาเพื่อตรวจสอบว่าเคยlogin แล้วหรือยัง
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //เพิ่ม filer jwtFilter ก่อน UsernamePasswordAuthenticationFilter

            return http.build();
        }

        //เมื่อผู้ใช้กรอกชื่อผู้ใช้และรหัสผ่านในแบบฟอร์มเข้าสู่ระบบและส่งข้อมูลไปยังเซิร์ฟเวอร์ ระบบจะเริ่มกระบวนการตรวจสอบการเข้าสู่ระบบ มาจาก UsernamePasswordAuthenticationFilter
        @Bean
        public AuthenticationProvider authenticationProvider() { //DaoAuthenticationProvider รับข้อมูลผู้ใช้ - UserDetailsService ค้นหา -DaoAuthenticationProvider จะตรวจสอบรหัสผ่านที่ได้รับเข้ามากับรหัสผ่านที่เก็บในระบบ
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // จะรับข้อมูลชื่อผู้ใช้ (username) และรหัสผ่าน (password) จากกระบวนการเข้าสู่ระบบ
            provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
            provider.setUserDetailsService(userDetailsService); //ใช้ UserDetailsService เพื่อค้นหาข้อมูลผู้ใช้จากแหล่งข้อมูลที่ ถูกเซ็ตไว้แล้วใน UserPrincipal - สร้าง Authentication ที่บรรจุข้อมูลเกี่ยวกับผู้ใช้
            return provider; //จะสร้างและคืนค่าวัตถุ Authentication
        }
        //คือการส่งมอบการทำงานให้แก่คลาสที่เลือกมา
        //นำ AuthenticationConfiguration มาฉีดการทำงานให้กับ  AuthenticationManager
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager(); //AuthenticationManager รับข้อมูลการเข้าสู่ระบบ ตรวจสอบข้อมูลผู้ใช้ข้อมูลนั้นถูกต้องหรือไม่ จะคือผลลัพธ์ ข้อมูลเกี่ยวกับผู้ใช้และสิทธิ์การเข้าถึง
                                                       //AuthenticationConfiguration จัดการการสร้าง AuthenticationManager ที่มีฟังก์ชันในการสร้าง AuthenticationManager ที่ใช้ในการตรวจสอบสิทธิ์ สามารถกำหนดค่าการตรวจสอบสิทธิ์อื่นๆ ได้ตามต้องการ
        }
        //AuthenticationManager ใช้สำหรับการตรวจสอบข้อมูลการเข้าสู่ระบบของผู้ใช้
        //AuthenticationConfiguration ใช้สำหรับกำหนดค่าที่เกี่ยวข้องกับการตรวจสอบสิทธิ์
        //การฉีด AuthenticationConfiguration เข้าสู่ AuthenticationManager ช่วยให้การสร้างและการใช้งาน AuthenticationManager




    }
