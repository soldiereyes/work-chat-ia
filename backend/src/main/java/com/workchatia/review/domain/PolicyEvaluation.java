package com.workchatia.review.domain;
import java.util.List;
public record PolicyEvaluation(ReviewResult result,List<String> violations){public static PolicyEvaluation allowed(){return new PolicyEvaluation(ReviewResult.ALLOWED,List.of());}}