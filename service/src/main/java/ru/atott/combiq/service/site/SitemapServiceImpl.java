package ru.atott.combiq.service.site;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.Types;
import ru.atott.combiq.dao.entity.PostEntity;
import ru.atott.combiq.dao.entity.QuestionEntity;
import ru.atott.combiq.dao.es.NameVersionDomainResolver;
import ru.atott.combiq.dao.repository.QuestionnaireRepository;
import ru.atott.combiq.service.ServiceException;
import ru.atott.combiq.service.bean.Question;
import ru.atott.combiq.service.bean.QuestionnaireHead;
import ru.atott.combiq.service.mapper.QuestionMapper;
import ru.atott.combiq.service.mapper.QuestionnaireHeadMapper;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SitemapServiceImpl implements SitemapService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private NameVersionDomainResolver domainResolver;

    public void generateSitemap(OutputStream outputStream, UrlResolver urlResolver, List<SitemapEntry> staticEntries) {
        try {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(outputStream, "utf-8");
            writer.writeStartDocument();
            writeUrlset(writer, urlResolver, staticEntries);
            writer.writeEndDocument();
            writer.flush();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void writeUrlset(XMLStreamWriter writer, UrlResolver urlResolver, List<SitemapEntry> staticEntries) throws Exception {
        writer.setDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");
        writer.writeStartElement("urlset");
        writer.writeDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");

        // Static entries.
        if (staticEntries != null) {
            staticEntries.forEach(entry -> writeEntry(writer, entry));
        }

        // Questions.
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withIndices(domainResolver.resolveQuestionIndex())
                .withTypes(Types.question)
                .build();

        String scrollId = elasticsearchOperations.scan(searchQuery, TimeUnit.MINUTES.toMillis(1), false);
        boolean hasRecords = true;

        while (hasRecords){
            Page<QuestionEntity> page = elasticsearchOperations.scroll(scrollId, TimeUnit.SECONDS.toMillis(10), QuestionEntity.class);

            if (page != null && page.hasContent()) {
                page.getContent().stream().forEach(entity -> writeQuestion(writer, entity, urlResolver));
            } else {
                hasRecords = false;
            }
        }

        // Questionnaires.
        QuestionnaireHeadMapper<QuestionnaireHead> questionnaireHeadMapper = new QuestionnaireHeadMapper<>(QuestionnaireHead.class);
        List<QuestionnaireHead> questionnaireHeads = questionnaireHeadMapper.toList(questionnaireRepository.findAll());
        questionnaireHeads.forEach(questionnaireHead -> writeQuestionnaire(writer, questionnaireHead, urlResolver));

        // Posts.
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withIndices(domainResolver.resolveQuestionIndex())
                .withTypes(Types.post)
                .build();

        scrollId = elasticsearchOperations.scan(searchQuery, TimeUnit.MINUTES.toMillis(1), false);
        hasRecords = true;

        while (hasRecords){
            Page<PostEntity> page = elasticsearchOperations.scroll(scrollId, TimeUnit.SECONDS.toMillis(10), PostEntity.class);

            if (page != null && page.hasContent()) {
                page.getContent().stream().forEach(entity -> writePost(writer, entity, urlResolver));
            } else {
                hasRecords = false;
            }
        }

        writer.writeEndElement();
    }

    private void writeEntry(XMLStreamWriter writer, SitemapEntry entry) {
        try {
            writer.writeStartElement("url");

            writeTextElement(writer, "loc", entry.getUrl());
            if (StringUtils.isNotBlank(entry.getPriority())) {
                writeTextElement(writer, "priority", entry.getPriority());
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void writeQuestionnaire(XMLStreamWriter writer, QuestionnaireHead questionnaireHead, UrlResolver urlResolver) {
        try {
            String priority = "0.5";

            writer.writeStartElement("url");

            writeTextElement(writer, "loc", urlResolver.externalize(urlResolver.getQuestionnaireUrl(questionnaireHead)));
            writeTextElement(writer, "priority", priority);


            writer.writeEndElement();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void writePost(XMLStreamWriter writer, PostEntity postEntity, UrlResolver urlResolver) {
        try {
            if (!postEntity.isPublished()) {
                return;
            }

            writer.writeStartElement("url");

            String postUrl = urlResolver.getPostUrl(postEntity.getId(), postEntity.getHumanUrlTitle());
            writeTextElement(writer, "loc", urlResolver.externalize(postUrl));
            writeTextElement(writer, "priority", "0.7");
            writer.writeEndElement();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void writeQuestion(XMLStreamWriter writer, QuestionEntity questionEntity, UrlResolver urlResolver) {
        try {
            if (questionEntity.isDeleted()) {
                return;
            }

            QuestionMapper questionMapper = new QuestionMapper();
            Question question = questionMapper.map(questionEntity);
            String priority = "0.5";
            if (question.isLanding()) {
                priority = "0.6";
            }

            writer.writeStartElement("url");

            writeTextElement(writer, "loc", urlResolver.externalize(urlResolver.getQuestionUrl(question)));
            writeTextElement(writer, "priority", priority);
            if(question.getLastModify() != null) {
                writeTextElement(writer, "lastmod", new SimpleDateFormat("yyyy-MM-dd").format(question.getLastModify()));
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void writeTextElement(XMLStreamWriter writer, String localName, String text) throws Exception {
        writer.writeStartElement(localName);
        writer.writeCharacters(text);
        writer.writeEndElement();
    }
}
