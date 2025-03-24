package com.example.website_login_1.repository;

import com.example.website_login_1.dto.CityByCountryInfo;
import com.example.website_login_1.dto.CityInfo;
import com.example.website_login_1.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query(
            """
                    SELECT new com.almameet.almameet_core.dto.CityByCountryInfo(c.id, c.city)
                    FROM City c
                    WHERE c.country = :country
                    ORDER by c.city
                    """
    )
    List<CityByCountryInfo> findCityByCountry(String country);

    @Query(value =
            """
                    SELECT c.id, c.city
                    FROM Cities c
                    WHERE MATCH(city) AGAINST(:cityName IN BOOLEAN MODE)
                    AND c.country = :country
                    ORDER by c.city
                    """,
            nativeQuery = true
    )
    List<CityByCountryInfo> findCityByCountry(String country, String cityName);

    @Query(value =
            """
                    SELECT DISTINCT(c.country)
                    FROM Cities c
                    WHERE MATCH(c.country) AGAINST(:country IN BOOLEAN MODE)
                    ORDER by c.country
                    """,
            nativeQuery = true
    )
    List<String> findDistinctCountries(String country);

    @Query(
            """
                    SELECT DISTINCT c.country
                    FROM City c
                    ORDER by c.country
                    """
    )
    List<String> findDistinctCountries();

    @Query(
            """
                    SELECT new com.almameet.almameet_core.dto.CityInfo(c.id, c.city, c.longitude, c.latitude)
                    FROM City c
                    WHERE c.id = :cityId
                    """
    )
    CityInfo getCoordinates(Long cityId);
}
