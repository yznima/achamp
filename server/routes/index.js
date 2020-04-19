var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
var moment = require('moment');


mongoose.connect('mongodb://localhost/AChampdb');

var EventsSchema = new mongoose.Schema({
        title: String,
        description: String,
        address: String,
        beginingDate: String,
        beginingTime: String,
        picture: String,
        received: {
            type: Date,
            default: Date.now
        }
    },
    {
        versionKey: false // You should be aware of the outcome after set to false
    });


var EventsModel = mongoose.model("EventsModel", EventsSchema);

/* GET home page. */
router.get('/', function (req, res, next) {
    res.render('index', {title: 'Express'});
});

router.post("/post", function (req, res) {

    console.log("request is", req.body);

    //making sure the object doesn't already exist

    var newEvents = new EventsModel(req.body);

    newEvents.save(function (err, user) {
        if (err) {

            console.log("could not save newly added user" + err);
        }
        else {
            // once saved, send client ok!
            res.send("ok!");
        }
    });


});







router.post("/giveupdated", function (req, res) {

    console.log("got this on the back end:", req.body);

    console.log("/giveall", "username = " + req.body.username);
    userInstance.findOne({username: req.body.username},

        function (error, user) {

            if (user) {
                console.log("user", user.lastchecked);
                EventsModel.find({


                    received: {

                        "$gte": user.lastchecked

                    }
                }, function (err, events) {

                    if (events) {
                        //console.log("events:", events);
                        res.send(events);
                    }
                    if (err) {
                        //console.log("error", err)
                    }
                });
            }
            if (error) {
                console.log("error", error);
            }
        });

    userInstance.update({username: req.body.username},
        {

            $set: {

                'lastchecked': new Date()
            }

        }, function (result, error) {
            if (result) {
                console.log("result on update:", result);
            }
            if (error) {

                console.log("error on update:", error);


            }

        }
    );

});





var friend = new mongoose.Schema({
    username: {
        type: String,
        required: true 
    }
});

var friendModel = mongoose.model("friendModel", friend);

var user = new mongoose.Schema({
    name: String,
    lastname: String,
    username: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true,
        unique: false,
    },
    email: {
        type: String,
        required: true,
        unique: true
    },
    lastchecked: {
        type: Date,
        default: Date.now
    },
    friends:[friend]
});


router.post("/getfriends", function(req, res){
    userInstance.findOne({username: req.body.username}, function(err, user){
        //If the user exist
        if(user){
            if(user.password == req.body.password)
            { 
                console.log("user.friends = "+ user);
                res.send(user.friends);
            }
            else
            {
                console.log("Wrong username or password");
                res.sendStatus(404);
            }
        }
        else{
            console.log("Couldn't find user: " + req.body.username);
            res.sendStatus(404);
        }
    })
})

function userExists(arr, username) {
  return arr.some(function(el) {
    return el.username === username;
  }); 
}

router.post("/addfriends", function(req, res){
    userInstance.findOne({username: req.body.username}, function(err, user){
        //If the user exist
        if(user){
            if(user.password == req.body.password)
            { 
                userInstance.findOne({username: req.body.seconduser}, function(err, seconduser){
                    if(seconduser && !userExists(user.friends, seconduser.username))
                    {
                        var newFriend = new friendModel();
                        newFriend._id = seconduser._id; 
                        newFriend.username = seconduser.username;
                        user.friends.push(newFriend);
                        user.save();
                        console.log("added user" + seconduser.username + " as a friend of " + user);
                        res.sendStatus(200);
                    }
                    else{
                        res.sendStatus(404);
                    }
                });
            }
            else
            {
                console.log("Wrong username or password");
                res.sendStatus(404);
            }
            
        }
        else{
            console.log("Couldn't find user: " + req.body.username);
            res.sendStatus(404);
        }
    })
})

router.post("/deleteFriend",  function(req, res){
    userInstance.findOne({username: req.body.username}, function(err, user){
        //If the user exist
        if(user){
            if(user.password == req.body.password)
            { 
                userInstance.findOne({username: req.body.seconduser}, function(err, seconduser){
                    if(seconduser && userExists(user.friends, seconduser.username))
                    {
                        var newFriend = new friendModel();
                        newFriend._id = seconduser._id; 
                        newFriend.username = seconduser.username;
                        user.friends.pull(newFriend);
                        user.save();
                        res.sendStatus(200);
                    }
                    else{
                        res.sendStatus(404);
                    }
                });
            }
            else
            {
                console.log("Wrong username or password");
                res.sendStatus(404);
            }
            
        }
        else{
            console.log("Couldn't find user: " + req.body.username);
            res.sendStatus(404);
        }
    })
})

router.post("/getAllUsers", function(req, res){
    userInstance.find({}, function(err, listUser){
        if(listUser){
            var unList = [];
            for(var x in listUser)
            {
                console.log("Problem Loading list of users", listUser[x].username);
                var newFriend = {
                    username:listUser[x].username
                };
                unList[x] = newFriend;
            }
            res.send(unList);
        }
        else
        {
            console.log("Problem Loading list of users");
            res.sendStatus(402);
        }
    })
})
//////////////////////////////////////////////////////////////////////////////////////
var userInstance = mongoose.model("userINstance", user);

router.post("/signup", function (req, res) {

    console.log("request is", req.body);

    //making sure the object doesn't already exist
    userInstance.findOne({email: req.body.email, username: req.body.username}, function (err, user) {

        //if exists send the client [null]
        if (user) {
            console.log("could not save newly added user" + user);
            res.sendStatus(403);

        }

        //if not , create a new object and save it in the db
        else {
            var newUser = new userInstance(req.body);

            newUser.save(function (err, user) {
                if (err) {
                    res.sendStatus(404);
                }
                else {
                    // once saved, send client ok!
                    res.sendStatus(200);
                }
            });


        }

    })

})


router.post("/login", function (req, res) {

    console.log("request is", req.body);

    //making sure the object doesn't already exist
    userInstance.findOne({username: req.body.username}, function (err, user) {
        
        //if exists send the client [null]
        if (user) {
            if (user.password == req.body.password) {
                console.log("/login", "successful login for:" + user);
                res.sendStatus(200);
            }
            else {
                console.log("/login", "wrong password for user:" + req.body.username);
                res.sendStatus(403);
            }
        }

        //if not , create a new object and save it in the db
        else {
            console.log("/login", "couldn't find user" + req.body);
            res.sendStatus(500);
        }

    })

})

router.post("/givefuture", function (req, res) {



    var userobjects = [];
    EventsModel.find({}, function (err, events) {
        if (events) {

            for(var i = 0; i < events.length; i++)
            {
                console.log("events begining date" ,  events[i].beginingDate+" T "+events[i].beginingTime.substring(0,5)+":00");
                var mom = new moment(events[i].beginingDate +" "+ events[i].beginingTime.substring(0,5)+":00");
                console.log("moment is valie", mom.isAfter(moment()));
                if(mom.isValid() && mom.isAfter(moment()))
                {
                    userobjects.push(events[i])
                }
            }
            res.send(userobjects);
        }
        if (err) {
            console.log("/givefuture: err", err);
            res.sendStatus(400);
        }
    });


   userInstance.update({username: req.body.username},
        {

            $set: {

                'lastchecked': new Date()
            }

        }, function (result, error) {
            if (result) {
                console.log("result on update:", result);
            }
            if (error) {

                console.log("error on update:", error);


            }

        }
    );

});

router.post("/newevents", function (req, res) {

    console.log("got this on the back end:", req.body);

    console.log("/giveall", "username = " + req.body.username);
    userInstance.findOne({username: req.body.username},

        function (error, user) {

            if (user) {
                console.log("user", user.lastchecked);
                EventsModel.find({


                    received: {

                        "$gte": user.lastchecked

                    }
                }, function (err, events) {

                    if (events) {
                        var json = JSON.stringify({
                            number : events.length
                        });
                        console.log("json", json);
                        res.end(json);
                    }
                    if (err) {
                        //console.log("error", err)
                    }
                });
            }
            if (error) {
                console.log("error", error);
            }
        });
});
module.exports = router;
