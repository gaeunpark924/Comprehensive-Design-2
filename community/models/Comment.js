var mongoose = require('mongoose');

// schema
var commentSchema = mongoose.Schema({
  post:{type:mongoose.Schema.Types.ObjectId, ref:'post_Seoul', required:true},
  author:{type:mongoose.Schema.Types.ObjectId, ref:'user', required:true},
  text:{type:String, required:[true,'text is required!']},
  isDeleted:{type:Boolean}, 
  createdAt:{type:Date, default:Date.now},
  updatedAt:{type:Date},
},{
  toObject:{virtuals:true}
});

commentSchema.virtual('childComments') //대댓글
  .get(function(){ return this._childComments; })
  .set(function(value){ this._childComments=value; });

// model & export
var Comment = mongoose.model('comment',commentSchema);
module.exports = Comment;