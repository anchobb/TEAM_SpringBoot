package com.example.demo.domain.repository;


import com.example.demo.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long>{



    // Type , Keyword 로 필터링된 count 계산
    @Query("SELECT COUNT(b) FROM Notice b WHERE b.title LIKE %:keyWord%")
    Integer countWhereTitleKeyword(@Param("keyWord")String keyWord);

    @Query(value = "SELECT * FROM musicdb.notice ORDER BY no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<Notice> findNoticeAmountStart(@Param("amount") int amount, @Param("offset") int offset);


    @Query(value = "SELECT * FROM musicdb.notice b WHERE b.title LIKE %:keyWord%  ORDER BY b.no DESC LIMIT :amount OFFSET :offset", nativeQuery = true)
    List<Notice> findNoticeTitleAmountStart(@Param("keyWord")String keyword, @Param("amount") int amount,@Param("offset") int offset);


}
