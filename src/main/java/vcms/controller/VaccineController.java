package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.VaccineCreationRequest;
import vcms.dto.request.VaccineUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.VaccineResponse;
import vcms.model.Vaccine;
import vcms.service.VaccineService;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {
    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public ApiResponse<List<Vaccine>> getAllVaccines(){
        return vaccineService.getVaccines();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<VaccineResponse> getVaccineById(@PathVariable("id") Long id){
        return vaccineService.getVaccine(id);
    }

    @PostMapping("/create")
    public ApiResponse<VaccineResponse> createVaccine(@RequestBody VaccineCreationRequest request){
        return vaccineService.createVaccine(request);
    }

    @PutMapping("/update/{id}")
    public ApiResponse<VaccineResponse> updateVaccineById(@PathVariable("id") Long id,
                                                          @RequestBody VaccineUpdateRequest request){
        return vaccineService.updateVaccine(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteVaccineById(@PathVariable("id") Long id){
        return vaccineService.deleteVaccine(id);
    }
}
