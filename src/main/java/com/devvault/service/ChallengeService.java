package com.devvault.service;

import com.devvault.config.seed.ChallengeSeedData;
import com.devvault.entity.CodeChallenge;
import com.devvault.repository.CodeChallengeRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ChallengeService {

    private final CodeChallengeRepository repository;
    private final ObjectMapper mapper;

    public ChallengeService(CodeChallengeRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Optional<CodeChallenge> findForProblem(Long problemId) {
        return repository.findByProblemId(problemId);
    }

    /** Creates or refreshes the challenge of a problem from a seed definition. */
    public void upsert(Long problemId, ChallengeSeedData.Def def) {
        CodeChallenge challenge = repository.findByProblemId(problemId).orElse(null);
        if (challenge == null) {
            repository.save(new CodeChallenge(problemId, def.functionName(), def.jsStarter(), def.javaStarter(),
                    compact(def.testsJson())));
            return;
        }
        challenge.setFunctionName(def.functionName());
        challenge.setJsStarter(def.jsStarter());
        challenge.setJavaStarter(def.javaStarter());
        challenge.setTestsJson(compact(def.testsJson()));
        repository.save(challenge);
    }

    /** The JSON the problem page hands to its script (all tests are shown to the learner, like sample cases). */
    public String clientJson(CodeChallenge challenge, boolean javaEnabled, boolean canSetStatus) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("problemId", challenge.getProblemId());
        data.put("functionName", challenge.getFunctionName());
        data.put("jsStarter", challenge.getJsStarter());
        data.put("javaStarter", challenge.getJavaStarter());
        data.put("tests", mapper.readTree(challenge.getTestsJson()));
        data.put("javaEnabled", javaEnabled);
        data.put("canSetStatus", canSetStatus);
        return mapper.writeValueAsString(data);
    }

    public long count() {
        return repository.count();
    }

    private String compact(String json) {
        return mapper.readTree(json).toString();
    }
}
