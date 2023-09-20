package com.example.demo.controller;


import com.example.demo.domain.dto.QnADto;
import com.example.demo.domain.dto.Criteria;
import com.example.demo.domain.dto.PageDto;
import com.example.demo.domain.entity.QnA;
import com.example.demo.domain.service.QnAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/qna")
@Slf4j
public class QnAController {

//    @Autowired
//    private QnARepository qnaRepository;



    @Autowired
    private QnAService qnaService;

    public static String READ_DIRECTORY_PATH ;





    //-------------------
    //-------------------
    @GetMapping("/list")
    public String list(Integer pageNo,String type, String keyword, Model model, HttpServletResponse response)
    {
        log.info("GET /qna/list... " + pageNo + " " + type +" " + keyword);

        //----------------
        //PageDto  Start
        //----------------
        Criteria criteria = null;
        if(pageNo==null) {
            //최초 /qna/list 접근
            pageNo=1;
            criteria = new Criteria();  //pageno=1 , amount=10
        }
        else {
            criteria = new Criteria(pageNo,10); //페이지이동 요청 했을때
        }
        //--------------------
        //Search
        //--------------------
        criteria.setType(type);
        criteria.setKeyword(keyword);


        //서비스 실행
        Map<String,Object> map = qnaService.GetQnAList(criteria);

        PageDto pageDto = (PageDto) map.get("pageDto");
        List<QnA> list = (List<QnA>) map.get("list");


        //Entity -> Dto
        List<QnADto>  qnaList =  list.stream().map(qna -> QnADto.Of(qna)).collect(Collectors.toList());
        System.out.println(qnaList);

        //View 전달
        model.addAttribute("qnaList",qnaList);
        model.addAttribute("pageNo",pageNo);
        model.addAttribute("pageDto",pageDto);

        //--------------------------------
        //COUNT UP - //쿠키 생성(/qna/read.do 새로고침시 조회수 반복증가를 막기위한용도)
        //--------------------------------
        Cookie init = new Cookie("reading","true");
        response.addCookie(init);
        //--------------------------------

        return "qna/list";
    }


    //-------------------
    // POST
    //-------------------
    @GetMapping("/post")
    public void get_addQnA(){
        log.info("GET /qna/post");
    }

    @PostMapping("/post")
    public String post_addQnA(@Valid QnADto dto, BindingResult bindingResult, Model model) throws IOException {
        log.info("POST /qna/post " + dto + " " + dto);

        //유효성 검사
        if(bindingResult.hasFieldErrors()) {
            for(FieldError error  : bindingResult.getFieldErrors()) {
                log.info(error.getField()+ " : " + error.getDefaultMessage());
                model.addAttribute(error.getField(), error.getDefaultMessage());
            }
            return "/qna/post";
        }

        //서비스 실행
        boolean isadd = qnaService.addQnA(dto);

        if(isadd) {
            return "redirect:/qna/list";
        }
        return "redirect:/qna/post";


    }



    //-------------------
    // READ
    //-------------------

    @GetMapping("/read")
    public String read(Long no,Integer pageNo, Model model,HttpServletRequest request, HttpServletResponse response) {
        log.info("GET /qna/read : " + no);

       //서비스 실행
       QnA qna =  qnaService.getQnAOne(no);

       QnADto dto = new QnADto();
       dto.setNo(qna.getNo());
       dto.setTitle(qna.getTitle());
       dto.setContent(qna.getContent());
       dto.setRegdate(qna.getRegdate());
       dto.setUsername(qna.getUsername());
       dto.setCount(qna.getCount());

       System.out.println("FILENAMES : " + qna.getFilename());
       System.out.println("FILESIZES : " + qna.getFilesize());

       String filenames[] = null;
       String filesizes[] = null;
       if(qna.getFilename()!=null){
           //첫문자열에 [ 제거
           filenames = qna.getFilename().split(",");
           filenames[0] = filenames[0].substring(1, filenames[0].length());
           //마지막 문자열에 ] 제거
           int lastIdx = filenames.length-1;
           System.out.println("filenames[lastIdx] : " + filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]")));
           filenames[lastIdx] = filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]"));

           model.addAttribute("filenames", filenames);
       }
       if(qna.getFilesize()!=null){
            //첫문자열에 [ 제거
           filesizes = qna.getFilesize().split(",");
           filesizes[0] = filesizes[0].substring(1, filesizes[0].length());
           //마지막 문자열에 ] 제거
           int lastIdx = filesizes.length-1;
           System.out.println("filesizes[lastIdx] : " + filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]")));
           filesizes[lastIdx] = filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]"));



           model.addAttribute("filesizes", filesizes);
       }


       if(qna.getDirpath()!=null){
           //model.addAttribute("dirpath",  qna.getDirpath());
           //--------------------------------------------------------
           // FILEDOWNLOAD 추가
           //--------------------------------------------------------
           this.READ_DIRECTORY_PATH = qna.getDirpath();
       }
       model.addAttribute("qnaDto",dto);
       model.addAttribute("pageNo",pageNo);


        //-------------------
        // COUNTUP
        //-------------------

        //쿠키 확인 후  CountUp(/qna/read.do 새로고침시 조회수 반복증가를 막기위한용도)
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
        {
            for(Cookie cookie:cookies)
            {
                if(cookie.getName().equals("reading"))
                {
                    if(cookie.getValue().equals("true"))
                    {
                        //CountUp
                        System.out.println("COOKIE READING TRUE | COUNT UP");
                        qnaService.count(qna.getNo());
                        //쿠키 value 변경
                        cookie.setValue("false");
                        response.addCookie(cookie);
                    }
                }
            }
        }

        return "/qna/read";

    }

    @GetMapping("/update")
    public void update(Long no,Model model){
        log.info("GET /qna/update no " + no);


        //서비스 실행
        QnA qna =  qnaService.getQnAOne(no);

        QnADto dto = new QnADto();
        dto.setNo(qna.getNo());
        dto.setTitle(qna.getTitle());
        dto.setContent(qna.getContent());
        dto.setRegdate(qna.getRegdate());
        dto.setUsername(qna.getUsername());
        dto.setCount(qna.getCount());


        System.out.println("FILENAMES : " + qna.getFilename());
        System.out.println("FILESIZES : " + qna.getFilesize());

        String filenames[] = null;
        String filesizes[] = null;
        if(qna.getFilename()!=null){
            //첫문자열에 [ 제거
            filenames = qna.getFilename().split(",");
            filenames[0] = filenames[0].substring(1, filenames[0].length());
            //마지막 문자열에 ] 제거
            int lastIdx = filenames.length-1;
            System.out.println("filenames[lastIdx] : " + filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]")));
            filenames[lastIdx] = filenames[lastIdx].substring(0,filenames[lastIdx].lastIndexOf("]"));

            model.addAttribute("filenames", filenames);
        }
        if(qna.getFilesize()!=null){
            //첫문자열에 [ 제거
            filesizes = qna.getFilesize().split(",");
            filesizes[0] = filesizes[0].substring(1, filesizes[0].length());
            //마지막 문자열에 ] 제거
            int lastIdx = filesizes.length-1;
            System.out.println("filesizes[lastIdx] : " + filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]")));
            filesizes[lastIdx] = filesizes[lastIdx].substring(0,filesizes[lastIdx].lastIndexOf("]"));



            model.addAttribute("filesizes", filesizes);
        }


        if(qna.getDirpath()!=null){
            //model.addAttribute("dirpath",  qna.getDirpath());
            //--------------------------------------------------------
            // FILEDOWNLOAD 추가
            //--------------------------------------------------------
            this.READ_DIRECTORY_PATH = qna.getDirpath();
        }

        model.addAttribute("qnaDto",dto);

    }

    @PostMapping("/update")
    public String Post_update(@Valid QnADto dto, BindingResult bindingResult, Model model) throws IOException {
        log.info("POST /qna/update dto " + dto);

        if(bindingResult.hasFieldErrors()) {
            for( FieldError error  : bindingResult.getFieldErrors()) {
                log.info(error.getField()+ " : " + error.getDefaultMessage());
                model.addAttribute(error.getField(), error.getDefaultMessage());
            }
            return "/qna/read";
        }

        //서비스 실행
        boolean isadd = qnaService.updateQnA(dto);

        if(isadd) {
            return "redirect:/qna/read?no="+dto.getNo();
        }
        return "redirect:/qna/update?no="+dto.getNo();

    }



    //--------------------------------
    // /qna/reply/delete
    //--------------------------------
    @GetMapping("/reply/delete/{bno}/{rno}")
    public String delete(@PathVariable Long bno, @PathVariable Long rno){
        log.info("GET /qna/reply/delete bno,rno " + rno + " " + rno);

        qnaService.deleteReply(rno);

        return "redirect:/qna/read?no="+bno;
    }

    //--------------------------------
    // /qna/reply/thumbsup
    //--------------------------------
    @GetMapping("/reply/thumbsup")
    public String thumbsup(Long bno, Long rno)
    {

        qnaService.thumbsUp(rno);
        return "redirect:/qna/read?no="+bno;
    }
    //--------------------------------
    // /qna/reply/thumbsdown
    //--------------------------------
    @GetMapping("/reply/thumbsdown")
    public String thumbsudown(Long bno, Long rno)
    {
        qnaService.thumbsDown(rno);
        return "redirect:/qna/read?no="+bno;
    }



    @ExceptionHandler(Exception.class)
    public String error1(Exception ex,Model model) {
        System.out.println("QnAExcptionHandler FileNotFoundException... ex " + ex);
        //System.out.println("GlobalExceptionHandler FileNotFoundException... ex ");
        model.addAttribute("ex",ex);
        return "qna/error";
    }

    @GetMapping("/error")
    public void error_page(){

    }



}
