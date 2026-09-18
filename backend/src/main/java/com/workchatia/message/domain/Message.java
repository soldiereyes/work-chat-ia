package com.workchatia.message.domain;
import java.util.UUID;
public class Message {
 private final UUID id;
 private final UUID conversationId;
 private final UUID authorId;
 private String content;
 private MessageStatus status;
 private final MessageType type;
 public Message(UUID id,UUID conversationId,UUID authorId,String content,MessageType type){
  this.id=id;this.conversationId=conversationId;this.authorId=authorId;this.content=content;this.type=type;this.status=MessageStatus.DRAFT;
 }
 public void submitForReview(){require(MessageStatus.DRAFT);status=MessageStatus.PENDING_REVIEW;}
 public void approve(){require(MessageStatus.PENDING_REVIEW);status=MessageStatus.APPROVED;}
 public void block(){status=MessageStatus.BLOCKED;}
 public void replaceContent(String value){if(status!=MessageStatus.DRAFT&&status!=MessageStatus.PENDING_REVIEW)throw new IllegalStateException("message cannot be edited");content=value;}
 public UUID id(){return id;} public UUID conversationId(){return conversationId;} public UUID authorId(){return authorId;}
 public String content(){return content;} public MessageStatus status(){return status;} public MessageType type(){return type;}
 private void require(MessageStatus expected){if(status!=expected)throw new IllegalStateException("invalid message state: "+status);}
}