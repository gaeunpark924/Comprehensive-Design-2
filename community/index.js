var express = require('express');
var mongoose = require('mongoose');
var bodyParser = require('body-parser'); 
var methodOverride = require('method-override');
var flash = require('connect-flash');
var session = require('express-session'); //접속자 구분을 위해 필요
var passport = require('./config/passport');
var util = require('./util');
var app = express(); //express 실행하여 app object 초기화

// DB setting
mongoose.connect(process.env.MONGO_DB);
var db = mongoose.connection;
db.once('open', function(){ 
  console.log('DB connected'); 
});
db.on('error', function(err){ 
  console.log('DB ERROR : ', err); 
});

// Other settings
app.set('view engine', 'ejs'); //ejs set
app.use(express.static(__dirname+'/public')); // '현재위치//public'route를 static폴더로 지정 
app.use(bodyParser.json()); //json형식의 데이터를 받음
app.use(bodyParser.urlencoded({extended:true})); //urlencoded data를 extended 알고리즘을 사용해서 분석
app.use(methodOverride('_method')); //_method의 query로 들어오는 값으로 HTTP method바꿈
app.use(flash());
app.use(session({secret:'MySecret', resave:true, saveUninitialized:true}));

//Passport
app.use(passport.initialize());
app.use(passport.session());

// Custom Middlewares
app.use(function(req,res,next){
  res.locals.isAuthenticated = req.isAuthenticated(); //현재 로그인 되어있는지 return
  res.locals.currentUser = req.user; //로그인되면 session으로부터 user를 deserialize하여 생성
  next();
});

// Routes
app.use('/', require('./routes/home'));
app.use('/posts_Seoul', util.getPostQueryString, require('./routes/posts_Seoul'));
app.use('/posts_Gyeonggi', util.getPostQueryString, require('./routes/posts_Gyeonggi'));
app.use('/posts_CC', util.getPostQueryString, require('./routes/posts_CC'));
app.use('/posts_GW', util.getPostQueryString, require('./routes/posts_GW'));
app.use('/posts_JL', util.getPostQueryString, require('./routes/posts_JL'));
app.use('/posts_GS', util.getPostQueryString, require('./routes/posts_GS'));
app.use('/posts_JJ', util.getPostQueryString, require('./routes/posts_JJ'));
app.use('/users', require('./routes/users'));
app.use('/comments', util.getPostQueryString, require('./routes/comments'));


// Port setting
var port = 3000;
app.listen(port, function(){ //port변수를 이용하여 3000번 포트에 node.js서버 연결
  console.log('server on! http://localhost:'+port); //서버가 실행되면 콘솔창에 표시될 메세지
});