package com.workchatia.review.application;
import com.workchatia.review.domain.MessageReview;
import java.util.UUID;
public interface LLMProvider { MessageReview review(UUID messageId,String content); }