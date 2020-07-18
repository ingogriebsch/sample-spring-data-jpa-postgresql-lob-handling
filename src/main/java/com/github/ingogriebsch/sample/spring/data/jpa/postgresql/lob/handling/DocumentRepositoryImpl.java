package com.github.ingogriebsch.sample.spring.data.jpa.postgresql.lob.handling;

import java.io.InputStream;
import java.sql.Blob;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import lombok.NonNull;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

    @Autowired
    @NonNull
    private DocumentRepository documentRepository;

    @Autowired
    @NonNull
    private EntityManager entityManager;

    @Override
    @Transactional
    public Document save(@NonNull Document template, @NonNull InputStream source) {
        Session session = entityManager.unwrap(Session.class);
        Blob content = Hibernate.getLobCreator(session).createBlob(source, template.getContentLength());
        try {
            template.setContent(content);
            Document saved = documentRepository.save(template);

            // FIXME is this really a good idea? We need to make the call to be able to call Blob.free() but do we need to do it?
            // Or handles the framework the resource?
            entityManager.flush();
            return saved;
        } finally {
            BlobUtils.free(content);
        }
    }

    @Override
    @Transactional
    public boolean deleteIfExists(@NonNull String id) {
        Document document = documentRepository.findOne(id);
        if (document != null) {
            documentRepository.deleteContent(id);
            documentRepository.delete(document);
        }
        return document != null;
    }

}
