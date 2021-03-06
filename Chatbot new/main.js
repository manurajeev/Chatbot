var sendForm = document.querySelector('#chatform'),
    textInput = document.querySelector('.chatbox'),
    chatList = document.querySelector('.chatlist'),
    userBubble = document.querySelectorAll('.userInput'),
    botBubble = document.querySelectorAll('.bot__output'),
    animateBotBubble = document.querySelectorAll('.bot__input--animation'),
    overview = document.querySelector('.chatbot__overview'),
    hasCorrectInput,
    imgLoader = false,
    animationCounter = 1,
    animationBubbleDelay = 600,
    input,
    previousInput,
    isReaction = false,
    unkwnCommReaction = "I didn't quite get that.",
    chatbotButton = document.querySelector(".submit-button")
    var features = [];
    
   
sendForm.onkeydown = function(e){
  if(e.keyCode == 13){
    e.preventDefault();

    //No mix ups with upper and lowercases
    var input = textInput.value.toLowerCase();

    //Empty textarea fix
    if(input.length > 0) {
      createBubble(input)
    }
  }
};

sendForm.addEventListener('submit', function(e) {
  console.log('Suvmitted form');
  //so form doesnt submit page (no page refresh)
  e.preventDefault();

  //No mix ups with upper and lowercases
  var input = textInput.value.toLowerCase();

  //Empty textarea fix
  if(input.length > 0) {
    createBubble(input)
  }
}) //end of eventlistener

var createBubble = function(input) {
  //create input bubble
  var chatBubble = document.createElement('li');
  chatBubble.classList.add('userInput');

  //adds input of textarea to chatbubble list item
  chatBubble.innerHTML = input;

  //adds chatBubble to chatlist
  chatList.appendChild(chatBubble)

  checkInput(input);
}

var checkInput = function(input) {
  hasCorrectInput = false;
  isReaction = false;
  //Checks all text values in possibleInput
  for(var textVal in possibleInput){
    //If user reacts with "yes" and the previous input was in textVal
    if(input == "yes" || input.indexOf("yes") >= 0){
      if(previousInput == textVal) {
        console.log("sausigheid");

        isReaction = true;
        hasCorrectInput = true;
        botResponse(textVal);
      }
    }
    if(input == "no" && previousInput == textVal){
      unkwnCommReaction = "For a list of commands type: Commands";
      unknownCommand("I'm sorry to hear that :(")
      unknownCommand(unkwnCommReaction);
      hasCorrectInput = true;
    }
    //Is a word of the input also in possibleInput object?
    if(input == textVal || input.indexOf(textVal) >=0 && isReaction == false){
			console.log("succes");
      hasCorrectInput = true;
      botResponse(textVal);
		}
	}
  //When input is not in possibleInput
  if(hasCorrectInput == false){
    console.log("failed");
    unknownCommand(unkwnCommReaction);
    hasCorrectInput = true;
  }
}

// debugger;

function botResponse(textVal) {
  //sets previous input to that what was called
  // previousInput = input;

  //create response bubble
  var userBubble = document.createElement('li');
  userBubble.classList.add('bot__output');

  if(isReaction == true){
    if (typeof reactionInput[textVal] === "function") {
    //adds input of textarea to chatbubble list item
      userBubble.innerHTML = reactionInput[textVal]();
    } else {
      userBubble.innerHTML = reactionInput[textVal];
    }
  }

  if(isReaction == false){
    //Is the command a function?
    if (typeof possibleInput[textVal] === "function") {
      // console.log(possibleInput[textVal] +" is a function");
    //adds input of textarea to chatbubble list item
      userBubble.innerHTML = possibleInput[textVal]();
    } else {
      userBubble.innerHTML = possibleInput[textVal];
    }
  }
  //add list item to chatlist
  chatList.appendChild(userBubble) //adds chatBubble to chatlist

  // reset text area input
  textInput.value = "";
}

function unknownCommand(unkwnCommReaction) {
  // animationCounter = 1;

  //create response bubble
  var failedResponse = document.createElement('li');

  failedResponse.classList.add('bot__output');
  failedResponse.classList.add('bot__output--failed');

  //Add text to failedResponse
  failedResponse.innerHTML = unkwnCommReaction; //adds input of textarea to chatbubble list item

  //add list item to chatlist
  chatList.appendChild(failedResponse) //adds chatBubble to chatlist

  animateBotOutput();

  // reset text area input
  textInput.value = "";

  //Sets chatlist scroll to bottom
  chatList.scrollTop = chatList.scrollHeight;

  animationCounter = 1;
}

function responseText(e) {

  var response = document.createElement('li');

  response.classList.add('bot__output');

  //Adds whatever is given to responseText() to response bubble
  response.innerHTML = e;

  chatList.appendChild(response);

  animateBotOutput();

  console.log(response.clientHeight);

  //Sets chatlist scroll to bottom
  setTimeout(function(){
    chatList.scrollTop = chatList.scrollHeight;
    console.log(response.clientHeight);
  }, 0)
}

function responseImg(e) {
  var image = new Image();

  image.classList.add('bot__output');
  //Custom class for styling
  image.classList.add('bot__outputImage');
  //Gets the image
  image.src = "/images/"+e;
  chatList.appendChild(image);

  animateBotOutput()
  if(image.completed) {
    chatList.scrollTop = chatList.scrollTop + image.scrollHeight;
  }
  else {
    image.addEventListener('load', function(){
      chatList.scrollTop = chatList.scrollTop + image.scrollHeight;
    })
  }
}

//change to SCSS loop
function animateBotOutput() {
  chatList.lastElementChild.style.animationDelay= (animationCounter * animationBubbleDelay)+"ms";
  animationCounter++;
  chatList.lastElementChild.style.animationPlayState = "running";
}

function commandReset(e){
  animationCounter = 1;
  previousInput = Object.keys(possibleInput)[e];
  console.log(previousInput);
}

// Starting message
window.onload = function(){
var d = new Date();
var time = d.getHours();
var salutation = "";
if (time < 12) {
  salutation = "Good Morning";
}
if (time >= 12 && time < 16) {
  salutation = "Good Afternoon";
}
if (time >= 16) {
  salutation = "Good Evening";
}
  console.log(salutation);
  document.getElementById('first_output').innerHTML = 'Hey, '+salutation+ ', I\'m Autobot!';
};

var possibleInput = {
  "help" : function(){
    responseText("You can type a command in the chatbox")
    responseText("Something like &quot;Navvy, please show me Mees&rsquo; best work&quot;")
    responseText("Did you find a bug or problem? Tweet me @MeesRttn")
    commandReset(0);
    return
    },
  "best work" : function(){
    responseText("I will show you Mees' best work!");
    responseText("These are his <a href='#animation'>best animations</a>")
    responseText("These are his <a href='#projects'>best projects</a>")
    responseText("Would you like to see how I was built? (Yes/No)")
    commandReset(1);
    return
    },
  "about" : function(){
    responseText("This is me, Autobot");
    responseText("I'm here to help you with yout Automation needs");
    responseText("These are the operations I can perform right now");
    responseText("<ul><li class='input__nested-list'><button id='create' class='btn' onclick=\"createFeature()\"> Create a new feature file </button></li><li class='input__nested-list'><button id='execute' class='btn' onclick=\"executeFeature()\"> Execute a feature file </button></li> </ul>");
    commandReset(1);
    return
    },
  "Creating Feature File" : function(){
    responseText("Feature File Created Successfully!!");
    responseText("Below are the test case Scenarios I can create <br><button id='scenarios_validlogin' class='btn' onclick='createValidScenario()'>Valid Login Test Case </button> <button id='scenarios_invalidlogin' class='btn' onclick='createInvalidScenario()'>Invalid Login Test Case </button>");
    commandReset(2);
    return
  }, 
  "Adding Invalid Scenario" : function(){
    responseText("Test Case with Invalid scenario Created Successfully!!");
    responseText("Do you want to execute this?");
    commandReset(4);
    return
  },
  "Adding Valid Scenario" : function(){
    responseText("Test Case with Valid scenario Created Successfully!!");
    responseText("Do you want to execute this?");
    commandReset(5);
    return
  },
  "Showing feature files" : function(){
    responseText("Which feature file do you want to execute? <br> <button class='btn' onclick=\"executeFeatureFile('"+features[0]+"')\">"+features[0]+"</button> <button class='btn' onclick=\"executeFeatureFile('"+features[1]+"')\">"+features[1]+"</button>");
  },
  "Executing sample.feature" : function(){
    socket.emit('executeFeature', 'Sample.feature');
  },
  "Executing new.feature" : function(){
    socket.emit('executeFeature', 'new.feature');
  },
  "rick roll" : function(){
    window.location.href = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
    },
  // work experience
}

var reactionInput = {
  "best work" : function(){
    //Redirects you to a different page after 3 secs
    responseText("On this GitHub page you'll find everything about Navvy");
    responseText("<a href='https://github.com/meesrutten/chatbot'>Navvy on GitHub</a>")
    animationCounter = 1;
    return
  },
  "about" : function(){
    responseText("Things I want to learn or do:");
    responseText("Get great at CSS & JS animation");
    responseText("Create 3D browser experiences");
    responseText("Learn Three.js and WebGL");
    responseText("Combine Motion Design with Front-End");
    animationCounter = 1;
    return
    },
    "Adding Valid Scenario": function(){
      responseText("Executing the feature file");
      socket.emit('execute', 'Sent an event(Execute) from the client!');
      animationCounter = 1;
      return
    },
    "Adding Invalid Scenario" : function(){
      responseText("Executing the feature file");
      socket.emit('execute', 'Sent an event(Execute) from the client!');
      animationCounter = 1;
      return
    },
}

//Creating a new feature file
function createFeature() {
  socket.emit('message', 'Sent an event from the client!');
  createBubble("Creating Feature File");
}

//Executing feature file
function executeFeature() {
  
  socket.emit('execute', 'Sent an event(execute) from the client!');
  socket.on('featureNames', function(data) {
    console.log(data);
    features = data.split(",");
    console.log("Executed client feature file");
    createBubble("Showing feature files");
 });
}

function executeFeatureFile(featureName) {
  createBubble("Executing "+featureName);
}

function createInvalidScenario() {
  socket.emit('invalidLogin', '\nScenario: To Verify valid login test case\n  Given User executes test case \'TC-002\' for \'Invalid login scenario\'\n  Given User provides the url\n  And User Logs in to the webpage with username and password\n  Then Verify Invalid user login');
  createBubble("Adding Invalid Scenario");
}

function createValidScenario() {
  socket.emit('validLogin', '\nScenario: To Verify valid login test case\n  Given User executes test case \'TC-001\' for \'Valid login scenario\'\n  Given User provides the url\n  And User Logs in to the webpage with username and password\n  Then Verify Valid user login');
  createBubble("Adding Valid Scenario");
}