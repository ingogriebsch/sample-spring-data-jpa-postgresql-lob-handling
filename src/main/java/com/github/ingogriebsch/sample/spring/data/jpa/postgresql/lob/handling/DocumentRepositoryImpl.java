package com.github.ingogriebsch.sample.spring.data.jpa.postgresql.lob.handling;

import java.io.InputStream;
import java.sql.Blob;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.NonNull;

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

            // FIXME is this really a good idea? We need it to be able to call Blob.free() but do we need to do it? Or handles the
            // framework the resource?
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
        // FIXME This is probably not enough to get the content behind the entry deleted as well!
        if (document != null) {
            documentRepository.delete(document);
        }
        return document != null;
    }
}
