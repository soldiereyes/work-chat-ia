package com.workchatia.review.domain;
import java.util.UUID;
public record ReviewSuggestion(UUID id,SuggestionType type,String originalText,String suggestedText,String explanation){}