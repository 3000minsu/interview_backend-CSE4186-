package CSE4186.interview.service;

//import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpeechToTextService {

    private final String apiKey;

    public SpeechToTextService(@Value("${google.api-key}") String secret) {apiKey = secret;  }

    public String convertSpeechToText(String base64Audio) {
        String GOOGLE_STT_API_URL = "https://speech.googleapis.com/v1/speech:recognize?key=" + apiKey;

        //base64Audio = base64Audio + "\\";
        Map<String, Object> requestMap = new HashMap<>();
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("encoding", "MP3");
        configMap.put("sampleRateHertz", 16000);
        //configMap.put("encoding", "WEBM_OPUS");
        //configMap.put("sampleRateHertz", 48000);
        configMap.put("languageCode", "ko-KR");

        Map<String, String> audioMap = new HashMap<>();
        audioMap.put("content", base64Audio);

        requestMap.put("config", configMap);
        requestMap.put("audio", audioMap);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(GOOGLE_STT_API_URL, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();

        List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
        String transcript = null;

        if (results != null && !results.isEmpty()) {
            Map<String, Object> firstResult = results.get(0);
            List<Map<String, Object>> alternatives = (List<Map<String, Object>>) firstResult.get("alternatives");
            if (alternatives != null && !alternatives.isEmpty()) {
                transcript = (String) alternatives.get(0).get("transcript");
            }
        }

        return transcript;
    }
}
