package vcms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vcms.dto.request.NewsCreationRequest;
import vcms.dto.request.NewsUpdateRequest;
import vcms.dto.response.NewsResponse;
import vcms.exception.AppException;
import vcms.exception.ErrorCode;
import vcms.mapper.NewsMapper;
import vcms.model.Employee;
import vcms.model.News;
import vcms.repository.NewsRepository;
import vcms.utils.DateService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    @Value("${upload.newsFolder}")
    private String UPLOAD_NEWS_FOLDER;
    private final NewsRepository newsRepository;

    private final DateService dateService;

    private final EmployeeService employeeService;

    private final NewsMapper newsMapper;


    public NewsService(NewsRepository newsRepository, DateService dateService,
                       EmployeeService employeeService,
                       NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.dateService = dateService;
        this.employeeService = employeeService;
        this.newsMapper = newsMapper;
    }

    public List<NewsResponse> getAllNews() {
        List<NewsResponse> newsResponseList = new ArrayList<>();
        List<News> newsList = newsRepository.findAll();
        for (News news : newsList) {
            NewsResponse response = newsMapper.toNewsResponse(news);
            response.setEmployeeFullName(news.getEmployee().getEmployeeFullName());
            response.setEmployeeAvatar(news.getEmployee().getEmployeeAvatar());
            newsResponseList.add(response);
        }
        return newsResponseList;
    }

    public List<NewsResponse> getPendingNews() {
        List<NewsResponse> newsResponseList = new ArrayList<>();
        List<News> newsList = newsRepository.findAllByNewsStatus("PENDING");
        for (News news : newsList) {
            NewsResponse response = newsMapper.toNewsResponse(news);
            response.setEmployeeFullName(news.getEmployee().getEmployeeFullName());
            response.setEmployeeAvatar(news.getEmployee().getEmployeeAvatar());
            newsResponseList.add(response);
        }
        return newsResponseList;
    }

    public List<NewsResponse> getApprovedNews() {

        List<NewsResponse> newsResponseList = new ArrayList<>();
        List<News> newsList = newsRepository.findAllByNewsStatus("APPROVED");
        for (News news : newsList) {
            NewsResponse response = newsMapper.toNewsResponse(news);
            response.setEmployeeFullName(news.getEmployee().getEmployeeFullName());
            response.setEmployeeAvatar(news.getEmployee().getEmployeeAvatar());
            newsResponseList.add(response);
        }
        return newsResponseList;
    }

    public NewsResponse updateNewsStatus(NewsUpdateRequest request) {
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        news.setNewsStatus(request.getStatus());
        return newsMapper.toNewsResponse(newsRepository.save(news));
    }

    public void deleteNews(Long newsId) {
        newsRepository.deleteById(newsId);
    }

    public String saveNewsImage(MultipartFile newsImageFile) {
        String contentType = newsImageFile.getContentType();
        String fileExtension = "";

        // Xác định phần mở rộng dựa trên loại ảnh
        if ("image/jpeg".equals(contentType)) {
            fileExtension = ".jpg";
        }
        else if ("image/png".equals(contentType)) {
            fileExtension = ".png";
        }
        else {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        // Lấy tên tệp gốc và thêm phần mở rộng (nếu cần)
        String originalFilename = newsImageFile.getOriginalFilename();
        if (originalFilename != null && !originalFilename.endsWith(fileExtension)) {
            originalFilename += fileExtension;
        }

        // Xác định đường dẫn lưu tệp ảnh
        Path newsFolderPath = Paths.get(UPLOAD_NEWS_FOLDER).toAbsolutePath().normalize();
        String newFilePath = newsFolderPath.resolve(originalFilename).toString();

        try {
            // Lưu ảnh vào thư mục đã chỉ định
            newsImageFile.transferTo(new File(newFilePath));
            return originalFilename; // Trả về tên tệp để lưu vào cơ sở dữ liệu
        }
        catch (IOException ex) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }
    }

    public NewsResponse createNews(NewsCreationRequest request) {
        try {
            News news = new News();
            news.setNewsTitle(request.getNewsTitle());
            news.setNewsContent(request.getNewsContent());
            news.setNewsCreateAt(dateService.getDateNow());
            news.setNewsUpdateAt((dateService.getDateNow()));
            Employee employee = employeeService.getEmployeeByEmpId(request.getEmployeeId());
            news.setEmployee(employee);
            news.setNewsStatus("PENDING");
            String imageName = saveNewsImage(request.getNewsImage());
            news.setNewsImage(imageName);
            newsRepository.save(news);
            return newsMapper.toNewsResponse(news);
        }
        catch (Exception exception) {
            throw new AppException(ErrorCode.CREATE_FAILED);
        }
    }
}
