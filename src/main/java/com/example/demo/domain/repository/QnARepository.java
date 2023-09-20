package com.example.demo.domain.repository;


import com.example.demo.domain.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QnARepository extends JpaRepository<QnA,Long> {




    @Query(value = "SELECT * FROM musicdb.qna ORDER BY no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<QnA> findQnAAmountStart(@Param("amount") int amount, @Param("offset") int offset);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE QnA b SET b.title = :title, b.content = :content, b.regdate = :regdate, b.count = :count, b.filename = :filename, b.filesize = :filesize WHERE b.no = :no")
    Integer updateQnA(
            @Param("title") String title,
            @Param("content") String content,
            @Param("regdate") LocalDate regdate,
            @Param("count") Long count,
            @Param("filename") String filename,
            @Param("filesize") String filesize,
            @Param("no") Long no
    );

    // Type , Keyword 로 필터링된 count 계산
    @Query("SELECT COUNT(b) FROM QnA b WHERE b.title LIKE %:keyWord%")
    Integer countWhereTitleKeyword(@Param("keyWord")String keyWord);

    @Query("SELECT COUNT(b) FROM QnA b WHERE b.username LIKE %:keyWord%")
    Integer countWhereUsernameKeyword(@Param("keyWord")String keyWord);

    @Query("SELECT COUNT(b) FROM QnA b WHERE b.content LIKE %:keyWord%")
    Integer countWhereContentKeyword(@Param("keyWord")String keyWord);

    @Query(value = "SELECT * FROM musicdb.qna b WHERE b.title LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<QnA> findQnATitleAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount, @Param("offset") int offset);

    @Query(value = "SELECT * FROM musicdb.qna b WHERE b.username LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<QnA> findQnAUsernameAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount, @Param("offset") int offset);

    @Query(value = "SELECT * FROM musicdb.qna b WHERE b.content LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<QnA> findQnAContentsAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount, @Param("offset") int offset);



}
