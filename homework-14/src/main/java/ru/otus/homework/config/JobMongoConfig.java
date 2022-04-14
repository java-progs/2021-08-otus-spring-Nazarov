package ru.otus.homework.config;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.otus.homework.batch.MongoPrepareTasklet;
import ru.otus.homework.domain.*;

import java.util.HashMap;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JobMongoConfig {

    public static final String JOB_MIGRATE = "migrateDbToMongoJob";

    public static final String READER_USER_DB = "readerUser";
    public static final String READER_AUTHOR_DB = "readerAuthor";
    public static final String READER_AUTHOR_MONGO = "readerAuthorMongo";
    public static final String READER_GENRE_DB = "readerGenre";
    public static final String READER_GENRE_MONGO = "readerGenreMongo";
    public static final String READER_BOOK_DB = "readerBook";

    public static final String STEP_MONGO_PREPARE = "prepareMongoStep";
    public static final String STEP_USER = "migrateUserStep";
    public static final String STEP_AUTHOR = "migrateAuthorStep";
    public static final String STEP_AUTHOR_UPDATE = "updateAuthorStep";
    public static final String STEP_GENRE = "migrateGenreStep";
    public static final String STEP_GENRE_UPDATE = "updateGenreStep";
    public static final String STEP_BOOK = "migrateBookStep";

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final Mapper mapper;

    private final Logger logger = LoggerFactory.getLogger(JobMongoConfig.class);

    @Bean
    public JpaCursorItemReader<User> readerUser(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<User>()
                .name(READER_USER_DB)
                .entityManagerFactory(entityManagerFactory.getObject())
                .queryString("select u from User u")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Author> readerAuthor(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Author>()
                .name(READER_AUTHOR_DB)
                .entityManagerFactory(entityManagerFactory.getObject())
                .queryString("select a from Author a")
                .build();
    }

    @Bean
    public MongoItemReader<AuthorMongo> readerAuthorMongo(MongoTemplate template) {
        return new MongoItemReaderBuilder<AuthorMongo>()
                .name(READER_AUTHOR_MONGO)
                .template(template)
                .jsonQuery("{}")
                .targetType(AuthorMongo.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public JpaCursorItemReader<Genre> readerGenre(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Genre>()
                .name(READER_GENRE_DB)
                .entityManagerFactory(entityManagerFactory.getObject())
                .queryString("select g from Genre g")
                .build();
    }

    @Bean
    public MongoItemReader<GenreMongo> readerGenreMongo(MongoTemplate template) {
        return new MongoItemReaderBuilder<GenreMongo>()
                .name(READER_GENRE_MONGO)
                .template(template)
                .jsonQuery("{}")
                .targetType(GenreMongo.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public JpaCursorItemReader<Book> readerBook(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Book>()
                .name(READER_BOOK_DB)
                .entityManagerFactory(entityManagerFactory.getObject())
                .queryString("select b from Book b")
                .build();
    }

    @Bean
    public MongoItemWriter<UserMongo> writerUser(MongoTemplate template) {
        return new MongoItemWriterBuilder<UserMongo>()
                .template(template)
                .collection("user")
                .build();
    }

    @Bean
    public MongoItemWriter<AuthorMongo> writerAuthor(MongoTemplate template) {
        return new MongoItemWriterBuilder<AuthorMongo>()
                .template(template)
                .collection("author")
                .build();
    }

    @Bean
    public MongoItemWriter<GenreMongo> writerGenre(MongoTemplate template) {
        return new MongoItemWriterBuilder<GenreMongo>()
                .template(template)
                .collection("genre")
                .build();
    }

    @Bean
    public MongoItemWriter<BookMongo> writerBook(MongoTemplate template) {
        return new MongoItemWriterBuilder<BookMongo>()
                .template(template)
                .collection("book")
                .build();
    }

    @Bean
    public ItemProcessor<User, UserMongo> processorUser() {
        return (user -> mapper.userToMongo(user));
    }

    @Bean
    public ItemProcessor<Author, AuthorMongo> processorAuthor() {
        return (author -> mapper.authorToMongo(author));
    }

    @Bean
    public ItemProcessor<AuthorMongo, AuthorMongo> processorAuthorClearTempFields() {
        return (author -> {
            author.setOldId(null);
            return author;
        });
    }

    @Bean
    public ItemProcessor<Genre, GenreMongo> processorGenre() {
        return (genre -> mapper.genreToMongo(genre));
    }

    @Bean
    public ItemProcessor<GenreMongo, GenreMongo> processorGenreClearTempFields() {
        return (genre -> {
            genre.setOldId(null);
            return genre;
        });
    }

    @Bean
    public ItemProcessor<Book, BookMongo> processorBook() {
        return (book -> mapper.bookToMongo(book));
    }

    @Bean
    public Step migrateUserStep(JpaCursorItemReader<User> readerUser,
                                MongoItemWriter<UserMongo> writerUser,
                                ItemProcessor processorUser) {
        return stepBuilderFactory.get(STEP_USER)
                .<User, UserMongo> chunk(10)
                .reader(readerUser)
                .processor(processorUser)
                .writer(writerUser)
                .listener(new ItemReadListener<User>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(User u) { logger.info("Прочитано: " + u);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<UserMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Step migrateAuthorStep(JpaCursorItemReader<Author> readerAuthor,
                                  MongoItemWriter<AuthorMongo> writerAuthor,
                                  ItemProcessor processorAuthor) {
        return stepBuilderFactory.get(STEP_AUTHOR)
                .<Author, AuthorMongo> chunk(10)
                .reader(readerAuthor)
                .processor(processorAuthor)
                .writer(writerAuthor)
                .listener(new ItemReadListener<Author>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(Author a) { logger.info("Прочитано: " + a);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<AuthorMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Step updateAuthorStep(MongoItemReader<AuthorMongo> readerAuthor,
                                 MongoItemWriter<AuthorMongo> writerAuthor,
                                 ItemProcessor processorAuthorClearTempFields) {
        return stepBuilderFactory.get(STEP_AUTHOR_UPDATE)
                .<AuthorMongo, AuthorMongo> chunk(10)
                .reader(readerAuthor)
                .processor(processorAuthorClearTempFields)
                .writer(writerAuthor)
                .listener(new ItemReadListener<AuthorMongo>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(AuthorMongo a) { logger.info("Прочитано: " + a);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<AuthorMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Step migrateGenreStep(JpaCursorItemReader<Genre> readerGenre,
                                 MongoItemWriter<GenreMongo> writerGenre,
                                 ItemProcessor processorGenre) {
        return stepBuilderFactory.get(STEP_GENRE)
                .<Genre, GenreMongo> chunk(10)
                .reader(readerGenre)
                .processor(processorGenre)
                .writer(writerGenre)
                .listener(new ItemReadListener<Genre>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(Genre g) { logger.info("Прочитано: " + g);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<AuthorMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Step updateGenreStep(MongoItemReader<GenreMongo> readerGenre,
                                MongoItemWriter<GenreMongo> writerGenre,
                                ItemProcessor processorGenreClearTempFields) {
        return stepBuilderFactory.get(STEP_GENRE_UPDATE)
                .<GenreMongo, GenreMongo> chunk(10)
                .reader(readerGenre)
                .processor(processorGenreClearTempFields)
                .writer(writerGenre)
                .listener(new ItemReadListener<GenreMongo>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(GenreMongo g) { logger.info("Прочитано: " + g);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<GenreMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Step migrateBookStep(JpaCursorItemReader<Book> readerBook,
                                MongoItemWriter<BookMongo> writerBook,
                                ItemProcessor processorBook) {
        return stepBuilderFactory.get(STEP_BOOK)
                .<Book, BookMongo> chunk(10)
                .reader(readerBook)
                .processor(processorBook)
                .writer(writerBook)
                .listener(new ItemReadListener<Book>() {
                    public void beforeRead() {logger.info("Начало чтения");}
                    public void afterRead(Book b) { logger.info("Прочитано: " + b);}
                    public void onReadError(Exception e) { logger.error("Ошибка чтения: " + e);}
                })
                .listener(new ItemWriteListener<BookMongo>() {
                    public void beforeWrite(List list) { list.forEach(i -> logger.info("Запись: " + i));}
                    public void afterWrite(List list) { list.forEach(i -> logger.info("Записано: " + i));}
                    public void onWriteError(Exception e, List list) { logger.error("Ошибка записи: " + e);}
                })
                .build();
    }

    @Bean
    public Tasklet mongoPrepareTasklet(MongoTemplate template) {
        val tasklet = new MongoPrepareTasklet();
        tasklet.setMongoTemplate(template);
        return tasklet;
    }

    @Bean
    public Step prepareMongoStep(Tasklet mongoPrepareTasklet) {
        return stepBuilderFactory.get(STEP_MONGO_PREPARE)
                .tasklet(mongoPrepareTasklet)
                .build();
    }

    @Bean
    public Job migrateDbToMongoJob(Step prepareMongoStep,
                                   Step migrateUserStep,
                                   Step migrateAuthorStep,
                                   Step migrateGenreStep,
                                   Step migrateBookStep,
                                   Step updateAuthorStep,
                                   Step updateGenreStep) {
        return jobBuilderFactory.get(JOB_MIGRATE)
                .flow(prepareMongoStep)
                .next(migrateUserStep)
                .next(migrateAuthorStep)
                .next(migrateGenreStep)
                .next(migrateBookStep)
                .next(updateAuthorStep)
                .next(updateGenreStep)
                .end()
                .build();
    }

}
