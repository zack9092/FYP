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
			app.listen(3000||process.env.PORT,function(){
				console.log("Connected to MongoDB Server");
			});
		}
});