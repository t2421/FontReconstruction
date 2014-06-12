var fontJSON;
var canvas;
var processingInstance;
$(document).ready(function() {
	var getString = "Typograpohy";
	var fontName = "Times New Roman";
	$.ajax({
		url: 'http://localhost:8010/FontReconstruction/FontReconstruction',
		type: 'GET',
		dataType: 'jsonp',
		data: {font: encodeURI(getString),fontSize:200,fontName:encodeURI(fontName)}
	})
	.done(function(data) {
		fontJSON = data;
		init();
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {
		//console.log("complete");
	});
});

function init(){
	canvas = document.getElementById("world");
	processingInstance = new Processing(canvas,sketch);
}

function sketch(processing){
	var p = processing;
	p.setup = function(){
		p.width = $(window).width();
		p.height = $(window).height();
		p.background(0);
		p.translate(30,400);
		drawFont(p);
	};

	p.draw = function(){
		p.background(0);
		p.translate(30,400);
		drawFont(p);
	};

}


function drawFont(processing){
	var p = processing;
	var len = fontJSON.length;
	var startPoint = {x:0,y:0};
	var endPoint = {x:0,y:0};
	var loopPoint = {};
	var numLoop = parseInt(p.map(p.mouseY,0,p.height,1,10));
	var randomSeed = p.map(p.mouseX,0,p.width,0,50);
	var entryPoint = {};
	for(var j = 0; j< numLoop;j++){
		for (var i = 0; i < len; i++) {
			p.stroke(255);
			var pointInfo = fontJSON[i];
			var points = fontJSON[i].points;
			p.noFill();
			p.strokeWeight(1);
			p.strokeJoin(p.ROUND);

			if(pointInfo.lineType == "QUADTO"){
				endPoint = {x:points[2]+p.random(-randomSeed,randomSeed),y:points[3]+p.random(-randomSeed,randomSeed)}
				p.bezier(startPoint.x,startPoint.y,points[0]+p.random(-randomSeed,randomSeed),points[1]+p.random(-randomSeed,randomSeed),points[0]+p.random(-randomSeed,randomSeed),points[1]+p.random(-randomSeed,randomSeed),endPoint.x,endPoint.y);
				startPoint = {x:endPoint.x,y:endPoint.y};
			}else if(pointInfo.lineType == "LINETO"){
				endPoint = {x:points[0]+p.random(-randomSeed,randomSeed),y:points[1]+p.random(-randomSeed,randomSeed)}
				p.line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
				startPoint = {x:endPoint.x,y:endPoint.y};
			}else if(pointInfo.lineType == "MOVETO"){
				startPoint = {x:points[0]+p.random(-randomSeed,randomSeed),y:points[1]+p.random(-randomSeed,randomSeed)};
				entryPoint = {x:startPoint.x,y:startPoint.y};
			}else if(pointInfo.lineType == "CLOSETO"){
				p.line(startPoint.x,startPoint.y,entryPoint.x,entryPoint.y);
			}
		};
	}
	
}