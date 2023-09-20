package com.example.demo.interceptor;

import com.example.demo.config.auth.PrincipalDetails;
import com.example.demo.domain.entity.QnA;
import com.example.demo.domain.repository.QnARepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QnAInterceptor implements HandlerInterceptor {

    private final QnARepository qnaRepository;

    public QnAInterceptor(QnARepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //게시물작성 사용자 정보
        long no =  Long.parseLong(request.getParameter("no"));
        QnA qna = qnaRepository.findById(no).get();
        System.out.println("[Interceptor] qna Update Interceptor..." + qna);
        String qnaUsername = qna.getUsername();
        //접속중인 사용자 정보

        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authUsername = principalDetails.getUsername();

        if(!qnaUsername.equals(authUsername)){
            //response.sendRedirect("/qna/error");
            throw new Exception("권한이 없습니다");
            //return false;
        }
        return true;    //계정정보 일치시 Update 페이지로 진입
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
