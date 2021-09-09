const express = require('express');
var count_testloc = 0;
var count = 0;
var result =[];


// 파일시스템 모듈을 이용하여 이미지를 읽은후 base64로 인코딩하기  
function base64_encode(file) {  
  // 바이너리 데이터 읽기 file 에는 파일의 경로를 지정  
  var bitmap = fs.readFileSync(file);  
  //바이너리 데이터를 base64 포멧으로 인코딩하여 스트링 획득
  return new Buffer(bitmap).toString('base64');  
}  

function getAngle(lat1, log1, lat2, log2) {
  //출발지
	var x1 = lat1 * Math.PI / 180;
	var x2 = lat2 * Math.PI / 180;
  //목적지
	var y1 = log1 * Math.PI / 180;
	var y2 = log2 * Math.PI /180;
  //위도, 경도를 라디안 단위로 변환
	var y = Math.sin(y2 - y1) * Math.cos(x2);
	var x = Math.cos(x1) * Math.sin(x2) - Math.sin(x1) * Math.cos(x2) * Math.cos(y2 - y1);
  //방위각 (라디안)
	var radian = Math.atan2(y, x);
  //방위각 (degree, 정규화)
	var bearing = (radian*180 /Math.PI + 360) % 360;
	return bearing;
};

//const port = 3000;
const config = require('./config.js');
const grabzit = require('grabzit');

const app = express();
const ejs = require("ejs");
var fs = require('fs');

app.set('views',__dirname+'/view');//템플릿 파일의 위치
app.set('view engine','ejs');//ejs 템플릿 엔진 사용

app.engine('html',require('ejs').renderFile);   

//8080 포트를 가지고 대기
const server = app.listen(8080,()=>{
//app.listen(8080,()=>{
   console.log('start server : :8080');
});

var http = require('http');
var bodyParser = require('body-parser')
app.use(bodyParser.urlencoded({extended: false}))

app.get('/roadview', function(req, res) {
    res.render('index',{'log':req.query.log, 'lat':req.query.lat, 'bearing':req.query.bearing});
    console.log('roadview call');
});

var pairList = [] 
var final_img_list =[];

//multer 모듈로 이미지 저장
const multer = require('multer')
var storage = multer.diskStorage({
  destination: function(req, file, cb){
    cb(null, './public/image')
  },
  filename: function (req, file, cb){
    cb(null, file.originalname)
  }
})
const upload = multer({storage: storage})

/* 앱에서 서버로 이미지 post */
app.post('/android/post/upload', upload.single('img'),(req,res)=>{
  res.json(req.file)
  console.log(req.file)
  //console.log(req.body)
  const obj = JSON.parse(JSON.stringify(req.body)); // req.body = [Object: null prototype] { title: 'product' }
  console.log(obj);

})


/* 앱에서 서버에 json post하길 원함 */ 
app.post('/android/post', function(req, res, next){ /* 접근 url -> ex) http://123.456.78.90:3000/post */
  console.log('client wants to post json in server');
  //console.log(req.body.locations)
  var inputData;
  var loclist;
  pairList = [];
  pairList = req.body.locations;
  console.log("처음 리스트:",pairList);

  final_img_list = [];
  savePairList(pairList, start, destination);

  setTimeout(function(){
   console.log("send to android==> ",result) //result =  ['경도_위도.jpg','경도_위도.jpg',...]
    
   //1. 경도 위도 리스트
    var final_latlng_list = [];
    if(result.length>=1){
      for(var x=0; x<result.length; x++){ 
        var a = result[x].split("_"); //a[0] 경도 127.xxx
        var long = parseFloat(a[0]);
        var b = a[1].split(".jpg") //a[1] 37.xxxx.jpg
        var lat = parseFloat(b[0]);
        var latlng_list = [];
        latlng_list.push(long);
        latlng_list.push(lat);
  
        final_latlng_list.push(latlng_list); 
        latlng_list = [];
      }
      console.log("final_latlng_list=",final_latlng_list); // [[경도,위도],[경도,위도],[경도,위도],....]
  
      var aJsonArray = [];
      for(var x=0; x<result.length; x++){ 
        var aJson = {};
        aJson.location =final_latlng_list[x];
        aJson.image = base64_encode( __dirname + '/screenshot/' + result[x]);
        aJsonArray.push(aJson);
      }
      res.send(aJsonArray);
      console.log("send complete");
    //   //============================이미지 지우기
    // const removePath = (p, callback) => {
    //   fs.stat(p, (err, stats) => { 
    //     if (err) return callback(err);
    
    //     if (!stats.isDirectory()) { 
    //       return fs.unlink(p, err => err ? callback(err) : callback(null, p));
    //     }
    //   });
    // };
    
    // const printResult = (err, result) => {
    //   if (err) return console.log(err);
    
    //   console.log(`${result} 를 정상적으로 삭제했습니다`);
    // };
    
    // const p = path.join(__dirname+'/screenshot');
    
    // try { // D
    //   const files = fs.readdirSync(p);  
    //   if (files.length) 
    //     files.forEach(f => removePath(path.join(p, f), printResult)); 
    // } catch (err) {
    //   if (err) return console.log(err);
    // }
    
    // removePath(p, printResult); 
    // // ===================이미지 지우기 끝

    }else{
      var aJsonArray2 = [];
      var aJson2 = {};
      aJson2.location =[0.0,0.0];
      aJson2.image = "null";
      aJsonArray2.push(aJson2);
      res.send(aJsonArray2)
    }
  },90000);

 });

 var url = require('url');
 var file = require('fs');
 var path = require('path');

function savePairList(list, start, destination) {// 프로미스 객체 반환해야함
  pairList = list;
  console.log("savePaitList 함수 안 리스트: ",pairList);
  
  /* 로드뷰 부분 */
    console.log('grabzit call');
    var client = new grabzit(config.applicationKey, config.applicationSecret);
    
    testloc = pairList;

    var arr = [];
    count_testloc =testloc.length;
    console.log("count_testloc",count_testloc);
    for (var i = 0; i < testloc.length; i++){
              jbString = testloc[i].slice(1,-1);
              var jbSplit = jbString.split(',');
              
              if(jbSplit[0].trim()>100){
              	arr.push(jbSplit[0].trim());
	              arr.push(jbSplit[1].trim());
              }else{
	              arr.push(jbSplit[1].trim());
	              arr.push(jbSplit[0].trim());
	            };
    };

    console.log(arr);
    var options = {"width":-1,"height":-1,"format":"jpg","targetElement":"#roadview","waitForElement": "#roadview,"};
    var cnt = 0;
    while (cnt < arr.length){
      var log = arr[cnt];
      var lat = arr[cnt+1];
      
      var angle;

      if(cnt != arr.length-2){
        angle = getAngle(lat, log, arr[cnt+3],arr[cnt+2]);
      }else{
        angle = 0;
      }

      var options = {"width":-1,"height":-1,"format":"jpg","targetElement":"#roadview","waitForElement": "#roadview,"};
      
      client.url_to_image("http://11.222.33.44:8080/roadview?log="+log+"&lat="+lat+"&bearing="+angle, options);
  
      console.log("url_to_image",arr[cnt],arr[cnt+1],angle);
  
      //이미지 Save
      client.save(config.callbackHandlerUrl+"?log="+log+"&lat="+lat, function (error, id){
        if (error != null){
            throw error;
            return;
        }
        console.log('okay');
      });
      
      cnt = cnt + 2;
      if(cnt>=arr.length){
        return 1;
      }
    };

  };

 /*grabzit 비동기 통신*/
app.get('/handler', function (req, res) {// res 하면 어디로 보내지는지 확인
  console.log('handler');
  var queryData = url.parse(req.url, true).query;
  
  var id = queryData.id;
  
  var client = new grabzit(config.applicationKey, config.applicationSecret);
  var log = String(req.query.log);
  var lat = String(req.query.lat);

  client.get_result(id, function(err, result){
      if (err != null) {
          return;
      }
      //screenshot 폴더에 파일 저장
     file.writeFile(path.join('screenshot', log+'_'+lat+'.jpg'), result, 'binary',function(error){   
      if(error != null)
        throw error;
      else
        console.log("Write jpg file");
    });

  });
  //Sleep(5000);
  count = count + 1;
  if(count == count_testloc){
    const {spawn} =require('child_process');
    const final= spawn('python3',['/home/ubuntu/nodejs/yolov5/detect_simple_3.py']);///home/final/yolov5-master/best.pt']); //'yolov5_master/detect.py']);
  
    final.stdout.on('data',function(data){
       var label_dict_print = data.toString();
        console.log(label_dict_print);
        var result_temp = label_dict_print.split('\n').reverse()[1];
        var arr = result_temp.split('{');
        var arr2 = arr[1].split('}');
        console.log(arr2[0]);

        var result_temp2 = arr2[0];

        //"경도_위도.jpg":"bollard","경도_위도.jpg":"bollard",.... 출력 확인
        var all_list = arr2[0].split(", "); // ["경도_위도.jpg":"bollard","경도_위도.jpg":"bollard",...]
        console.log("all_list = ",all_list);
        var regExp = /'/gi;

        for(var i=0; i< all_list.length; i++){
          console.log(all_list[i])
          var img = all_list[i].split(":");
          var str = img[0];
          if(regExp.test(str)){
            var new_img = str.replace(regExp,"");
          } else {
            var new_img = str;
          }
          final_img_list.push(new_img); // ["경도_위도.jpg","경도_위도.jpg",...]
        }
        console.log("final_img_list=",final_img_list);
        saveModelResult(final_img_list);
        test_flag = 1;
    });

    final.stderr.on('data',function(data){
     console.log(data.toString());
    });

  };
  
});

function saveModelResult(string){
  result = string
  console.log("save함수안",result);
}

   
