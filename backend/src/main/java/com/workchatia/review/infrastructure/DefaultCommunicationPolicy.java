package com.workchatia.review.infrastructure;
import com.workchatia.review.domain.*;
import org.springframework.stereotype.Component;
@Component
public class DefaultCommunicationPolicy implements CommunicationPolicy { public PolicyEvaluation evaluate(String content){ if(content==null||content.isBlank())return new PolicyEvaluation(ReviewResult.BLOCKED,java.util.List.of("Mensagem vazia.")); return PolicyEvaluation.allowed(); } }