package com.iths.rickard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalsRepository extends JpaRepository<Cat, Long> {

    //Custom repository methods
    Cat findByName(String name);

    Object deleteById(int i);

//    List<Cat> findByIdGreaterThan(Long id);
//    List<Cat> findAllByNameIsContaining(String name);
//    @Query("Select p from Person p where lower(p.name) like lower(?1)")
//    List<Cat> findByNameCustom(String name);
}
