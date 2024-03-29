package ru.mikhailov.requesthandlersystem.master.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mikhailov.requesthandlersystem.master.config.PageRequestOverride;
import ru.mikhailov.requesthandlersystem.master.request.model.Request;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findRequestsByUserId(Long userId, PageRequestOverride pageRequest);

    @Query("SELECT r FROM Request r WHERE r.status = 'SHIPPED'")
    List<Request> findRequestStatusShipped(PageRequestOverride pageRequest);

    @Query("SELECT r FROM Request r WHERE r.status = 'ACCEPTED'")
    List<Request> findRequestStatusAccepted(PageRequestOverride pageRequest);

    @Query("SELECT r FROM Request r WHERE r.status = 'REJECTED'")
    List<Request> findRequestStatusRejected(PageRequestOverride pageRequest);

    @Query("SELECT r FROM Request r " +
            "WHERE LOWER(r.user.name) LIKE CONCAT('%', LOWER(:namePart),'%') AND r.status= 'SHIPPED'")
    List<Request> findShippedRequestsByUserNamePart(
            @Param("namePart") String namePart,
            PageRequestOverride pageRequest);

    void deleteRequestsByUserId(Long userId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.status IN :statuses AND LOWER(r.user.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<Request> findByStatusAndUserNamePart(@Param("namePart") String namePart,
                                              @Param("statuses") List<RequestStatus> statuses,
                                              PageRequestOverride pageable);
}
