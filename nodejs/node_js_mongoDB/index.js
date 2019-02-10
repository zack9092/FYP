var mongodb = require('mongodb');
var ObjectID= mongodb.ObjectID;
var express = require('express');
var app= express();
var bodyParser=require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

var MongoClient = mongodb.MongoClient;
var url = 'mongodb://kwongkalai1:kwongkalai1@ds151402.mlab.com:51402/kwongkalai';

MongoClient.connect(url,function(err,client){
	if(err)
		console.log('Unable to connect to the mongoDB server.Error',err);
	else{
		app.post('/login',function(req,res,next){
			var post_data=req.body;
			var userName=post_data.userName;
			var userPassword=post_data.userPassword;
			var db=client.db('kwongkalai');
			db.collection('student').find({"userName":userName}).count(function(err,number){
				if(number==0){res.json("User Not Exists");
					console.log("User Not Exists");
				}
				else{
					db.collection('student').findOne({"userName":userName},function(err,user){
						if(user.password==userPassword){
							res.json('Login success');
							console.log('Login success');
						}
						else{
							res.json('Wrong password');
							console.log('Wrong password');
						}
					});

				}
			});

			});

			//Receiving JSON from java Server
			app.post('/devicesPosition',function(req,res,next){
				var post_data=req.body;
				console.log(post_data);
				var devices = JSON.parse(post_data.details);
				console.log(devices);
				var tmpTotalPos = {}; // a virtual tmp for storing location occupancy
				tmpTotalPos["c4a"] = 0;
				tmpTotalPos["c4b"] = 0;
				tmpTotalPos["c4c"] = 0;
				//INSERT CODE a forloop to loop through all array element
				for(var device in devices){
    			console.log(device);
					var pos = askTensorForPosition(device);
					tmpTotalPos[pos]=tmpTotalPos[pos]+1;
					console.log(tmpTotalPos);
				}
				//INSERT CODE to update database
			});

			function askTensorForPosition(obj){//In the format of {"sourceMac":[{packet1},{packet2},{packet3}]}
				//INSERT CODE for actually asking for position
				randomLocation = getRandomInt(3);
				if(randomLocation == 0){
					return "c4a"
				}else if(randomLocation == 1){
					return "c4b"
				}else{
					return "c4c"
				}
			}

			function getRandomInt(max) {
			  return Math.floor(Math.random() * Math.floor(max));
			}

			app.listen(3000||process.env.PORT,function(){
				console.log("Connected to MongoDB Server");
			});
		}
});
