package com.evalorithm.repository;

import com.evalorithm.entity.SupportTicket;
import com.evalorithm.enums.SupportTicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    @Query("SELECT st FROM SupportTicket st WHERE st.user.id = :userId")
    List<SupportTicket> findByUserId(@Param("userId") Long userId);

    List<SupportTicket> findByStatus(SupportTicketStatus status);

    @Query("SELECT st FROM SupportTicket st WHERE st.assignedTo.id = :userId")
    List<SupportTicket> findByAssignedToUserId(@Param("userId") Long userId);

    Page<SupportTicket> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
