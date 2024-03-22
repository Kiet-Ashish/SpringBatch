package com.example.springbatchrest.Repository;

import com.example.springbatchrest.Entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoPageRepository extends PagingAndSortingRepository<Todo, Long> {
    Page<Todo> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);
    Page<Todo> findByIdLessThan(Long id, Pageable pageable);
}
