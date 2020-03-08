package com.iths.rickard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalsRepository extends JpaRepository<Cat, Long> {

    Cat findByName(String name);
    Object deleteById(int i);
}
