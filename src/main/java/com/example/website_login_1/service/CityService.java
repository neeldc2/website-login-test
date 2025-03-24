package com.example.website_login_1.service;

import com.example.website_login_1.dto.CityByCountryInfo;
import com.example.website_login_1.dto.CityInfo;
import com.example.website_login_1.entity.City;
import com.example.website_login_1.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    public List<CityByCountryInfo> getCityNamesByCountry(
            final String country,
            final String cityName
    ) {
        if (StringUtils.hasText(cityName)) {
            final String updatedCityName = cityName + "*";
            return cityRepository.findCityByCountry(country, updatedCityName);
        }
        return cityRepository.findCityByCountry(country);
    }

    public List<String> getCountries(String countryName) {
        if (StringUtils.hasText(countryName)) {
            final String updatedCountry = countryName + "*";
            return cityRepository.findDistinctCountries(updatedCountry);
        }
        return cityRepository.findDistinctCountries();
    }

    public CityInfo getCityCoordinates(
            final Long cityId) {
        return cityRepository.getCoordinates(cityId);
    }

    public void importCitiesFromCSV() {
        if (cityRepository.count() > 0) {
            return;
        }

        List<City> cities = new ArrayList<>();
        Map<String, Set<String>> countryAndCities = new HashMap<>();

        try (Reader reader = new FileReader("src/main/resources/city/worldcities.csv");
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                City city = new City();
                city.setId(Long.parseLong(csvRecord.get("id")));
                city.setCity(csvRecord.get("city_ascii"));
                city.setCountry(csvRecord.get("country"));
                city.setLongitude(Double.parseDouble(csvRecord.get("lng")));
                city.setLatitude(Double.parseDouble(csvRecord.get("lat")));

                if (!isCityAlreadyAdded(city, countryAndCities)) {
                    cities.add(city);
                    Set<String> cityNames = countryAndCities.getOrDefault(city.getCountry(), new HashSet<>());
                    cityNames.add(city.getCity());
                    countryAndCities.put(city.getCountry(), cityNames);
                }
            }

            cityRepository.saveAll(cities);

            log.info("Cities added");
        } catch (Exception e) {
            log.error("Error saving city information", e);
        }
    }

    private boolean isCityAlreadyAdded(final City city,
                                       final Map<String, Set<String>> countryAndCities) {
        Set<String> cities = countryAndCities.getOrDefault(city.getCountry(), Set.of());
        if (CollectionUtils.isEmpty(cities)) {
            return false;
        }

        return cities.stream()
                .anyMatch(cityName -> cityName.equalsIgnoreCase(city.getCity()));
    }

}
