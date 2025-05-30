package vcms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vcms.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.orderDate = CURRENT_DATE")
    Page<Order> findAllInToday(Pageable pageable);

    Page<Order> findAllByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Order> findAllByOrderInjectionDate(LocalDate injectionDate);

    List<Order> findAllByOrderCustomerPhoneAndOrderCustomerDobOrderByOrderInjectionDateDesc(String phone,
                                                                                            LocalDate dob);

    @Query("SELECT SUM(o.orderTotal) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Long sumTotalRevenueByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findAllOrdersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v.vaccineName, COUNT(od) FROM Order o " +
            "JOIN o.orderDetailList od " +

            "JOIN od.vaccine v " +
            "WHERE o.orderInjectionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY v.vaccineName")
    List<Object[]> countVaccinesInOrders(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);


    @Query("SELECT vp.vaccinePackageName, COUNT(od) FROM Order o " +
            "JOIN o.orderDetailList od " +
            "JOIN od.vaccinePackage vp " +
            "WHERE o.orderInjectionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY vp.vaccinePackageName")
    List<Object[]> countPackagesInOrders(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);


}
