const express = require('express')
const app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
const testFolder = __dirname + '\\BDD Framework\\src\\test\\resources\\MasterFeature';

//Socket Client-Server Communication
io.on('connection', function(socket) {
    socket.on('message', function(data) {
        console.log(data);
        const fs = require('fs');
        fs.writeFile(testFolder+"\\new.feature", "Feature: Login Check \n\nBackground: \nGiven : User loads the \"sample\" json file", function(err) {
         if(err) {
            return console.log(err);
        }

        console.log("The file was saved!");
         }); 
     });
     socket.on('validLogin', function(data) {
        console.log(data);
        const fs = require('fs');
        fs.appendFile(testFolder+'\\new.feature', data, 'utf8',
        function(err) { 
            if (err) throw err;
            console.log("Data is appended to file successfully.")
        });
        console.log("The file was updated with valid login!");
          
     });
     socket.on('invalidLogin', function(data) {
        console.log(data);
        const fs = require('fs');
        fs.appendFile(testFolder+'\\new.feature', data, 'utf8',
        function(err) { 
            if (err) throw err;
            console.log("Data is appended to file successfully.")
        });
        console.log("The file was updated with invalid login!");
        
     });
     socket.on('execute', function(data) {
        console.log(data);
        const fs = require('fs');
        var features = '';
        fs.readdirSync(testFolder).forEach(file => {
            features = features + file + ',';
        });
        socket.emit('featureNames',features);
     });
     socket.on('executeFeature', function(data) {
        console.log(data);
          
     });
});

//Rendering front end files
app.use(express.static(__dirname+'/Chatbot new'));

http.listen(8080, () => {
    console.log('App listening on port 8080');
    console.log(__dirname);
});

app.get('/create', function(req, res) {
    console.log(req.body);
    //res.send('Feature file done');
    res.sendStatus(200);
    // sending a response does not pause the function
    foo();
  });

  var foo = function() {
    console.log('Clickedfeature');
  };