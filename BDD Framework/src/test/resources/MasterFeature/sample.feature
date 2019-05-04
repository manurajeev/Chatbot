Feature: Verify the New Contract
  User should be able to verify the Payment service provider dropdown values and Sales Channel dropdown values

  Background: 
    Given : User loads the "sample" json file

  Scenario: To Verify valid login test case
    Given User executes test case 'TC-001' for 'Valid login scenario'
    Given User provides the url
    And User Logs in to the webpage with username and password
    Then Verify Valid user login

  Scenario: To Verify invalid login test case
    Given User executes test case 'TC-002' for 'Invalid login scenario'
    Given User provides the url
    And User Logs in to the webpage with username and password
    Then Verify Invalid user login