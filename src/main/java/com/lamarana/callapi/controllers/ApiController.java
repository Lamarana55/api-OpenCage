package com.lamarana.callapi.controllers;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ApiController {
    @Value("${app.key}")
    private String key;



    @GetMapping
    ResponseEntity<?> index(){
        // In real live application the JOpenCageGeocoder should be a Singleton
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(key);

        JOpenCageForwardRequest request = new JOpenCageForwardRequest("Pita");
        request.setMinConfidence(1);
        request.setNoAnnotations(false);
        request.setNoDedupe(true);
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        System.out.println(response);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("location")
    ResponseEntity<?> localisation(@RequestParam double latitude, @RequestParam double longitude){
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(key);

        JOpenCageReverseRequest request = new JOpenCageReverseRequest(latitude, longitude);
        request.setLanguage("fr"); // prioritize results in a specific language using an IETF format language code
        request.setNoDedupe(true); // don't return duplicate results
        request.setLimit(5); // only return the first 5 results (default is 10)
        request.setNoAnnotations(true); // exclude additional info such as calling code, timezone, and currency
        request.setMinConfidence(3); // restrict to results with a confidence rating of at least 3 (out of 10)

        JOpenCageResponse response = jOpenCageGeocoder.reverse(request);

        // get the formatted address of the first result:
        String[] formattedAddress = response.getResults().get(0).getFormatted().split(",");
        String region = null;
        if(formattedAddress.length == 3){
            region = formattedAddress[1].split(" ")[2];
        }else {
            region = formattedAddress[2].split(" ")[formattedAddress.length -1];
        }
        String adress = response.getResults().get(0).getFormatted();
//        String region = formattedAddress[formattedAddress.length -1];
//        formattedAddress is now 'Travessera de Gràcia, 142, 08012 Barcelona, España'
        return  ResponseEntity.ok().body(region);
    }
}