package com.example.demo.domain.service;

import com.example.demo.domain.dto.UserDto;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean joinMember(UserDto dto, Model model,HttpServletRequest request)
    {

        //패스워드 일치여부확인
        if(!dto.getPassword().equals(dto.getRepassword()))
        {
            model.addAttribute("repassword","패스워드가 일치하지 않습니다");
            return false;
        }
        
        //Email 인증여부 확인
        HttpSession session = request.getSession();
        Boolean is_email_auth = (Boolean)session.getAttribute("is_email_auth");
        if(is_email_auth!=null)
        {
            if(is_email_auth)   //코드 인증 확인 -> 회원가입 진행
            {
                dto.setRole("ROLE_USER");
                dto.setPassword(passwordEncoder.encode(dto.getPassword()) );

                User user = UserDto.dtoToEntity(dto);

                userRepository.save(user);

                return true;
            }
            else                //Code 인증 실패상태,,,
            {
                return false;
            }

        }
        else            //Code인증 진행x
        {
            return false;
        }
    }





}
