package CSE4186.interview.service;

import CSE4186.interview.entity.Question;
import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.entity.SelfIntroductionDetail;
import CSE4186.interview.exception.NotFoundException;
import CSE4186.interview.repository.QuestionRepository;
import CSE4186.interview.repository.SelfIntroductionDetailRepository;
import CSE4186.interview.repository.SelfIntroductionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@Transactional
public class QuestionService {

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final SelfIntroductionDetailRepository selfIntroductionDetailRepository;
    private final SelfIntroductionRepository selfIntroductionRepository;
    private final QuestionRepository questionRepository;
    private final TextToSpeechService textToSpeechService;
    private final SpeechToTextService speechToTextService;
    private final String url;

    private final List<String> deptName = new ArrayList<>(Arrays.asList(
            "백엔드/서버개발",
            "프론트엔드",
            "앱개발",
            "게임개발",
            "데이터 사이언티스트",
            "빅 데이터 개발",
            "데브옵스 개발",
            "임베디드 소프트웨어 개발",
            "정보보안",
            "인공지능 개발",
            "기타 프로그램 개발"));

    private final String system_content_tech="###Role###\n You need to write a script for a \"%s\" development team leader who will conduct an interview." +
            "Your role consists of two tasks: 1. Classify the given self-introduction into achievements and activities the applicant has undertaken" +
            "and the lessons the applicant has learned during the process." +
            "2. Provide %d questions to verify the authenticity of the 'achievements (activities)' " +
            "and %d questions to assess the applicant's 'technical understanding' based on the 'lessons learned'." +
            "\n\n###Note### \n Do not output the results of task 1. For task 2, number each question and separate them with line breaks." +
            "Do not categorize each question into \"achievements(activities)\" or \"lessons learned\". This is a script to be read to the applicant, " +
            "so no additional comments should be added. Make sure to produce exactly total %d number of questions.\n\n" +

            "###Example Response###\n" +
            "<행위 질문>\n" +
            "1. 질문 1\n" +
            "2. 질문 2\n" +
            "...\n" +
            "<배운점 질문>\n" +
            "3. 질문 3\n" +
            "4. 질문 4\n" +
            "...";
    private final String system_content_personality="###Role###\n You need to write a script for a human resource team leader who will conduct an interview." +
            "Your role consists of two tasks: 1. Classify the given self-introduction into achievements and activities the applicant has undertaken" +
            "and the lessons the applicant has learned during the process." +
            "2. Provide %d questions to verify the authenticity of the 'achievements (activities)' " +
            "and %d questions to assess the applicant's 'character' based on the \'lessons learned\'" +
            "\n\n###Note### \n Do not output the results of task 1. For task 2, number each question and separate them with line breaks." +
            "Do not categorize each question into \"action\" or \"lessons learned\". This is a script to be read to the applicant, " +
            "so no additional comments should be added. Make sure to produce exactly total %d number of questions.\n\n" +

            "###Example Response###\n" +
            "<행위 질문>\n" +
            "1. 질문 1\n" +
            "2. 질문 2\n" +
            "...\n" +
            "<배운점 질문>\n" +
            "3. 질문 3\n" +
            "4. 질문 4\n" +
            "...";

    private final String system_content_followUp="###Role###\nYou need to write a script for a team lead of a \"%s\" development team who is acting as an interviewer.\n" +
            "The team lead should continuously engage in conversation with the applicant.\n" +
            "Based on the context of the chat and the applicant's latest answer, the team lead should ask follow-up questions to assess the applicant's knowledge level. " +
            "The team lead can ask for detailed explanations about the technologies mentioned by the applicant or request the applicant to elaborate on advanced knowledge related to those technologies.\n\n"+
            "###Note###\nYou should consider the context of the chat between you and the applicant so far.\n" +
            "And you need to create questions based on the applicant's latest answer.\n" +
            "Create questions to verify the development knowledge included in the user's last answer. " +
            "Also, the answers to your questions should not be found in the applicant's previous response."+
            "You have to make questions in Korean.\n"+
            "I will now provide the conversation between you and the applicant.\n" +
            "You asked the first question, and your question is as follows:\n";
    private final String system_content_followUp_tail="\n###Role###\nPlease ask additional questions based on %s development keywords in user's response.\n"+
            "You should give higher priority to the most recent user chat when generating new questions.\n"+
            "You need to ask 3 questions to verify user's knowledge level. For example, you can ask user to explain %s development keywords included in user's answer.\n"+
            "###Note###\nnumber each question and separate them with line breaks.\n"+
            "###Example Response###\n"+
            "1. 질문 1\n"+
            "2. 질문 2\n"+
            "3. 질문 3\n";

    public QuestionService(@Value("${google.api-key}") String secret, ObjectMapper objectMapper, SelfIntroductionDetailRepository selfIntroductionDetailRepository, SelfIntroductionRepository selfIntroductionRepository, QuestionRepository questionRepository,  TextToSpeechService textToSpeechService, SpeechToTextService speechToTextService){
        apiKey=secret;
        url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        this.objectMapper = objectMapper;
        this.selfIntroductionDetailRepository = selfIntroductionDetailRepository;
        this.selfIntroductionRepository = selfIntroductionRepository;
        this.questionRepository = questionRepository;
        this.textToSpeechService = textToSpeechService;
        this.speechToTextService = speechToTextService;
    }

    public static class QuestionObject{
        private List<List<Map<String,String>>> questionList;
        private int followupNum;
    }

    @Builder
    @Getter
    public static class ApiRequestBody {

        private List<Contents> contents;
        private List<SafetySettings> safetySettings;
        private GenerationConfig generationConfig;

        @Getter
        @Builder
        public static class Contents {
            List<PartElem> parts;
        }

        @Getter
        @Builder
        static class SafetySettings {
            String category;
            String threshold;
        }

        @Getter
        @Builder
        static class GenerationConfig {
            List<SequenceElem> stopSequences;
            float temperature;
            int maxOutputTokens;
            float topP;
            int topK;
        }

        @Getter
        @Builder
        static class PartElem {
            String text;
        }

        @Getter
        @Builder
        static class SequenceElem {
            List<String> array;
        }
    }

    private List<Map<String,String>> createChat(String role,String content){
        List<Map<String,String>> message = new ArrayList<>();
        Map chat=new HashMap<>();
        chat.put("role",role);
        chat.put("content",content);
        message.add(chat);
        return message;
    }

    private List<Map<String,String>> setPrompt(Integer questionNum, String dept, String type, String selfIntroductionContent){
        String system_content;

        //0. 질문 type을 판단하여 tech일 경우 직무를 프롬프트에 추가해준다
        if(type.matches("기술 항목")) system_content=String.format(system_content_tech,dept,questionNum/2,questionNum-(questionNum/2), questionNum);
        else system_content=String.format(system_content_personality,questionNum/2,questionNum-(questionNum/2), questionNum);
        System.out.println("######prompt set######");
        System.out.println(system_content);
        System.out.println("######prompt set######");

        List<Map<String,String>> message;

        //1. system의 chat을 만든다
        message = createChat("system",system_content);

        return message;
    }

    /*프롬프트 테스트용. 프론트에서 사용자의 답변을 텍스트로 변환해서 줬다고 가정. STT 적용되면 수정해야 함.*/
    private List<Map<String,String>> setPromptForFollowUp(int turn, int selfIntroductionId, String dept, List<Map<String,String>>prevChat, String userAudio){
        String messageToJson;
        String system_content;
        String system_content_tail;
        SelfIntroduction selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId)).orElseThrow(()->new NotFoundException("해당하는 자소서를 찾을 수 없습니다."));
        List<Map<String,String>> message = new ArrayList<>();

        //1. turn이 0이라면 시스템 프롬프트를 추가한다.
        if(turn==0){
            //1.0 시스템 프롬프트 설정
            system_content=String.format(system_content_followUp,dept);

            //1.1 [시스템 프롬프트+prevChat의 첫번째 system chat(최초의 질문)]을 하나의 system chat으로
            message.addAll(createChat("system",system_content+prevChat.get(0).get("content")));

            //1.2 prevChat의 첫번째 system chat과 마지막 user chat을 제외하고 message 배열에 추가.
            message.addAll(prevChat.subList(1, prevChat.size()));
        }
        //아닐 시 마지막 user chat을 제외하고 전부 message 배열에 추가한다.
        else{
            message.addAll(prevChat.subList(0,prevChat.size()));

            // DB에 저장한다.
            //1. 후보 꼬리 질문들 중 유저가 선택한 질문(유저의 응답 직전 시스템 질문)을 가져온다.
            String questionContent=prevChat.get(prevChat.size()-1).get("content");

            //2. 새로운 Question Entity를 생성한다.
            Question question=Question.builder()
                    .content(questionContent)
                    .selfIntroduction(selfIntroduction)
                    .build();

            //3. DB에 question을 저장한다.
            questionRepository.save(question);
        }
        
        //user 음성 데이터를 텍스트로 변환한다.
        String userText = speechToTextService.convertSpeechToText(userAudio);

        //2. 마지막 user chat의 뒤에 변환된 텍스트를 활용하여 유저 프롬프트를 추가한다.
        //createChat("user",prevChat.get(prevChat.size()-1).get("content"));
        message.addAll(createChat("user",userText));
        System.out.println(userText);

        //3. 시스템의 프로므트를 다시 설정하여 chat에 추가
        system_content_tail=String.format(system_content_followUp_tail,dept,dept);
        message.addAll(createChat("system",system_content_tail));

        return message;
    }

    private HttpEntity<String> setHeader(String requestBody){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        return requestEntity;
    }

    private String getTextContent(String response) {
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> candidates= (Map<String, Object>)((List<Object>)jsonMap.get("candidates")).get(0);
        List<Map<String, Object>> parts=(List<Map<String,Object>>)((Map<String,Object>) candidates.get("content")).get("parts");
        String textContent = (String)parts.get(0).get("text");
        return textContent;
    }

    private Boolean getQuestions(Integer requiredQuestionNum, String textContent, String type, Integer maxQuestionNum, QuestionObject questionObject){
        //1. 질문을 \n 기준으로 파싱
        int followupNumTemp = questionObject.followupNum;
        String[] questionsParsedByLine=textContent.split("\n");
        for(int i=0; i<questionsParsedByLine.length; i++) System.out.println(questionsParsedByLine[i]);

        //2. 질문을 <숫자.> 기준으로 파싱하고 <숫자.>은 삭제
        String[] rawQuestions=Arrays.stream(questionsParsedByLine)
                .filter(q->q.matches("\\d+\\..*"))
                .map(q->q.replaceAll("\\d+\\.","").trim())
                .toArray(String[]::new);

        //3. List에 [{"Text", "질문"}, {"Audio", "오디오 데이터"}, {"Turn", "1"}]  형식으로 저장 -> 결국 이중 리스트 형식
        List<List<Map<String,String>>> textAudioTurnList=new ArrayList<>();
        IntStream.range(0, rawQuestions.length)
                .forEach(index->{
                    List<Map<String, String>> questionInfo = new ArrayList<>();

                    //{"Text", "질문"} 형식으로 생성
                    Map<String, String> questionTextMap = new HashMap<>();
                    questionTextMap.put("Text", rawQuestions[index]);
                    questionInfo.add(questionTextMap);

                    //{"Audio", "오디오 데이터"} 형식으로 생성
                    Map<String, String> questionAudioMap = new HashMap<>();
                    //tts에서 오디오파일을 가져온다
                    byte[] audioData = textToSpeechService.convertTextToSpeech(rawQuestions[index]);
                    String audioBase64 = Base64.getEncoder().encodeToString(audioData);
                    questionAudioMap.put("Audio", audioBase64);
                    questionInfo.add(questionAudioMap);

                    //{"Turn", "1"} 형식으로 생성
                    Map<String, String> questionTurnMap = new HashMap<>();
                    // 꼬리 질문은 "tech" 타입에서 2개만 생성
                    // 꼬리 질문은 3으로 표시
                    if(type.equals("기술 항목") && questionObject.followupNum<2) {
                        questionTurnMap.put("Turn", "3");
                        //followupNumTemp.getAndIncrement();
                        questionObject.followupNum++;
                    }
                    // 일반 질문은 1으로 표시
                    else questionTurnMap.put("Turn", "1");
                    questionInfo.add(questionTurnMap);

                    // [{"Text", "질문"}, {"Audio", "오디오 데이터"}, {"Turn", "1"}] 꼴을 리스트에 넣어서 2중 리스트 만들기
                    textAudioTurnList.add(questionInfo);
                });

        //4.0 올바른 형식인지 검사 - 빈 리스트인가?
        if(textAudioTurnList.size()==0){
            System.out.println("Zero questions");
            questionObject.followupNum = followupNumTemp;
            return false;
        }

        //4.1 올바른 형식인지 검사 - 질문 형식이 맞는가? -> 재요청
        Map<String,String> first_Question_Audio_PairMap=textAudioTurnList.get(0).get(0);
        String firstQuestion=first_Question_Audio_PairMap.get("Text");
        System.out.println("firstQuestion : "+firstQuestion);
        if(!(firstQuestion.endsWith("?") || firstQuestion.endsWith("요.")||firstQuestion.endsWith("바랍니다.")||firstQuestion.endsWith("바랍니다"))){
            System.out.println("wrong format");
            questionObject.followupNum = followupNumTemp;
            return false;
        }

        //4.2 올바른 형식인지 검사 - 원래 질문보다 적은 수가 생성되었는가? -> *추가 안하고* 재요청
        if(textAudioTurnList.size()<requiredQuestionNum) {
            System.out.println("less questions");
            if(textAudioTurnList.size()>maxQuestionNum)
                questionObject.questionList.addAll(textAudioTurnList.subList(0,maxQuestionNum));
            else
                questionObject.questionList.addAll(textAudioTurnList);
            return false;
        }

        //4.3 올바른 형식인지 검사 - 원래 질문보다 많은 수가 생성되었는가?
        // -> 원래 개수만큼 선택 => (m개, m개)인데 (n개, n개)가 생성됨. (0~m)인덱스 선택. (n~n+m)인덱스 선택.
        if(textAudioTurnList.size()>requiredQuestionNum){
            System.out.println("too much questions");
            //int need=requiredQuestionNum;
            questionObject.questionList.addAll(textAudioTurnList.subList(0,maxQuestionNum));
            return true;
        }

        //5. questionList에 생성된 질문 담기
        questionObject.questionList.addAll(textAudioTurnList.subList(0,maxQuestionNum));
        System.out.println("proper questions");
        return true;
    }

    private List<SelfIntroductionDetail> getSelfIntroductionDetails(int selfIntroductionId){
        SelfIntroduction selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId))
                .orElseThrow(()->new NotFoundException("해당 게시글이 존재하지 않습니다. id=" + selfIntroductionId));
        return selfIntroduction.getSelfIntroductionDetailList();
    }

    private void createQuestionForEachSelfIntroductionDetails(int requiredQuestionNum, String dept, String type, String selfIntroductionContent, QuestionObject questionObject){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());;
        System.out.println("selfIntroductionContent ; "+selfIntroductionContent);
        boolean isQuestionCreatedNormally=false;
        int callNum=0;

        //1. 프롬프트 세팅
        List<Map<String,String>> message = setPrompt(requiredQuestionNum,dept,type,selfIntroductionContent);
        String messageToJson;
        try {
            messageToJson=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String prompt="messages="+messageToJson+selfIntroductionContent;
        //입력은 chat으로 주지 않는게 가장 나은 듯.
        System.out.println(prompt);

        //2. requestBody 만들기
        String requestBody=createRequestBody(prompt);

        //3. 헤더 세팅
        HttpEntity<String> requestEntity = setHeader(requestBody);

        //질문 생성 요청 이전에 질문 수
        int prevQuestionNum = questionObject.questionList.size();
        //현재 detail에 대한 생성된 질문 수
        int currentQuestionNum = questionObject.questionList.size() - prevQuestionNum;
        // 잘못된 형식의 답변이 생성되면 재요청
        while(!isQuestionCreatedNormally && callNum<3 && currentQuestionNum < requiredQuestionNum) {
            System.out.println("callNum : "+callNum);
            //4. rest 통신
            ResponseEntity<String> responseEntity = template.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            String response = responseEntity.getBody();

            //5. response에서 gemini 답변 부분만 가져오기
            String textContent = getTextContent(response);

            //6. 답변 속 질문을 파싱하여 List 형태로 저장
            isQuestionCreatedNormally=getQuestions(requiredQuestionNum, textContent, type, (requiredQuestionNum - currentQuestionNum), questionObject);

            currentQuestionNum = questionObject.questionList.size() - prevQuestionNum;
            callNum+=1;
        }
    }

    public Map<String, List<List<Map<String,String>>>> createQuestion(int requiredQuestionNum, int deptNum, int selfIntroductionId, List<String> additionalQuestions){

        int totalDetailNum, eachQuestionNum, remainQuestionNum;
        int professionalQuestionNum = 1;

        List<Map<String,String>> message=new ArrayList<>();

        QuestionObject questionObject = new QuestionObject();
        questionObject.questionList = new ArrayList<>();
        questionObject.followupNum = 0;

        // dept 추출
        String dept;
        if(deptNum<deptName.size())
            dept = deptName.get(deptNum);
        else
            dept = "기타 프로그램 개발";

        Map<String, List<String>> interviewQuestions;
        //미리 뽑아 놓은 질문 넣기
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("interviewQuestion.json");
            interviewQuestions = mapper.readValue(resource.getInputStream(), Map.class);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> professionalQuestions = interviewQuestions.get(dept);
        Random random = new Random();
        int randomIndex = random.nextInt(professionalQuestions.size());
        String professionalQuestion = professionalQuestions.get(randomIndex);

        //미리 뽑아 놓은 질문 additionalQuestions에 추가하기
        additionalQuestions.addFirst(professionalQuestion);
        //생성할 질문 수 줄이기
        requiredQuestionNum = requiredQuestionNum - professionalQuestionNum;

        // selfIntroduction에 포함된 selfIntroductionDetails를 가져옴
        List<SelfIntroductionDetail> selfIntroductionDetails=getSelfIntroductionDetails(selfIntroductionId);

        // 문항 개수
        totalDetailNum = selfIntroductionDetails.size();
        // 문항별 질문 개수
        eachQuestionNum = requiredQuestionNum / totalDetailNum;
        // 마지막 문항 질문 = 문항별 질문 개수 + 나누고 남은 질문
        remainQuestionNum = requiredQuestionNum % totalDetailNum + eachQuestionNum;

        int index = 0;
        for (SelfIntroductionDetail selfIntroductionDetail : selfIntroductionDetails) {
            //마지막 문항은 남아있는 질문 개수만큼 할당
            if (index == totalDetailNum - 1)
                createQuestionForEachSelfIntroductionDetails(remainQuestionNum, dept, selfIntroductionDetail.getType(), selfIntroductionDetail.getContent(), questionObject);
                //이전 문항은 문항별 개수만큼 할당
            else
                createQuestionForEachSelfIntroductionDetails(eachQuestionNum, dept, selfIntroductionDetail.getType(), selfIntroductionDetail.getContent(), questionObject);
            index++;
        }

        //유저가 추가한 질문에 대해서도 저장
        additionalQuestions.forEach(s->{
            List<Map<String, String>> questionInfo = new ArrayList<>();

            Map<String, String> questionTextMap = new HashMap<>();
            questionTextMap.put("Text", s);
            questionInfo.add(questionTextMap);

            Map<String, String> questionAudioMap = new HashMap<>();
            byte[] audioData = textToSpeechService.convertTextToSpeech(s);
            String audioBase64 = Base64.getEncoder().encodeToString(audioData);
            questionAudioMap.put("Audio", audioBase64);
            questionInfo.add(questionAudioMap);

            Map<String, String> questionTurnMap = new HashMap<>();
            questionTurnMap.put("Turn", "1");
            questionInfo.add(questionTurnMap);

            questionObject.questionList.add(questionInfo);
        });
        
        //질문 순서 섞기
        Collections.shuffle(questionObject.questionList);

        questionObject.questionList.subList(0, requiredQuestionNum);
        // 생성된 모든 질문들을 JSON 형태로 저장한 후 리턴
        Map<String, List<List<Map<String,String>>>> questionToJson=new HashMap<>();
        questionToJson.put("questions",questionObject.questionList);

        //생성된 모든 질문들을 DB에 저장
        saveAllQuestions(selfIntroductionId, questionObject);

        return questionToJson;
    }

    public Map<String,Object> createFollowUpQuestion(int turn, int deptNum, int selfIntroductionId, List<Map<String,String>>prevChat, String userAudio){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        List<Map<String,String>> message;
        SelfIntroduction selfIntroduction;
        Boolean isQuestionCreatedNormally=false;
        int callNum=0;

        QuestionObject questionObject = new QuestionObject();
        questionObject.questionList = new ArrayList<>();
        questionObject.followupNum = 0;

        // dept 추출
        String dept;
        if(deptNum<deptName.size())
            dept = deptName.get(deptNum);
        else
            dept = "기타 프로그램 개발";

        // 1. turn을 계산한다.
        if(turn+1>2) {
            //마지막 꼬리질문에 대한 유저 응담
            if(turn+1==3){
                //0. selfIntroduction을 가져온다.
                selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId)).orElseThrow(()->new NotFoundException("해당하는 자소서를 찾을 수 없습니다."));

                //1. 후보 꼬리 질문들 중 유저가 선택한 질문(유저의 응답 직전 시스템 질문)을 가져온다.
                String questionContent=prevChat.get(prevChat.size()-1).get("content");

                //2. 새로운 Question Entity를 생성한다.
                Question question=Question.builder()
                        .content(questionContent)
                        .selfIntroduction(selfIntroduction)
                        .build();

                //3. DB에 question을 저장한다.
                questionRepository.save(question);
            }
            return null;
        }

        // 2. 이전 질문들과 새로운 요구사항을 붙여서 프롬프트를 생성한다.
        message = setPromptForFollowUp(turn,selfIntroductionId,dept,prevChat,userAudio);
        String messageToJson;
        try {
            messageToJson=objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("gemini output");
        message.forEach(m->{
            System.out.println(m);
        });

        //5. message에서 3에서 추가한 시스템 프롬프트 챗을 제거한다. (프론트에게 전달하기 위함)
        message.removeLast();
        String prompt="messages"+messageToJson;

        //3. requestBody 만들기
        String requestBody=createRequestBody(prompt);

        //4. 헤더 세팅
        HttpEntity<String> requestEntity = setHeader(requestBody);

        // 잘못된 형식의 답변이 생성되면 재요청
        while(!isQuestionCreatedNormally && callNum<3) {
            //5. rest 통신
            ResponseEntity<String> responseEntity = template.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            String response = responseEntity.getBody();

            //6. response에서 gemini 답변 부분만 가져오기
            String textContent = getTextContent(response);

            //7. 질문 목록 파싱하여 리스트로 저장하기
            isQuestionCreatedNormally=getQuestions(3, textContent, "", 3, questionObject);
            callNum+=1;
        }

        //8. 생성된 모든 질문들을 JSON 형태로 저장한 후 리턴
        Map<String, Object> questionToJson=new HashMap<>();
        questionToJson.put("followUps",questionObject.questionList);
        questionToJson.put("messages",message);
        questionToJson.put("turn",String.valueOf(turn+1));
        return questionToJson;
    }

    private String createRequestBody(String prompt){
        ApiRequestBody apiRequestBody = ApiRequestBody.builder()
                .contents(Arrays.asList(ApiRequestBody.Contents.builder()
                                .parts(
                                        Arrays.asList(ApiRequestBody.PartElem.builder()
                                                .text(prompt)
                                                .build()
                                        )
                                )
                                .build()
                        )
                )
                .safetySettings(Arrays.asList(ApiRequestBody.SafetySettings.builder()
                                .category("HARM_CATEGORY_DANGEROUS_CONTENT")
                                .threshold("BLOCK_ONLY_HIGH")
                                .build()
                        )
                )
                .generationConfig(ApiRequestBody.GenerationConfig.builder()
                        .stopSequences(Collections.emptyList())
                        .temperature(1.5f)
                        .maxOutputTokens(800)
                        .topP(0.8f)
                        .topK(10)
                        .build()
                )
                .build();

        return convertToJson(apiRequestBody);
    }

    public String convertToJson(ApiRequestBody apiRequestBody){
        try {
            return objectMapper.writeValueAsString(apiRequestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveAllQuestions(int selfIntroductionId, QuestionObject questionObject){
        SelfIntroduction selfIntroduction=selfIntroductionRepository.findById(Long.valueOf(selfIntroductionId)).orElseThrow(()->new NotFoundException("해당하는 자소서를 찾을 수 없습니다"));
        IntStream.range(0, questionObject.questionList.size())
                .forEach(index->{
                    Question question=Question
                            .builder()
                            .content(questionObject.questionList.get(index).get(0).get("Text"))
                            .selfIntroduction(selfIntroduction)
                            .build();
                    questionRepository.save(question);
                });
    }

}
