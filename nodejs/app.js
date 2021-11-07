const express = require('express');
var result =[];
var final_route = "";
var final_database_data = [];

// 파일시스템 모듈을 이용하여 이미지를 읽은후 base64로 인코딩하기  
function base64_encode(file) {  
  // 바이너리 데이터 읽기 file 에는 파일의 경로를 지정  
  var bitmap = fs.readFileSync(file);  
  //바이너리 데이터를 base64 포멧으로 인코딩하여 스트링 획득
  return new Buffer(bitmap).toString('base64');  
}  

//데이터베이스 설정
var mysql=require('mysql');

var connection= mysql.createConnection({
   host :'localhost',
   user:'root',
   password:'0000',
   database :'FLATROAD'
});

//sql문 실행 함수
function execute_SQL(query){
  console.log(query);
  connection.query(query,function (error, results, fields){
        if (error){
               console.log(error);
        }
        return results;
   });
}

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
   console.log('start server : 8080');
});

var http = require('http');
var bodyParser = require('body-parser')
app.use(bodyParser.urlencoded({extended: false}))

//로드뷰 호출
app.get('/roadview', function(req, res) {
    res.render('index',{'log':req.query.log, 'lat':req.query.lat});//, 'bearing':req.query.bearing});
    console.log('roadview call');
});

var pairList = []; 
var final_img_list =[];

//multer 모듈로 이미지 저장
const multer = require('multer')
var storage = multer.diskStorage({
  destination: function(req, file, cb){
    cb(null, './public/image')
  },
  filename: function (req, file, cb){ //이미지파일저장시 IMGID/IMGNAME 형태로 저장
    query_2_select="select * from STORE_IMGID;"

    connection.query(query_2_select,function (error, results, fields){//현재 이미지 id 가지고 오기
       if (error){
         console.log(error);
       }
       var NEW_IMGID=results[0]["TOTALIMGID"]+1; //추출한 이미지ID에서 +1을 하여 새로운 ID 생성 

       query_2_update="update STORE_IMGID set TOTALIMGID = "+NEW_IMGID+";"; //이미지ID 업데이트
       execute_SQL(query_2_update);
       //console.log("완료");
    
      var ImgName=NEW_IMGID+"_"+file.originalname; //이미지이름 앞에 이미지ID를 추가
      console.log("새 이미지이름:"+ImgName);
      cb(null, ImgName)
    });
  }
})
const upload = multer({storage: storage});

/* 기능2 : 사용자가 위험요소 추가 -앱에서 서버로 이미지 post */
app.post('/android/post/upload', upload.single('img'),(req,res)=>{
  res.json(req.file)
  console.log(req.file)
  //console.log(req.body)
  const obj = JSON.parse(JSON.stringify(req.body)); // req.body = [Object: null prototype] { title: 'product' }
  console.log('obj내용:',obj);

  //데이터베이스에 저장 ** type 확인하고 넣기
  var filename =req.file.filename;
  console.log('location타입 :'+typeof(obj.location))
  var latitude =obj.location.split(' ')[0];
  var longitude = obj.location.split(' ')[1];
  var feature = obj.feature;
  var o_type =obj.type;

  if (o_type.includes("볼라드")){
    obs_type=8;
  }

  var NEW_INFOID=filename.split('_')[0];
  console.log(NEW_INFOID);

  query_2_insert='insert into OBSTACLE_INFO VALUES ('+NEW_INFOID+','+obs_type+','+longitude+','+latitude+',\"'+feature+'\",\"'+filename+'\");';
  console.log(query_2_insert);

  connection.query(query_2_insert,function (error, results, fields){
     if (error){
          console.log(error);
     }
     console.log(results);
  });
});

const request = require('request');
var routeArr = [];

/* tmap */
/* 앱에서 서버에 json post하길 원함 */ 
app.post('/android/post/point', function(req, res, next){ /* 접근 url -> ex) http://123.456.78.90:3000/post */
    var st = req.body.start.split(",");
    var startLat = parseFloat(st[0]);
    var startLon = parseFloat(st[1]);
    
    var dt = req.body.destination.split(",");
    var destLat = parseFloat(dt[0]);
    var destLon = parseFloat(dt[1]);
    var ROUTE_TYPE = Number(req.body.option);
    var tDistance = 0;
    //Tmap API 요청	
    request({
        uri: "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result",
       method: "POST",
        form: {
            "appKey" : "",
            "startX" : startLon,
            "startY" : startLat,
            "speed" : 0,  
            "endX" : destLon,
            "endY" : destLat,
            "searchOption" : ROUTE_TYPE,
            "reqCoordType" : "WGS84GEO",   //WGS84GEO: 경위도(기본값)
            "resCoordType" : "WGS84GEO",   //EPSG3857: Google Mercator
            "startName" : "출발지",
            "endName" : "도착지",
        },
        json: true
    }, function(error, response, body){
	    
        console.error('Tmap error:', error); 				  // 에러
        console.log('Tmap statusCode:', response && response.statusCode); // 응답코드
  
        var resultData = body.features;

        tDistance = ((resultData[0].properties.totalDistance) / 1000).toFixed(1);
        console.log("총 거리:"+tDistance+"km");
        
        var descriptionArr = [];
	    
        let routeJson = new Object();
        routeJson.startx = String(startLat);
        routeJson.starty = String(startLon);
        routeJson.endx = String(destLat);
        routeJson.endy = String(destLon);
	    
	//경로 옵션    
        switch(ROUTE_TYPE){
            case 0:
                routeJson.searchOption = "추천";
                break;
            case 4:
                routeJson.searchOption = "대로우선";
                break;
            case 10:
                routeJson.searchOption = "최단거리";
                break;
            case 30:
                routeJson.searchOption = "최단거리+계단제외";
                break;
            default:
                routeJson.searchOption = "";
                break;
        }
        
            for ( var i in resultData) {
                var geometry = resultData[i].geometry;
                var properties = resultData[i].properties;      
                if (geometry.type == "LineString") {
                    for ( var j in geometry.coordinates) {                                   
                        //구간의 첫번째 좌표("Point") 배열에 담기  
                        if (j == 0){          
                            routeArr.push(String(geometry.coordinates[j][1])+","+String(geometry.coordinates[j][0]));
                        }
                    }
                }
            }
            routeArr.push(String(destLat)+","+String(destLon))
            routeJson.route = routeArr;
            routeJson.description = descriptionArr;
           
            //3km 이상일 경우 바로 응답 보내게
            if (tDistance >= 3){
              console.log("총 거리 3km 이상")
              res.send([[],[],[]])
            }else{
              savePairList(routeArr, req.body.start, req.body.destination);
              saveFinalRoute(body.features)  //app.post안 변수 final_list로 저장되는지 확인
              console.log("saveFinalRoute이후 final_route값",final_route)
              setTimeout(()=>timeoutFunction(),90000);
            }
    });

  // console.log("saveFinalRoute이후 final_route값",final_route)  
  //final_img_list = []; //앱으로 보낼 이미지 리스트
  database_img=[]; //데이터베이스에 저장된 위험요소 정보중 사용할 이미지의 리스트
  database_data=[]; //최종적으로 앱으로 보낼 사용자가 추가한 위험요소 정보들의 리스트 
  	
  function timeoutFunction(){
    console.log("send to android==> ",result) //result =  ['경도_위도.jpg','경도_위도.jpg',...]
    console.log("추가위험요소 정보 : "+database_data);
    
    //1. 경도 위도 리스트
    var final_latlng_list = [];

    if(!(result.length==0)){
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
  
      var result_JsonArray = [];
      //var aJson = {};
      for(var x=0; x<result.length; x++){ 
        var aJson = {};
        aJson.location =final_latlng_list[x];
        aJson.image = base64_encode( __dirname + '/screenshot/' + result[x]);
        result_JsonArray.push(aJson);
      }
      //추가위험요소 정보
      //aJsonArray.push(database_data);
      //console.log(aJsonArray);
      
      //////////////////////////////////////////////
      //////////////최종 finalJsonArray////////////
      //////////////////////////////////////////////
      //1.경로 JSON.stringify(body.features)
      var finalJsonArray = [];
      finalJsonArray.push(final_route) 
      //console.log("1.경로 JSON.stringify(body.features)")

      //2.로드뷰로 찾은 위험요소 위치, 이미지
      finalJsonArray.push(result_JsonArray)
      //console.log("2.로드뷰로 찾은 위험요소 위치, 이미지")

      //3.추가위험정보 이름,위치,설명
      var database_JsonArray = [];
      for(var x=0; x<database_data.length; x++){ 
        var bJson = {};
        bJson.info =database_data[x];
        database_JsonArray.push(bJson);
      }
      finalJsonArray.push(database_JsonArray)
      //console.log("3.추가위험정보 이름,위치,설명")
      //jsonFinalOjbect.database = database_JsonArray

      //finalJsonArray를 string으로 변환해 전송
      //res.send(JSON.stringify(finalJsonArray))
      // 나중엔 finalJsonArray 전송
      //res.send(finalJsonArray);
      //console.log(jsonFinalOjbect.to)
      res.send(finalJsonArray)
      console.log("send complete");
      
      //============================이미지 지우기
      const removePath = (p, callback) => {
      fs.stat(p, (err, stats) => {
        if (err) return callback(err);
    
        if (!stats.isDirectory()) { 
          return fs.unlink(p, err => err ? callback(err) : callback(null, p));
        }
      });
    };
    
    const printResult = (err, result) => {
       if (err) return console.log(err);
    
       console.log(`${result} 를 정상적으로 삭제했습니다`);
     };
    
     const p = path.join(__dirname+'/screenshot');
    
     try { // D
       const files = fs.readdirSync(p);  
       if (files.length) 
         files.forEach(f => removePath(path.join(p, f), printResult)); 
     } catch (err) {
       if (err) return console.log(err);
     }
    
     removePath(p, printResult); 
     // ===================이미지 지우기 끝
    }
    else{
      var aJsonArray2 = [];
      var aJson2 = {};
      aJson2.location =[0.0,0.0];
      aJson2.image = "null";
      aJsonArray2.push(aJson2);
      res.send(aJsonArray2)
    }
  }
});


var url = require('url');
var file = require('fs');
var path = require('path');

//전역변수로 최종 티맵 루트를 저장
function saveFinalRoute(route){ 
    final_route = route
    console.log("saveFinalRoute 함수 안 루트: ",final_route);
}


//전역변수로 최종 database_list를 저장
function saveFinalDatabaseData(list){
    final_database_data = list
    console.log("saveFinalDatabaseData 함수 안 리스트: ",final_database_data);
}

function savePairList(list, start, destination) {// 프로미스 객체 반환해야함
  pairList = list;
  console.log("savePairList 함수 안 리스트: ",pairList);
  
  /* 로드뷰 부분 */
    console.log('grabzit call');
    var client = new grabzit(config.applicationKey, config.applicationSecret);
    
    //testloc = pairList;
    //좌표 리스트
    var arr = [];
    testloc =pairList.length;
    console.log("testloc",testloc);
    for (var i = 0; i < pairList.length; i++){
              jbString = pairList[i];
              var jbSplit = jbString.split(',');
              
              if(jbSplit[0].trim()>100){
                 arr.push(jbSplit[0].trim());
                 arr.push(jbSplit[1].trim());
              }else{
                 arr.push(jbSplit[1].trim());
                 arr.push(jbSplit[0].trim());
               };
    };

    var options = {"width":-1,"height":-1,"format":"jpg","targetElement":"#roadview","waitForElement": "#roadview,"};
    var cnt = 0;
    while (cnt < arr.length){
      var log = arr[cnt];
      var lat = arr[cnt+1];
      
      /*var angle;
      if(cnt != arr.length-2){
        angle = getAngle(lat, log, arr[cnt+3],arr[cnt+2]);
      }else{
        angle = 0;
      }*/

      var options = {"width":-1,"height":-1,"format":"jpg","targetElement":"#roadview","waitForElement": "#roadview,"};
      
      //client.url_to_image("http://"+config.ip+":8080/roadview?log="+log+"&lat="+lat+"&bearing="+angle, options); //방위각 포함된 url
      client.url_to_image("http://"+config.ip+":8080/roadview?log="+log+"&lat="+lat,options);
      
      console.log("url_to_image",arr[cnt],arr[cnt+1],angle);
  
      //이미지 저장
      client.save(config.callbackHandlerUrl+"?log="+log+"&lat="+lat, function (error, id){
        if (error != null){
            throw error;
            return;
        }
        console.log('save call');
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
        else{
          file.readdir('./screenshot', (err, files) => {
            console.log("Write jpg file screenshot 파일수",files.length);
            //screenshot 디렉토리의 file수 와 좌표 경로의 길이가 같으면
            if (files.length == routeArr.length){
              routeArr = []
              console.log("인공지능 모델 시작")
              yolo()
            }
          })
        }
      });
  });
  ////데이터베이스에 있는 정보가지고 오기!
  llog=parseFloat(log);
  console.log(typeof(llog));
  llat=parseFloat(lat);

  query_1_select='SELECT * FROM OBSTACLE_INFO WHERE LONGITUDE>'+(llog-0.0005)+' AND LONGITUDE <  '+(llog+0.0005)+ 'AND LATITUDE>'+(llat-0.0005)+' AND LATITUDE<'+(llat+0.0005)+';'; /// AND LATITUDE<'+(parseFloat(lat)+0.0005)+';';

  connection.query(query_1_select,function (error, results, fields){
    if (error){
            console.log(error);
    }
    len=results.length;  //추출한 데이터 갯수
    //console.log("추출한 위험요소 데이터갯수:"+len);
    for (i=0;i<len;i++){
            if(database_img.includes(results[i].INFO_ID)==false){ //그 전에 찾아놓은 위험요소 정보와 중복을 피하기 위해
              console.log(results[i].INFO_ID);    
              database_img.push(results[i].INFO_ID);
              database_data.push(results[i].INFO_ID+'/'+results[i].OBSTACLEID+'/'+results[i].LONGITUDE+'/'+results[i].LATITUDE+'/'+results[i].FEATURE+'/'+results[i].IMGNAME);
        }
    }
  });
  console.log(database_data)
  saveFinalDatabaseData(database_data)
});

function saveModelResult(string){
  result = string
  console.log("save함수안",result);
  console.log(result.length);
}
/* yolo */
function yolo(){
  final_img_list = []; //앱으로 보낼 이미지 리스트
   
    console.log("데이터이미지:"+database_img);
    const {spawn} =require('child_process');
    const final= spawn('python3',['/home/ubuntu/nodejs/yolov5/detect.py']);///home/final/yolov5-master/best.pt']); //'yolov5_master/detect.py']);
  
    final.stdout.on('data',function(data){
       var label_dict_print = data.toString();
        console.log('final결과:'+label_dict_print);
        var result_temp = label_dict_print.split('\n').reverse()[1];
        console.log('result_temp:'+result_temp);
        if(result_temp.length>=0){
          var arr = result_temp.split('{');

          var arr2 = arr[1].split('}');

          var result_temp2 = arr2[0];

        //"경도_위도.jpg":"bollard","경도_위도.jpg":"bollard",.... 출력 확인
          var all_list = arr2[0].split(", "); // ["경도_위도.jpg":"bollard","경도_위도.jpg":"bollard",...]
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
        }
        console.log("final_img_list=",final_img_list);
        saveModelResult(final_img_list);
        test_flag = 1;
    });

    final.stderr.on('data',function(data){
     console.log(data.toString());
    });
}
