package com.lilium.elasticsearch.controller;

import com.lilium.elasticsearch.document.Vehicle;
import com.lilium.elasticsearch.search.SearchRequestDTO;
import com.lilium.elasticsearch.service.VehicleService;
//import com.lilium.elasticsearch.service.helper.VehicleDummyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
    private final VehicleService service;
//    private final VehicleDummyDataService dummyDataService;

    @Autowired
//    public VehicleController(VehicleService service, VehicleDummyDataService dummyDataService) {
    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    public void index(@RequestBody final Vehicle vehicle) {
        service.index(vehicle);
    }

//    @PostMapping("/insertdummydata")
//    public void insertDummyData() {
//        dummyDataService.insertDummyData();
//    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable final String id) {
        return service.getById(id);
    }

    @PostMapping("/search")
    public List<Vehicle> search(@RequestBody final SearchRequestDTO dto) {
        return service.search(dto);
    }

    @GetMapping("/searchVehiclesCreatedSince/{date}")
    public List<Vehicle> getAllVehiclesCreatedSince(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return service.getAllVehiclesCreatedSince(date);
    }

    @PostMapping("/searchcreatedsince/{date}")
    public List<Vehicle> searchCreatedSince(
            @RequestBody final SearchRequestDTO dto,
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return service.searchCreatedSince(dto, date);
    }

    @GetMapping("/searchVehiclesCreatedBetween")
    public List<Vehicle> getAllVehiclesCreatedBetween(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {
        return service.getAllVehiclesCreatedBetween(fromDate, toDate);
    }

    @GetMapping("/searchVehiclesByDriver")
    public List<Vehicle> getAllVehiclesByDriver(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName) {
        return service.getAllVehiclesByDriver(firstName, lastName);
    }

}