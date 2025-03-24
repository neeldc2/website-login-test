package com.example.website_login_1.controller;

import com.example.website_login_1.annotation.ValidatePermission;
import com.example.website_login_1.dto.CityByCountryInfo;
import com.example.website_login_1.dto.CityInfo;
import com.example.website_login_1.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.website_login_1.constant.WebsiteLoginConstants.Permissions.EDIT_PROFILE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @ValidatePermission({EDIT_PROFILE})
    @GetMapping("/countries/{countryName}/cities")
    public List<CityByCountryInfo> getCityNamesByCountry(
            @PathVariable String countryName,
            @RequestParam String cityName) {
        return cityService.getCityNamesByCountry(countryName, cityName);
    }

    @ValidatePermission({EDIT_PROFILE})
    @GetMapping("/countries")
    public List<String> getCountries(@RequestParam String countryName) {
        return cityService.getCountries(countryName);
    }

    @ValidatePermission({EDIT_PROFILE})
    @GetMapping("/cities/{cityId}")
    public CityInfo getCityCoordinates(
            @PathVariable Long cityId) {
        return cityService.getCityCoordinates(cityId);
    }

}
