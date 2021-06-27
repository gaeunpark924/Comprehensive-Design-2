const {spawn} =require('child_process');
const result = spawn('python3',['/home/ubuntu/nodejs/yolov5/detect_simple_3.py']);///home/final/yolov5-master/best.pt']); //'yolov5_master/detect.py']);

result.stdout.on('data',function(data){
	console.log(data.toString());
});

result.stderr.on('data',function(data){
	console.log(data.toString());
});

