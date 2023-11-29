package com.goalist.twiliodemo.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CompanyTelRepository {

    private static final Map<Integer, String> companyTelMap = new HashMap<>();
    static {
        companyTelMap.put(1, "+15005550000");
        companyTelMap.put(2, "+15005550001");
        companyTelMap.put(3, "+15005550006");
    }

    public String retrieveCompanyTel(Integer companyId) {
        return companyTelMap.get(companyId);
    }
}
