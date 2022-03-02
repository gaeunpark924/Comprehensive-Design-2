var express  = require('express');
var router = express.Router();
var Post = require('../models/Post_GS');
var User = require('../models/User');
var util = require('../util');

// Index
router.get('/', async function(req, res){ 
  var page = Math.max(1, parseInt(req.query.page));   // parseInt 함수 : query sring은 문자열로 전달되기 때문에 숫자가 아닐 수도 있고, 정수를 읽어내기 위해 사용
  var limit = Math.max(1, parseInt(req.query.limit)); // math.max 함수 : page, limit은 양수여야 함(최소 1)
  page = !isNaN(page)?page:1;                         // 보여줄 페이지 1page
  limit = !isNaN(limit)?limit:5;                     // 한 페이지 5개의 게시물 표시

  var skip = (page-1)*limit; //무시할 게시글의 수. ex) 3번째 페이지라면 10개 게시물 무시, 21번째 게시글부터 표시
  var count = await Post.countDocuments({}); 
  var maxPage = Math.ceil(count/limit); //전체 페이지 수 계산
  var posts = await Post.find({}) 
    .populate('author')
    .sort('-createdAt')
    .skip(skip)   
    .limit(limit)
    .exec();

  res.render('posts_GS/index', {
    posts:posts,
    currentPage:page, // 현재페이지번호
    maxPage:maxPage,  // 마지막페이지번호
    limit:limit       // 페이지당 보여줄 게시물 수
  });
});

// New
router.get('/new', util.isLoggedin, function(req, res){
    var post = req.flash('post')[0] || {};
    var errors = req.flash('errors')[0] || {};
    res.render('posts_GS/new', { post:post, errors:errors });
});

// create
router.post('/', util.isLoggedin, function(req, res){
    req.body.author = req.user._id; 
    Post.create(req.body, function(err, post){
      if(err){
        req.flash('post', req.body);
        req.flash('errors', util.parseError(err));
        return res.redirect('/posts_GS/new'+res.locals.getPostQueryString());
      }
      res.redirect('/posts_GS'+res.locals.getPostQueryString(false, {page:1}));
    });
});

// show
router.get('/:id', function(req, res){
    Post.findOne({_id:req.params.id}) 
      .populate('author')             
      .exec(function(err, post){      
        if(err) return res.json(err);
        res.render('posts_GS/show', {post:post});
    });
});

// edit
router.get('/:id/edit',util.isLoggedin, checkPermission, function(req, res){
    var post = req.flash('post')[0];
    var errors = req.flash('errors')[0] || {};
    if(!post){
      Post.findOne({_id:req.params.id}, function(err, post){
          if(err) return res.json(err);
          res.render('posts_GS/edit', { post:post, errors:errors });
        });
    }
    else {
      post._id = req.params.id;
      res.render('posts_GS/edit', { post:post, errors:errors });
    }
});

// update
router.put('/:id',util.isLoggedin, checkPermission, function(req, res){
    req.body.updatedAt = Date.now();
    Post.findOneAndUpdate({_id:req.params.id}, req.body, {runValidators:true}, function(err, post){
      if(err){
        req.flash('post', req.body);
        req.flash('errors', util.parseError(err));
        return res.redirect('/posts_GS/'+req.params.id+'/edit'+res.locals.getPostQueryString()); 
      }
      res.redirect('/posts_GS/'+req.params.id+res.locals.getPostQueryString()); 
    });
});

// destroy
router.delete('/:id', util.isLoggedin, checkPermission, function(req, res){
  Post.deleteOne({_id:req.params.id}, function(err){
    if(err) return res.json(err);
    res.redirect('/posts_GS'+res.locals.getPostQueryString());
  });
});

module.exports = router;

// private functions 
function checkPermission(req, res, next){
    Post.findOne({_id:req.params.id}, function(err, post){
      if(err) return res.json(err);
      if(post.author != req.user.id) return util.noPermission(req, res);
  
      next();
    });
}