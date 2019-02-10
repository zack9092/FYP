var mongodb = require('mongodb');
var ObjectID= mongodb.ObjectID;
var express = require('express');
var app= express();
var url  = require('url');
var path=require('path');
var bodyParser=require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));
app.use(express.static(path.join(__dirname,'public')));
var MongoClient = mongodb.MongoClient;
var url = 'mongodb://kwongkalai1:kwongkalai1@ds151402.mlab.com:51402/kwongkalai';


app.post('/login',function(req,res,next){
	var post_data=req.body;
	var userName=post_data.userName;
	var userPassword=post_data.userPassword;
	MongoClient.connect(url,function(err,client){
		if(err)
			console.log('Unable to connect to the mongoDB server.Error',err);
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
				client.close();
			});
		});
});

app.get('/seats',function(req,res){
	console.log("/seats");
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection('student').findOne({"userName":"s1234567"},function(err,doc){
			res.json(doc);
			
			client.close();
		});
	});
});


app.get('/seatStatus',function(req,res){

	console.log("/seatStatus");
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection('seatStatus').findOne({"place_id":req.query.place_id},function(err,doc){
			res.json(doc);
			console.log(doc);
			client.close();
		});
	});
});
	
app.get('/seatRecommended',function(req,res){
	var lowerFloor=[];
	var upperFloor=[];
	console.log("/seatRecommended");
	findLowerFloor(req.query.floor,function(result){
		lowerFloor=result;
		findUpperFloor(req.query.floor,function(result2){
			upperFloor=result2;
			findNearestSeat(req.query.floor,lowerFloor,upperFloor,function(finalResult){
				res.json(finalResult);
			});
		});
	});
});

function findLowerFloor(floor,callback){
	var array=[];
	var criteria=JSON.parse('{"floor":{"$lte":'+floor+'}}');
	console.log(criteria);
	MongoClient.connect(url, function(err, client){
  				if (err) throw err;
 				var db=client.db('kwongkalai');
 				 db.collection("seatStatus").find(criteria).sort( { floor: -1 } ).toArray(function(err, result) {
   		 		if (err) throw err;
				//console.log(result);
				for(var x in result){
					if(Number(result[x].PeopleThere)/Number(result[x].MaxSeats)<0.8){
						console.log(result[x].PeopleThere/result[x].MaxSeats);
						array.push(result[x]);
						if(array.length>=3){
							client.close();
							callback(array);
							return;
						}
					}
  				}
				client.close();
				callback(array);
		});
	
	});
}

function findUpperFloor(floor,callback){
	var array=[];
	var criteria=JSON.parse('{"floor":{"$gt":'+floor+'}}');
	console.log(criteria);
	MongoClient.connect(url, function(err, client){
  				if (err) throw err;
 				var db=client.db('kwongkalai');
 				 db.collection("seatStatus").find(criteria).sort( { floor: 1 } ).toArray(function(err, result) {
   		 		if (err) throw err;
				//console.log(result);
				for(var x in result){
					if(Number(result[x].PeopleThere)/Number(result[x].MaxSeats)<0.8){
						console.log(result[x].PeopleThere/result[x].MaxSeats);
						array.push(result[x]);
						if(array.length>=3){
						client.close();
						callback(array);
						return;
						}
					}
  				}
				client.close();
				callback(array);
		});
	
	});
}

function findNearestSeat(currentFloor,lowerFloor,upperFloor,callBack){
	if(lowerFloor.length==0){
		callBack(upperFloor);
		return;
	}
	if(upperFloor.length==0){
		callBack(lowerFloor);
		return;
	}
	var result=[];
	var lowerFloorDistance;
	var upperFloorDistance;
	for(var i=0;i<lowerFloor.length;i++){
		lowerFloorDistance=currentFloor-lowerFloor[i].floor;
		for(var x=0;x<upperFloor.length;x++){
			upperFloorDistance=upperFloor[x].floor-currentFloor;
			if(lowerFloorDistance<=upperFloorDistance){
				//console.log(lowerFloor[x]);
				result.push(lowerFloor.shift());
			}else{
				//console.log(upperFloor[x]);
				result.push(UpperFloor.shift());
			}
			if(lowerFloor.length==0){
				break;
			}
			if(result.lenght>=3){
				callBack(result);
				return;
			}
		}
	}
	while(true){
		if((lowerFloor.length==0&&upperFloor.lenght==0)||result.length==3){
			callBack(result);
				return;
		}
		if(lowerFloor.length==0&&upperFloor.lenght!=0){
			result.push(upperFloor.shift());
		}else if(lowerFloor.length!=0&&upperFloor.lenght==0){
			result.push(lowerFloor.shift());
		}
	}
}


app.listen(3000||process.env.PORT,function(){
				console.log("Connected to MongoDB Server");
			});