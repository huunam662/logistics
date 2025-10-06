package warehouse_management.com.warehouse_management.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import warehouse_management.com.warehouse_management.model.ConnectionInterface;
import warehouse_management.com.warehouse_management.repository.ConnectionInterfaceRepository;
import warehouse_management.com.warehouse_management.utils.JwtUtils;

import java.util.Map;
import java.util.Objects; // Thêm import này cho Objects.requireNonNull

@Component
public class IntegrationUtils {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationUtils.class);
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final ConnectionInterfaceRepository connectionInterfaceRepository;

    public String getConnectUrl(String interfaceCode) {
        ConnectionInterface connectionInterface = connectionInterfaceRepository.findByInterfaceCode(interfaceCode);
        if (Objects.isNull(connectionInterface)) {
            throw IntegrationException.of("Không tìm thấy ConnInterface với interfaceCode = {" + interfaceCode + "}");
        }
        String url = connectionInterface.getInterfaceURL();
        url = url.replace("https://gateway.dev.meu-solutions.com/permission-erp", "http://localhost:8089");
        return url;
    }

    public IntegrationUtils(ObjectMapper objectMapper, RestTemplate restTemplate, ConnectionInterfaceRepository connectionInterfaceRepository) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.connectionInterfaceRepository = connectionInterfaceRepository;
    }

    public HttpHeaders buildDefaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public HttpHeaders buildHeaderWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    public String buildRequestBody(Map<String, Object> bodyMap) throws JsonProcessingException {
        return objectMapper.writeValueAsString(bodyMap);
    }

    public <T> T performPost(String url, Object body, HttpHeaders headers, Class<T> responseType) {
        logger.info("=====OUTBOUND REQUEST=====[POST]"+url);
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            // Chúng ta đảm bảo body không null và status là 2xx ở đây
            if (response.getStatusCode().is2xxSuccessful()) {
                // Sử dụng Objects.requireNonNull để đảm bảo body không null và ném NPE nếu null
                return Objects.requireNonNull(response.getBody(), "External service response body was null for URL: " + url);
            } else {
                // Điều này thường sẽ được bắt bởi HttpClientErrorException/HttpServerErrorException,
                // nhưng là một kiểm tra an toàn bổ sung.
                throw IntegrationException.of("External service responded with unexpected status: " + response.getStatusCode() + " for URL: " + url);
            }
        } catch (Exception e) {
            // Bắt bất kỳ lỗi nào khác
            throw IntegrationException.of("An unexpected error occurred while calling external service at " + url + ": " + e.getMessage());
        }
    }

    //    post mà k có body
    public <T> T performPost(String url, HttpHeaders headers, Class<T> responseType) {
        logger.info("=====OUTBOUND REQUEST=====[POST]"+url);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            // Chúng ta đảm bảo body không null và status là 2xx ở đây
            if (response.getStatusCode().is2xxSuccessful()) {
                // Sử dụng Objects.requireNonNull để đảm bảo body không null và ném NPE nếu null
                return Objects.requireNonNull(response.getBody(), "External service response body was null for URL: " + url);
            } else {
                // Điều này thường sẽ được bắt bởi HttpClientErrorException/HttpServerErrorException,
                // nhưng là một kiểm tra an toàn bổ sung.
                throw IntegrationException.of("External service responded with unexpected status: " + response.getStatusCode() + " for URL: " + url);
            }

        } catch (Exception e) {
            // Bắt bất kỳ lỗi nào khác
            throw IntegrationException.of("An unexpected error occurred while calling external service at " + url + ": " + e.getMessage());
        }
    }


    public <T> T performGet(String url, Class<T> responseType) {
        return performGet(url, buildDefaultHeader(), responseType);
    }


    public <T> T performGet(String url, HttpHeaders headers, Class<T> responseType) {
        logger.info("=====OUTBOUND REQUEST=====[GET]"+url);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody(), "External service response body was null for URL: " + url);
            } else {
                throw IntegrationException.of("External service responded with unexpected status: " + response.getStatusCode() + " for URL: " + url);
            }

        } catch (Exception e) {
            System.out.println(e);
            throw IntegrationException.of("An unexpected error occurred while calling external service at " + url + ": " + e.getMessage());
        }
    }

    public String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        if (params != null && !params.isEmpty()) {
            params.forEach(builder::queryParam);
        }
        return builder.toUriString();
    }


    public Map<String, Object> toBodyMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {
        });
    }

    public Map<String, String> toParamMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {
        });
    }


    public <T> T performPost(String url, String token, Class<T> responseType) {
        logger.info("=====OUTBOUND REQUEST=====[POST]"+url);
        return performPost(url, buildHeaderWithToken(token), responseType);
    }

    public <T> T performGet(String url, String token, Class<T> responseType) {
        logger.info("=====OUTBOUND REQUEST=====[GET]"+url);
        return performGet(url, buildHeaderWithToken(token), responseType);
    }
}