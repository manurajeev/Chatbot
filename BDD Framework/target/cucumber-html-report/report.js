$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("sample_Updated.feature");
formatter.feature({
  "line": 1,
  "name": "Verify the New Contract",
  "description": "User should be able to verify the Payment service provider dropdown values and Sales Channel dropdown values",
  "id": "verify-the-new-contract",
  "keyword": "Feature"
});
formatter.background({
  "line": 4,
  "name": "",
  "description": "",
  "type": "background",
  "keyword": "Background"
});
formatter.step({
  "line": 5,
  "name": ": User loads the \"sample\" json file",
  "keyword": "Given "
});
formatter.step({
  "line": 6,
  "name": "User provides the url",
  "keyword": "Given "
});
formatter.match({
  "arguments": [
    {
      "val": "sample",
      "offset": 18
    }
  ],
  "location": "CommonStepDefinitions.getDataFileName(String)"
});
formatter.result({
  "duration": 6907489868,
  "status": "passed"
});
formatter.match({
  "location": "CommonStepDefinitions.launchApplication()"
});
formatter.result({
  "duration": 5993448694,
  "status": "passed"
});
formatter.scenario({
  "comments": [
    {
      "line": 7,
      "value": "#Given User Enter username and password and LogIn to application"
    }
  ],
  "line": 9,
  "name": "To Verify whether E-Hub field is displaying in details tab",
  "description": "",
  "id": "verify-the-new-contract;to-verify-whether-e-hub-field-is-displaying-in-details-tab",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 10,
  "name": "User executes test case \u0027TC-1235\u0027 for \u0027name of the test case\u0027",
  "keyword": "Given "
});
formatter.step({
  "line": 11,
  "name": "User is on the home page of the application",
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "TC-1235",
      "offset": 25
    },
    {
      "val": "name of the test case",
      "offset": 39
    }
  ],
  "location": "CommonStepDefinitions.getMultipleTestcaseIdScenarioName2(String,String)"
});
formatter.result({
  "duration": 267786058,
  "status": "passed"
});
formatter.match({
  "location": "CommonStepDefinitions.homePage()"
});
formatter.result({
  "duration": 3892775345,
  "status": "passed"
});
formatter.after({
  "duration": 19925393,
  "status": "passed"
});
});