package com.example.website_login_1.dto;

public record CityInfo(
        Long cityId,
        String cityName,
        Double longitude,
        Double latitude
) {
}
