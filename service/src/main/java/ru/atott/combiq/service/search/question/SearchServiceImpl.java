package ru.atott.combiq.service.search.question;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.bucket.global.InternalGlobal;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.es.NameVersionDomainResolver;
import ru.atott.combiq.dao.repository.QuestionRepository;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.bean.Tag;
import ru.atott.combiq.service.dsl.DslParser;
import ru.atott.combiq.service.mapper.QuestionMapper;
import ru.atott.combiq.service.question.QuestionService;
import ru.atott.combiq.service.site.UserContext;
import ru.atott.combiq.service.question.FavoriteQuestionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private DefaultResultMapper defaultResultMapper;

    @Autowired
    private NameVersionDomainResolver domainResolver;

    @Autowired
    private Client client;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FavoriteQuestionService favoriteQuestionService;

    @Autowired
    private QuestionMapper questionMapper;

    public SearchServiceImpl() {
        SimpleElasticsearchMappingContext mappingContext = new SimpleElasticsearchMappingContext();
        defaultResultMapper = new DefaultResultMapper(mappingContext);
    }

    @Override
    public long countQuestions(SearchContext context) {
        CountRequestBuilder query = new SearchQueryBuilder()
                .setClient(client)
                .setDomainResolver(domainResolver)
                .setSearchContext(context)
                .buildCountRequest();

        return query.execute().actionGet().getCount();
    }

    @Override
    public SearchResponse searchQuestions(SearchContext context) {
        SearchRequestBuilder query = new SearchQueryBuilder()
                .setClient(client)
                .setDomainResolver(domainResolver)
                .setSearchContext(context)
                .buildSearchRequest();

        org.elasticsearch.action.search.SearchResponse searchResponse = query.execute().actionGet();

        Pageable pageable = new PageRequest((int)Math.floor((double)context.getFrom() / (double)context.getSize()), context.getSize());
        Page<QuestionEntity> page = defaultResultMapper.mapResults(searchResponse, QuestionEntity.class, pageable);

        SearchResponse response = new SearchResponse();
        response.setQuestions(page.map(questionMapper::map));
        response.setPopularTags(getPopularTags(searchResponse));
        response.setDslQuery(context.getDslQuery());
        return response;
    }

    @Override
    public Optional<SearchResponse> searchAnotherQuestions(UserContext uc, Question question) {
        if (CollectionUtils.isEmpty(question.getTags())) {
            return Optional.empty();
        }

        SearchContext searchContext = new SearchContext();
        searchContext.setUserContext(uc);
        searchContext.setDslQuery(DslParser.parse("[" + question.getTags().get(0) + "]"));
        searchContext.setSize(5);

        return Optional.of(searchQuestions(searchContext));
    }

    @Override
    public GetQuestionResponse getQuestion(UserContext uc, GetQuestionContext context) {
        GetQuestionResponse response = new GetQuestionResponse();

        if (context.getDsl() != null && context.getProposedIndexInDslResponse() != null) {
            SearchContext searchContext = new SearchContext();
            searchContext.setUserContext(uc);
            if (context.getProposedIndexInDslResponse() == 0) {
                searchContext.setFrom(0);
                searchContext.setSize(2);
            } else {
                searchContext.setFrom(context.getProposedIndexInDslResponse() - 1);
                searchContext.setSize(3);
            }
            searchContext.setDslQuery(context.getDsl());
            searchContext.setUserId(context.getUserId());

            SearchResponse searchResponse = searchQuestions(searchContext);
            List<Question> questions = searchResponse.getQuestions().getContent();
            if (context.getProposedIndexInDslResponse() == 0) {
                if (questions.size() > 0 && questions.get(0).getId().equals(context.getId())) {
                    response.setQuestion(questions.get(0));
                    QuestionPositionInDsl positionInDsl = new QuestionPositionInDsl();
                    positionInDsl.setIndex(context.getProposedIndexInDslResponse());
                    positionInDsl.setTotal(searchResponse.getQuestions().getTotalElements());
                    positionInDsl.setDslQuery(context.getDsl());
                    if (questions.size() > 1) {
                        positionInDsl.setNextQuestion(questions.get(1));
                    }
                    response.setPositionInDsl(positionInDsl);
                }
            } else {
                if (questions.size() > 1 && questions.get(1).getId().equals(context.getId())) {
                    response.setQuestion(questions.get(1));
                    QuestionPositionInDsl positionInDsl = new QuestionPositionInDsl();
                    positionInDsl.setIndex(context.getProposedIndexInDslResponse());
                    positionInDsl.setTotal(searchResponse.getQuestions().getTotalElements());
                    positionInDsl.setDslQuery(context.getDsl());
                    positionInDsl.setPreviousQuestion(questions.get(0));
                    if (questions.size() > 2) {
                        positionInDsl.setNextQuestion(questions.get(2));
                    }
                    response.setPositionInDsl(positionInDsl);
                }
            }
        }

        if (response.getQuestion() == null) {
            SearchContext searchContext = new SearchContext();
            searchContext.setUserContext(uc);
            searchContext.setFrom(0);
            searchContext.setSize(1);
            searchContext.setUserId(context.getUserId());
            searchContext.setQuestionId(context.getId());
            SearchResponse searchResponse = searchQuestions(searchContext);
            if (searchResponse.getQuestions().getContent().size() > 0) {
                response.setQuestion(searchResponse.getQuestions().getContent().get(0));
            }
        }

        if (response.getQuestion() != null) {
            Question question = response.getQuestion();
            if (question.getClassNames() == null) {
                questionService.refreshMentionedClassNames(question);
            }
        }

        return response;
    }

    @Override
    public Question getQuestionByLegacyId(String legacyId) {
        QuestionEntity entity = questionRepository.findOneByLegacyId(legacyId);
        return questionMapper.safeMap(entity);
    }

    private List<Tag> getPopularTags(org.elasticsearch.action.search.SearchResponse response) {
        InternalGlobal global = response.getAggregations().get("global");

        StringTerms popularTagsAgg = global.getAggregations().get("popularTags");
        return popularTagsAgg.getBuckets().stream()
                .map(bucket -> {
                    String tagValue = bucket.getKey();
                    long count = bucket.getDocCount();
                    return new Tag(tagValue, count);
                })
                .collect(Collectors.toList());
    }
}
