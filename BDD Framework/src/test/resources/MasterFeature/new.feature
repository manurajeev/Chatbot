Feature: Login Check 

Background: 
Given : User loads the "sample" json file
Scenario: To Verify valid login test case
  Given User executes test case 'TC-001' for 'Valid login scenario'
  Given User provides the url
  And User Logs in to the webpage with username and password
  Then Verify Valid user login