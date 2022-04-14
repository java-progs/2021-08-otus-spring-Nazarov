package ru.otus.homework.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

public class MongoPrepareTasklet implements Tasklet, InitializingBean {

    private MongoTemplate template;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        template.getDb().drop();
        return RepeatStatus.FINISHED;
    }

    public void setMongoTemplate(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(template, "mongo template must be set");
    }
}
