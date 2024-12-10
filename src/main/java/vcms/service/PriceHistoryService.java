package vcms.service;

import org.springframework.stereotype.Service;
import vcms.dto.request.PriceHistoryRequest;
import vcms.dto.response.PriceHistoryResponse;
import vcms.model.PriceHistory;
import vcms.model.Vaccine;
import vcms.repository.PriceHistoryRepository;
import vcms.utils.DateService;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepository;

    private final VaccineService vaccineService;

    private final DateService dateService;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository, VaccineService vaccineService,
                               DateService dateService) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.vaccineService = vaccineService;
        this.dateService = dateService;
    }


    public String addPriceHistory(PriceHistoryRequest request) {
        PriceHistory priceHistory = new PriceHistory();
        Vaccine vaccine = vaccineService.getVaccineByVaccineId(request.getVaccineId());
        priceHistory.setVaccine(vaccine);
        priceHistory.setPriceHistoryOldPrice(request.getOldPrice());
        priceHistory.setPriceHistoryNewPrice(request.getNewPrice());
        priceHistory.setPriceHistoryUpdateTime(dateService.getDateTimeNow());
        priceHistoryRepository.save(priceHistory);
        vaccine.setVaccineUpdateAt(dateService.getDateTimeNow());
        vaccineService.saveVaccine(vaccine);
        return "OK";
    }

    public List<PriceHistoryResponse> getPriceHistoryOfVaccine(Long vaccineId) {
        Vaccine vaccine = vaccineService.getVaccineByVaccineId(vaccineId);
        List<PriceHistory> priceHistoryList = priceHistoryRepository.findAllByVaccineOrderByPriceHistoryUpdateTimeDesc(
                vaccine);
        List<PriceHistoryResponse> priceHistoryResponseList = new ArrayList<>();
        for (PriceHistory priceHistory : priceHistoryList) {
            PriceHistoryResponse response = new PriceHistoryResponse();
            response.setPriceHistoryId(priceHistory.getPriceHistoryId());
            response.setPriceHistoryNewPrice(priceHistory.getPriceHistoryNewPrice());
            response.setPriceHistoryOldPrice(priceHistory.getPriceHistoryOldPrice());
            response.setPriceHistoryUpdateTime(priceHistory.getPriceHistoryUpdateTime());
            priceHistoryResponseList.add(response);
        }
        return priceHistoryResponseList;
    }
}
