package com.example.parkfinder.data;

import com.example.parkfinder.model.Park;

import java.util.List;

public interface AsyncResponse {
    void processPark(List<Park> parks);
}
