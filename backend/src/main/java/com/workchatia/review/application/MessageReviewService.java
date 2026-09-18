package com.workchatia.review.application;
import com.workchatia.review.domain.*;
import java.util.UUID;
public class MessageReviewService {
 private final CommunicationPolicy policy; private final LLMProvider provider;
 public MessageReviewService(CommunicationPolicy policy,LLMProvider provider){this.policy=policy;this.provider=provider;}
 public MessageReview review(UUID messageId,String content){PolicyEvaluation e=policy.evaluate(content); if(e.result()==ReviewResult.BLOCKED)return new MessageReview(UUID.randomUUID(),messageId,ReviewStatus.COMPLETED,ReviewResult.BLOCKED,String.join("; ",e.violations()),java.util.List.of()); return provider.review(messageId,content);}
}