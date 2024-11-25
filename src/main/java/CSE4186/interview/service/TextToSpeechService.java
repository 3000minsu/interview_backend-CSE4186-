package CSE4186.interview.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import java.util.Base64;


@Service
public class TextToSpeechService {


    private final String apiKey;
    public TextToSpeechService(@Value("${google.api-key}") String secret){
        apiKey=secret;
    }

    public byte[] convertTextToSpeech(@RequestBody String text) {
        String GOOGLE_TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestMap = new HashMap<>();
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("text", text);

        Map<String, String> voiceMap = new HashMap<>();
        voiceMap.put("languageCode", "ko-KR");
        voiceMap.put("name", "ko-KR-Neural2-C");

        Map<String, String> audioConfigMap = new HashMap<>();
        audioConfigMap.put("audioEncoding", "MP3");

        requestMap.put("input", inputMap);
        requestMap.put("voice", voiceMap);
        requestMap.put("audioConfig", audioConfigMap);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);
        ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_TTS_API_URL, HttpMethod.POST, entity, Map.class);

        // Get the Base64-encoded audio content from the response
        Map<String, String> responseBody = response.getBody();
        String audioContent = responseBody.get("audioContent");

        // Decode the Base64-encoded audio content to byte[]
        byte[] audioBytes = java.util.Base64.getDecoder().decode(audioContent);

        return audioBytes;

    }
}