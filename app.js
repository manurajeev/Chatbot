const express = require('express')
const app = express();

 console.log(__dirname+'/Chatbot_new');
 app.use(express.static(__dirname+'/Chatbot new'));
// // app.get('/', (req, res) => {
// //   res.sendFile(__dirname + '/main.html');
  
// //   fs.readFile("not-found.txt", "utf-8", (err, data) => {
// //     if (err) { console.log(err) }
// //     console.log(data);
// // })
// // });

// app.listen(8000, () => {
//   console.log('Example app listening on port 8000!')
// });

// const fs = require('fs');
// fs.writeFile("new.feature", "Hey there!", function(err) {
//     if(err) {
//         return console.log(err);
//     }

//     console.log("The file was saved!");
// }); 


// var server = express(); // better instead
// server.configure(function(){
//   server.use(express.static(__dirname));
// });

app.listen(8080, () => {
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