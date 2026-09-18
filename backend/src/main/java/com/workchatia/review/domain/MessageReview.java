package com.workchatia.review.domain;
import java.util.List;
import java.util.UUID;
public record MessageReview(UUID id,UUID messageId,ReviewStatus status,ReviewResult result,String summary,List<ReviewSuggestion> suggestions){}