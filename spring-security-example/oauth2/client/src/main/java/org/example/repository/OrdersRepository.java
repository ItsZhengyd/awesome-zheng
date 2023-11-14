package org.example.repository;

import org.example.entity.Orders;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdersRepository extends CrudRepository<Orders, Long> {

    List<Orders> findAllByUserName(String userName);

}
