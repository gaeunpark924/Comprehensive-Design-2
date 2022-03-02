//여러 곳에서 공통으로 쓰이는 함수들

var util = {};

util.parseError = function(errors){
  var parsed = {};
  if(errors.name == 'ValidationError'){
    for(var name in errors.errors){
      var validationError = errors.errors[name];
      parsed[name] = { message:validationError.message };
    }
  } 
  else if(errors.code == '11000' && errors.errmsg.indexOf('username') > 0) {
    parsed.username = { message:'This username already exists!' };
  } 
  else {
    parsed.unhandled = JSON.stringify(errors);
  }
  return parsed;
}

//사용자가 로그인이 되었는지 아닌지 판단. 비로그인 시 사용자를 에러메세지와 함께 로그인페이지로 보냄
util.isLoggedin = function(req, res, next){
  if(req.isAuthenticated()){
    next();
  } 
  else {
    req.flash('errors', {login:'Please login first'});
    res.redirect('/login');
  }
}

//route에 접근권한이 없다고 판단된 경우 호출. 에러메세지와 함께 로그인 페이지로 보냄.
util.noPermission = function(req, res){
  req.flash('errors', {login:"You don't have permission"});
  req.logout();
  res.redirect('/login');
}

//req.query로 전달 받은 query에서 page, limint을 추출하여 다시 한 줄의 문자열로 만들어 반환
util.getPostQueryString = function(req, res, next){
  res.locals.getPostQueryString = function(isAppended=false, overwrites={}){    
    var queryString = '';
    var queryArray = [];
    var page = overwrites.page?overwrites.page:(req.query.page?req.query.page:'');
    var limit = overwrites.limit?overwrites.limit:(req.query.limit?req.query.limit:'');

    if(page) queryArray.push('page='+page);
    if(limit) queryArray.push('limit='+limit);

    if(queryArray.length>0) queryString = (isAppended?'&':'?') + queryArray.join('&');

    return queryString;
  }
  next();
}
module.exports = util;