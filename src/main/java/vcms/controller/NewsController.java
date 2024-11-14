package vcms.controller;

import org.springframework.web.bind.annotation.*;
import vcms.dto.request.NewsCreationRequest;
import vcms.dto.request.NewsUpdateRequest;
import vcms.dto.response.ApiResponse;
import vcms.dto.response.NewsResponse;
import vcms.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/all")
    public ApiResponse<List<NewsResponse>> getAllNews() {
        return ApiResponse.<List<NewsResponse>>builder()
                .result(newsService.getAllNews())
                .build();
    }

    @GetMapping("/pending")
    public ApiResponse<List<NewsResponse>> getPendingNews() {
        return ApiResponse.<List<NewsResponse>>builder()
                .result(newsService.getPendingNews())
                .build();
    }

    @GetMapping("/approved")
    public ApiResponse<List<NewsResponse>> getApprovedNews() {
        return ApiResponse.<List<NewsResponse>>builder()
                .result(newsService.getApprovedNews())
                .build();
    }

    @GetMapping("/my-news/{employeeId}")
    public ApiResponse<List<NewsResponse>> getAllMyNews(
            @PathVariable("employeeId") Long employeeId
    ) {
        return ApiResponse.<List<NewsResponse>>builder()
                .result(newsService.getAllMyNews(employeeId))
                .build();
    }

    @GetMapping("my-news/detail/{newsId}")
    public ApiResponse<NewsResponse> getMyNewsDetailById(
            @PathVariable("newsId") Long newsId
    ) {
        return ApiResponse.<NewsResponse>builder()
                .result(newsService.getMyNewsDetailById(newsId))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<NewsResponse> createNews(
            @ModelAttribute NewsCreationRequest newsRequest
    ) {
        return ApiResponse.<NewsResponse>builder()
                .result(newsService.createNews(newsRequest))
                .build();
    }

    @PutMapping("/update")
    public ApiResponse<NewsResponse> updateNewsStatus(
            @RequestBody NewsUpdateRequest request
    ) {
        return ApiResponse.<NewsResponse>builder()
                .result(newsService.updateNewsStatus(request))
                .build();
    }

    @DeleteMapping("/delete/{newsId}")
    public ApiResponse<String> deleteNews(
            @PathVariable("newsId") Long newsId
    ) {
        newsService.deleteNews(newsId);
        return ApiResponse.<String>builder()
                .result("News deleted successfully.")
                .build();
    }
}
