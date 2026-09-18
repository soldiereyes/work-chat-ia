package com.workchatia.review.infrastructure;
import com.workchatia.review.application.LLMProvider;
import com.workchatia.review.domain.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
@Component
@Profile({"dev","test"})
public class StubLLMProvider implements LLMProvider { public MessageReview review(UUID messageId,String content){return new MessageReview(UUID.randomUUID(),messageId,ReviewStatus.COMPLETED,ReviewResult.ALLOWED,"Revisão inicial concluída.",List.of());} }