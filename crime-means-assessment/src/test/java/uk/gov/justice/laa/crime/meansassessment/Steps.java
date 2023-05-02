package uk.gov.justice.laa.crime.meansassessment;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.assertj.core.api.SoftAssertions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Steps {
    JSONObject response;

    Map<String, Object> request = new HashMap<>();

    @Given("the following applicant details")
    public void theFollowingAssessmentDetails(DataTable dataTable) throws IOException {
        List<Map<String, String>> listOfData = dataTable.asMaps(String.class, String.class);
        var resultDict = listOfData.get(0);

        for (var entry: resultDict.entrySet()) {
            switch (entry.getKey()) {
                case "total_income":
                    request.put(entry.getKey(), Float.parseFloat(entry.getValue()));
                    break;
            }
        }
    }
    @Given("I POST new assessment request")
    public void postAssessmentDetails() throws IOException, ParseException {
        var client = HttpClients.createDefault();
        var httpRequest = new HttpPost("http://localhost:8080/api/internal/v1/lep-crime");
        httpRequest.addHeader("content-type", "application/json");
        var jsonMaker = new JSONObject();
        for (var input: request.entrySet()) {
            jsonMaker.put(input.getKey(), input.getValue());
        }
        jsonMaker.put("submission_date", "2022-07-06");
        var json = jsonMaker.toJSONString();
//        var json = "{\"totalIncome\":12000.0,\"submission_date\":\"2022-07-06\"}";
        var payload = new StringEntity(json);
        httpRequest.setEntity(payload);
        var httpResponse = client.execute(httpRequest);
        var parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        response = (JSONObject) parser.parse(httpResponse.getEntity().getContent());
    }

    @Given("I GET the assessment details")
    public void getAssessmentResult(DataTable dataTable) throws IOException {
        List<Map<String, String>> listOfData = dataTable.asMaps(String.class, String.class);
        var resultDict = listOfData.get(0);
        var softAssertions = new SoftAssertions();

        for (var entry: resultDict.entrySet()) {
            switch (entry.getKey()) {
                case "imaResult":
                    softAssertions.assertThat(response.getAsString("result")).isEqualTo(entry.getValue());
                    break;
            }
        }
        softAssertions.assertAll();
    }
}
