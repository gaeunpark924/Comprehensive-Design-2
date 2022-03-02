var mongoose = require('mongoose');

// schema
var postSchema = mongoose.Schema({
    title:{type:String, required:[true,'Title is required!']},
    body:{type:String, required:[true,'Body is required!']},
    author:{type:mongoose.Schema.Types.ObjectId, ref:'user', required:true}, //user.id 와 post.author 연결
    createdAt:{type:Date, default:Date.now},
    updatedAt:{type:Date},
});
  
// model & export
var Post = mongoose.model('post_GS', postSchema);
module.exports = Post;