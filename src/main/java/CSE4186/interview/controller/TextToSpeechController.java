package CSE4186.interview.controller;

import CSE4186.interview.service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tts")
public class TextToSpeechController {

    @Autowired
    private TextToSpeechService textToSpeechService;

    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody String text) {
        try {
            byte[] audioContent = textToSpeechService.synthesizeText(text);
            return ResponseEntity.ok()
                    .header("Content-Type", "audio/mpeg")
                    .body(audioContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}