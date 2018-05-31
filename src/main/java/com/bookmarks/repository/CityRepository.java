package com.bookmarks.repository;

import com.bookmarks.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Leonid on 18/5/31.
 */
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
