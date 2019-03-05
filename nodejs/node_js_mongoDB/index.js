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
var allPackets = [];

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

//Receiving JSON from java Server
app.post('/devicesPacket',function(req,res,next){
	console.log('/devicesPacket');
	var post_data=req.body;
	var devices = JSON.parse(post_data.details);
	console.log(devices);
//Store all device into an array
	allPackets.push(devices);
});


app.get('/getDeviceArray',function(req,res,next){
	console.log('/getDeviceArray');
	var tmp = {};
	tmp["allPackets"] = allPackets;
//Store all device into an array
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(tmp));
});

app.post('/deviceLocation',function(req,res,next){
	console.log('/deviceLocation');
	var tmp = req.body;
	var newJson = JSON.parse(tmp['Details']);
	console.log(newJson);
//Store all device into an array
    	res.json();
});


app.get('/seats',function(req,res){
	console.log("/seats");
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection('student').findOne({"userName":"s1234567"},function(err,doc){
			client.close();
			res.json(doc);

			
		});
	});
});


app.get('/seatStatus',function(req,res){

	console.log("/seatStatus");
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection('seatStatus').findOne({"place_id":req.query.place_id},function(err,doc){
			console.log(req.query.place_id);
			
			console.log(doc);
			client.close();
			res.json(doc);
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


app.post('/booking',function(req,res,next){
	console.log('/booking');
	var post_data=JSON.parse(req.body.data);
	console.log(post_data);
	insert("booking",post_data,function(){
		console.log("Booking successful");
		var criteria=JSON.parse('{"place":"'+post_data["place"]+'"}');
		console.log(criteria);
		findOne("seatStatus",criteria,function(doc){
			console.log(doc);
		//var newNumber=Number(doc["booking"])
		var bookingPeople='{"booking":'+Number(doc["booking"]+1)+'}';
		console.log(bookingPeople);
		update("seatStatus",doc,bookingPeople,function(){});
		res.json();
		});
		
	});

});

app.get('/checkBooking',function(req,res){
	console.log("/checkBooking");
	var myDate = Date.now();
	console.log(myDate);
	checkBooking(myDate,function(){
		
		findBookingNumber(function(temp){
				updateBooking(temp);
				res.json();
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
	var lowerCheck=lowerFloor.length;
	var upperCheck=upperFloor.length;
	for(var i=0;i<lowerFloor.length;i++){
		lowerCheck--;
		lowerFloorDistance=currentFloor-lowerFloor[i].floor;
		//console.log(lowerFloor[i])
		for(var x=0;x<upperFloor.length;x++){
			upperCheck--;
			upperFloorDistance=upperFloor[x].floor-currentFloor;
			if(lowerFloorDistance<=upperFloorDistance){
				//console.log(lowerFloor[i]);
				result.push(lowerFloor[i]);
				break;
			}else{
				//console.log(upperFloor[x]);
				result.push(UpperFloor[x]);
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
		if((lowerCheck==0&&upperCheck==0)||result.length==3){
			callBack(result);
				return;
		}
		if(lowerCheck==0&&upperCheck!=0){
			result.push(upperFloor.shift());
		}else if(lowerCheck!=0&&upperCheck==0){
			result.push(lowerFloor.shift());
		}
	}
}

/*function askTensorForPosition(obj,callback){
	//In the format of {"sourceMac":[{packet1},{packet2},{packet3}]}
	//INSERT CODE for actually asking for position

	var occupancy = {}; // a virtual tmp for storing location occupancy
	occupancy["C4A"] = 0;
	occupancy["C4B"] = 0;
	occupancy["C4C"] = 0;

	for(var device in obj){
		var randomInt	 = getRandomInt(3);
		var randomLocation;
		if(randomInt == 0){
			randomLocation = "C4A"
		}else if(randomInt == 1){
			randomLocation = "C4B"
		}else{
			randomLocation = "C4C"
		}
		occupancy[randomLocation]=occupancy[randomLocation]+1;

	}
	callback(occupancy);
}*/

function getRandomInt(max) {
	return Math.floor(Math.random() * Math.floor(max));
}

function findOne(collection,criteria,callback){
	MongoClient.connect(url,function(err,client){
		
		var db=client.db('kwongkalai');
		db.collection(collection).findOne(criteria,function(err,doc){
			console.log(doc);
			client.close();
			callback(doc);
		});
	});
};

function insert(collection,data,callback){
	console.log(data);
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection(collection).insertOne(data,function(err){
			if(err) throw err;
			client.close();
			callback();
		});
	});
};

function update(collection,criteria,newData,callback){
	console.log('{"$set":'+newData+'}');
	var set = JSON.parse('{"$set":'+newData+'}');
	console.log("AAasdadsAA");
	console.log(set);
	MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection(collection).updateOne(criteria,set,function(err){
			if(err) throw err;
			client.close();
			callback();
		});
	});
	
};

function remove(collection,criteria,callback){
		MongoClient.connect(url,function(err,client){
		var db=client.db('kwongkalai');
		db.collection(collection).deleteOne(criteria,function(err,obj){
			if(err) throw err;
			client.close();
			callback();
		});
	});
}



function checkBooking(myDate,callback){
	MongoClient.connect(url, function(err, client){
  				if (err) throw err;
 				var db=client.db('kwongkalai');
 				 db.collection("booking").find().toArray(function(err, result) {
   		 		if (err) throw err;
				//console.log(result);
				if(result.length!=0){
					var test=0;
				result.forEach(function(x){
					
					if(myDate-Number(x.BookingTime)>60000){
						test++;
						console.log('{"place":"'+x.place+'"}');
						var criteria=JSON.parse('{"place":"'+x.place+'"}');
						remove("booking",x,function(){
							console.log(x+"DELETE!!!!!!");
							test--;
							if(test==0){
								console.log("11111111111");
								client.close();
								callback();
							}
							//findOne("seatStatus",criteria,function(doc){
							//var newBookingValue='{"booking":'+Number(doc["booking"]-1)+'}';
							//update("seatStatus",doc,newBookingValue,function(){});
							//console.log(x+"UPDATED!!!!!!!!");
							//});
							
							
						});
						
						
					}
					
  				});
				
				}
		});
		
	});
}

function findBookingNumber(callback){
	var temp={};
	MongoClient.connect(url, function(err, client){
	var db=client.db('kwongkalai');
	db.collection("booking").find().toArray(function(err, result) {
	for (x in result){
		console.log(result[x].place);
		//for(key in result[x]){
		if(temp[result[x].place]==null){
			temp[result[x].place]=1;
		}else{
			temp[result[x].place]++;
		}
		if(x==result.length-1){
			console.log(temp);
			client.close();
			callback(temp);
		}
		//}
	}
	
});
	});
}

function updateBooking(jsonObject){
	MongoClient.connect(url, function(err, client){
		var db=client.db('kwongkalai');
		//var bookingToZero='{"booking":'+ 0 +'}'
		//var set = JSON.parse('{"$set":'+newData+'}');
		
	db.collection("seatStatus").updateMany({},JSON.parse('{"$set":{"booking":'+ 0 +'}}'),function(){
		
	for (key in jsonObject){
		console.log('{"place":"'+key+'"}');
		console.log('{"booking":'+ jsonObject[key] +'}');
	var newBookingValue='{"booking":'+ jsonObject[key] +'}';
	update("seatStatus",JSON.parse('{"place":"'+key+'"}'),newBookingValue,function(){});
	}
	client.close();
	});
	
});
}

app.listen(3000||process.env.PORT,function(){
				console.log("Connected to MongoDB Server");
			});
